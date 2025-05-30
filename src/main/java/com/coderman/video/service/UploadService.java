package com.coderman.video.service;

import com.baomidou.mybatisplus.core.toolkit.Wrappers;
import com.coderman.video.config.UploadConfig;
import com.coderman.video.enums.UploadStatusEnum;
import com.coderman.video.mapper.UploadTaskMapper;
import com.coderman.video.model.SysFile;
import com.coderman.video.model.UploadTask;
import com.coderman.video.request.UploadCheckRequest;
import com.coderman.video.request.UploadInitRequest;
import com.coderman.video.request.UploadMergeRequest;
import com.coderman.video.request.UploadPartRequest;
import com.coderman.video.utils.SecurityUtils;
import com.coderman.video.vo.UploadCheckVO;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.lang3.time.DateUtils;
import org.springframework.util.Assert;

import javax.annotation.Resource;
import java.io.IOException;
import java.util.Date;
import java.util.List;

@Slf4j
public abstract class UploadService {

    @Resource
    private SysFileService sysFileService;
    @Resource
    private UploadTaskMapper uploadTaskMapper;
    @Resource
    private UploadConfig uploadConfig;

    /**
     * 检查上传任务是否已存在（用于秒传、断点续传判断）。
     *
     * @param request 上传检查请求，包含 fileHash、fileName 等信息
     * @return UploadCheckVO 返回是否需要上传、已上传分片等信息
     */
    public UploadCheckVO checkUpload(UploadCheckRequest request) {

        String fileHash = request.getFileHash();
        Long fileSize = request.getFileSize();

        Assert.hasText(fileHash, "文件Hash不能为空");
        Assert.notNull(fileSize, "文件大小不能为空");

        // 查找已经上传完成的文件（状态 = 3）
        SysFile sysFile = this.sysFileService.selectByHash(fileHash);
        UploadCheckVO resp = new UploadCheckVO(false, "", "", 1);
        if (sysFile != null) {
            resp.setIsFastUpload(true);
            resp.setFileUrl(sysFile.getFileUrl());
            return resp;
        }

        // 2. 检查是否有未完成的上传任务
        UploadTask unfinishedTask = uploadTaskMapper.selectOne(
                Wrappers.<UploadTask>lambdaQuery()
                        .eq(UploadTask::getFileHash, fileHash)
                        .eq(UploadTask::getFileSize, fileSize)
                        .eq(UploadTask::getUserId, SecurityUtils.getUserId())
                        .in(UploadTask::getStatus,
                                UploadStatusEnum.INIT.getCode(),
                                UploadStatusEnum.UPLOADING.getCode())
                        .orderByDesc(UploadTask::getId)
                        .last("limit 1")
        );
        if (unfinishedTask != null) {
            resp.setIsFastUpload(false);
            resp.setUploadId(unfinishedTask.getUploadId());
            resp.setNextPartNumber(unfinishedTask.getPartNumber() + 1);
            return resp;
        }

        return resp;
    }

    /**
     * 初始化上传任务，生成并返回 uploadId，用于后续分片上传标识。
     *
     * @param request 上传初始化请求，包含 fileHash、fileName、总大小等参数
     * @return String 返回上传任务 ID（uploadId）
     */
    public abstract String initUpload(UploadInitRequest request);

    /**
     * 上传单个分片数据。
     *
     * @param request 上传分片请求，包含 uploadId、分片索引、分片数据等
     * @throws IOException 分片写入过程中可能发生的 IO 异常
     */
    public abstract void uploadPart(UploadPartRequest request) throws IOException;

    /**
     * 合并所有已上传分片，生成最终完整文件。
     *
     * @param request 合并请求，包含 uploadId、分片数量、目标路径等
     * @return String 返回最终生成文件的 URL 或本地路径
     * @throws IOException 合并过程中可能发生的 IO 异常
     */
    public abstract String mergeUpload(UploadMergeRequest request) throws IOException;

    /**
     * 执行指定上传任务的数据清理操作。
     * <p>
     * 具体清理内容由子类实现，例如：
     * - 删除临时上传分片
     * - 清理元数据记录
     * - 清除缓存/占用的资源
     *
     * @param task 待清理的上传任务
     */
    protected void doClean(UploadTask task) {
        // do nothing
    }


    /**
     * 执行上传任务数据的定时清理
     * <p>
     * 清理逻辑说明：
     * - 找出在配置过期时间（expireMinutes）之前创建，且仍处于 INIT 或 UPLOADING 状态的任务；
     * - 执行对应的清理操作（doClean）；
     * - 将这些未完成的任务状态标记为 FAILED。
     */
    public void scheduleUploadDataCleanup() {

        int expireMinutes = uploadConfig.getExpireMinutes();

        // 计算过期时间点（当前时间减去配置的过期分钟数）
        Date expireTime = DateUtils.addMinutes(new Date(), -expireMinutes);
        log.info("开始清理 {} 分钟前未完成的上传任务", expireMinutes);

        // 查询超时未完成的上传任务（INIT / UPLOADING 且创建时间早于 expireTime）
        List<UploadTask> expiredTasks = uploadTaskMapper.selectList(
                Wrappers.<UploadTask>lambdaQuery()
                        .in(UploadTask::getStatus,
                                UploadStatusEnum.INIT.getCode(),
                                UploadStatusEnum.UPLOADING.getCode())
                        .lt(UploadTask::getCreatedAt, expireTime)
        );

        // 遍历清理每个过期任务
        for (UploadTask task : expiredTasks) {

            // 执行清理操作（具体实现由子类定义）
            this.doClean(task);

            // 更新任务状态为 FAILED，标识上传失败
            UploadTask update = new UploadTask();
            update.setId(task.getId());
            update.setStatus(UploadStatusEnum.FAILED.getCode());
            uploadTaskMapper.updateById(update);
        }
    }

}

package com.coderman.video.service.impl;

import com.baomidou.mybatisplus.core.toolkit.Wrappers;
import com.coderman.video.enums.UploadStatusEnum;
import com.coderman.video.mapper.UploadTaskMapper;
import com.coderman.video.model.UploadTask;
import com.coderman.video.request.UploadCheckRequest;
import com.coderman.video.request.UploadInitRequest;
import com.coderman.video.request.UploadMergeRequest;
import com.coderman.video.request.UploadPartRequest;
import com.coderman.video.service.UploadService;
import com.coderman.video.utils.FileUtils;
import com.coderman.video.utils.SecurityUtils;
import com.coderman.video.vo.UploadCheckVO;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.util.Assert;
import org.springframework.web.multipart.MultipartFile;

import javax.annotation.Resource;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.IOException;
import java.nio.file.Paths;
import java.util.UUID;

/**
 * @author ：zhangyukang
 * @date ：2025/05/28 15:45
 */
@Service
@Slf4j
public class UploadServiceImpl implements UploadService {

    @Resource
    private UploadTaskMapper uploadTaskMapper;

    @Value("${upload.base-dir:F:}")
    private String baseUploadPath;

    @Override
    @Transactional(rollbackFor = Exception.class, timeout = 5)
    public String uploadInit(UploadInitRequest request) {

        String fileHash = request.getFileHash();
        String fileName = request.getFileName();
        Integer totalParts = request.getTotalParts();
        Long fileSize = request.getFileSize();

        // 参数校验
        Assert.hasText(fileHash, "文件Hash不能为空");
        Assert.hasText(fileName, "文件名不能为空");
        Assert.notNull(totalParts, "分片总数不能为空");
        Assert.isTrue(totalParts > 0, "分片总数必须大于0");
        Assert.notNull(fileSize, "文件大小不能为空");
        Assert.isTrue(fileSize > 0, "文件大小必须大于0");

        // 生成上传任务
        UploadTask newTask = new UploadTask();
        String uploadId = UUID.randomUUID().toString().replace("-", "");

        newTask.setUploadId(uploadId);
        newTask.setFileHash(fileHash);
        newTask.setFileName(fileName);
        newTask.setFileType(FileUtils.getFileType(fileName));
        newTask.setFilePath(FileUtils.genFilePath(fileName, FileUtils.FileModuleEnum.COMMON_MODULE, fileHash));
        newTask.setFileSize(fileSize);
        newTask.setTotalParts(totalParts);
        newTask.setPartIndex(0);
        newTask.setStatus(0);
        newTask.setUserId(SecurityUtils.getUserId());
        this.uploadTaskMapper.insert(newTask);
        return uploadId;
    }


    @Override
    @Transactional(rollbackFor = Exception.class, timeout = 10)
    public void uploadPart(UploadPartRequest uploadPartRequest) throws IOException {
        MultipartFile file = uploadPartRequest.getFile();
        String fileHash = uploadPartRequest.getFileHash();
        String uploadId = uploadPartRequest.getUploadId();
        String fileName = uploadPartRequest.getFileName();
        Integer partIndex = uploadPartRequest.getPartIndex();

        // 1. 参数校验
        Assert.notNull(file, "分片文件不能为空");
        Assert.hasText(fileHash, "文件Hash不能为空");
        Assert.hasText(uploadId, "上传任务ID不能为空");
        Assert.hasText(fileName, "文件名不能为空");
        Assert.notNull(partIndex, "分片索引不能为空");

        // 2. 构建保存路径
        String baseDir = Paths.get(baseUploadPath, "videos", uploadId).toString();
        File dir = new File(baseDir);
        if (!dir.exists() && !dir.mkdirs()) {
            throw new IOException("创建上传目录失败：" + baseDir);
        }

        File partFile = new File(dir, partIndex + ".part");
        if (partFile.exists()) {
            log.warn("分片文件已存在，uploadId={}, partIndex={}", uploadId, partIndex);
            return;
        }

        // 3. 保存文件
        file.transferTo(partFile);

        // 4. 更新数据
        int rowCount = this.uploadTaskMapper.update(null,
                Wrappers.<UploadTask>lambdaUpdate()
                        .setSql("status = " + UploadStatusEnum.UPLOADING.getCode())
                        .setSql("part_index = part_index + 1")
                        .eq(UploadTask::getUploadId, uploadId)
        );
        if(rowCount > 0){
            log.info("保存分片成功: uploadId={}, partIndex={}, path={}", uploadId, partIndex, partFile.getAbsolutePath());
        }
    }

    @Override
    public UploadCheckVO uploadCheck(UploadCheckRequest request) {

        String fileHash = request.getFileHash();
        Long fileSize = request.getFileSize();

        Assert.hasText(fileHash, "文件Hash不能为空");
        Assert.notNull(fileSize, "文件大小不能为空");

        // 查找已经上传完成的文件（状态 = 3）
        UploadTask exist = uploadTaskMapper.selectOne(
                Wrappers.<UploadTask>lambdaQuery()
                        .eq(UploadTask::getFileHash, fileHash)
                        .eq(UploadTask::getStatus, UploadStatusEnum.MERGED.getCode())
                        .eq(UploadTask::getFileSize, fileSize)
                        .orderByDesc(UploadTask::getId)
                        .last("limit 1")
        );

        UploadCheckVO resp = new UploadCheckVO(false , "", "", 0);
        if (exist != null) {
            resp.setIsFastUpload(true);
            resp.setFilePath(exist.getFilePath());
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
        if(unfinishedTask !=null){
            resp.setIsFastUpload(false);
            resp.setUploadId(unfinishedTask.getUploadId());
            resp.setNextPartIndex(unfinishedTask.getPartIndex() + 1);
            return resp;
        }

        return resp;

    }

    @Override
    @Transactional(rollbackFor = Exception.class, timeout = 30)
    public void uploadMerge(UploadMergeRequest uploadMergeRequest) throws IOException {

        String uploadId = uploadMergeRequest.getUploadId();

        // 1. 参数校验
        Assert.hasText(uploadId, "上传任务ID不能为空");

        // 2. 获取上传任务信息
        UploadTask uploadTask = uploadTaskMapper.selectOne(
                Wrappers.<UploadTask>lambdaQuery()
                        .eq(UploadTask::getUploadId, uploadId)
                        .eq(UploadTask::getUserId, SecurityUtils.getUserId())
        );

        if (uploadTask == null) {
            throw new IllegalArgumentException("上传任务不存在或不属于当前用户");
        }

        // 3. 检查是否所有分片已上传完成
        if (uploadTask.getPartIndex() < uploadTask.getTotalParts()) {
            throw new IllegalStateException("还有分片未上传完成，无法合并");
        }

        // 4. 准备文件路径
        String baseDir = Paths.get(baseUploadPath, "videos", uploadId).toString();
        String finalFilePath = Paths.get(baseUploadPath, uploadTask.getFilePath()).toString();

        // 创建最终文件的父目录
        File finalFile = new File(finalFilePath);
        File parentDir = finalFile.getParentFile();
        if (!parentDir.exists() && !parentDir.mkdirs()) {
            throw new IOException("创建最终文件目录失败：" + parentDir.getAbsolutePath());
        }

        // 5. 合并文件
        try (FileOutputStream fos = new FileOutputStream(finalFile, true)) {
            for (int i = 1; i <= uploadTask.getTotalParts(); i++) {
                File partFile = new File(baseDir, i + ".part");
                if (!partFile.exists()) {
                    throw new IOException("分片文件缺失: " + partFile.getAbsolutePath());
                }

                try (FileInputStream fis = new FileInputStream(partFile)) {
                    byte[] buffer = new byte[8192];
                    int bytesRead;
                    while ((bytesRead = fis.read(buffer)) != -1) {
                        fos.write(buffer, 0, bytesRead);
                    }
                }

                // 删除已合并的分片文件
                if (!partFile.delete()) {
                    log.warn("删除分片文件失败: {}", partFile.getAbsolutePath());
                }
            }
        }

        // 6. 删除临时目录
        File tempDir = new File(baseDir);
        if (tempDir.exists() && !tempDir.delete()) {
            log.warn("删除临时目录失败: {}", tempDir.getAbsolutePath());
        }

        // 7. 更新任务状态为已完成
        uploadTask.setStatus(UploadStatusEnum.MERGED.getCode());
        uploadTaskMapper.updateById(uploadTask);

        log.info("文件合并成功: uploadId={}, filePath={}", uploadId, finalFilePath);
    }

}

package com.coderman.video.service.impl;

import com.baomidou.mybatisplus.core.toolkit.Wrappers;
import com.coderman.video.config.UploadConfig;
import com.coderman.video.enums.UploadStatusEnum;
import com.coderman.video.mapper.UploadTaskMapper;
import com.coderman.video.model.SysFile;
import com.coderman.video.model.UploadTask;
import com.coderman.video.request.UploadInitRequest;
import com.coderman.video.request.UploadMergeRequest;
import com.coderman.video.request.UploadPartRequest;
import com.coderman.video.service.SysFileService;
import com.coderman.video.service.UploadService;
import com.coderman.video.utils.FileUtils;
import com.coderman.video.utils.SecurityUtils;
import com.google.common.collect.Lists;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.lang3.StringUtils;
import org.springframework.boot.autoconfigure.condition.ConditionalOnProperty;
import org.springframework.stereotype.Component;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.util.Assert;
import org.springframework.web.multipart.MultipartFile;

import javax.annotation.Resource;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.IOException;
import java.nio.file.Paths;
import java.util.*;
import java.util.stream.Collectors;

/**
 * @author ：zhangyukang
 * @date ：2025/05/28 15:45
 */
@Slf4j
@Component("localUploadService")
@ConditionalOnProperty(name = "upload.strategy", havingValue = "local")
public class LocalUploadService extends UploadService {

    @Resource
    private UploadTaskMapper uploadTaskMapper;
    @Resource
    private UploadConfig uploadConfig;
    @Resource
    private SysFileService sysFileService;

    @Override
    public String initUpload(UploadInitRequest request) {

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
        String uploadId = UUID.randomUUID().toString().replace("-", "").toUpperCase();

        newTask.setUploadId(uploadId);
        newTask.setFileHash(fileHash);
        newTask.setFileName(fileName);
        newTask.setFileType(FileUtils.getFileType(fileName));
        newTask.setFilePath(FileUtils.genFilePath(fileName, FileUtils.FileModuleEnum.COMMON_MODULE));
        newTask.setFileSize(fileSize);
        newTask.setTotalParts(totalParts);
        newTask.setPartNumber(0);
        newTask.setStatus(0);
        newTask.setUserId(SecurityUtils.getUserId());
        this.uploadTaskMapper.insert(newTask);
        return uploadId;
    }


    @Override
    public void uploadPart(UploadPartRequest uploadPartRequest) throws IOException {
        MultipartFile file = uploadPartRequest.getFile();
        String fileHash = uploadPartRequest.getFileHash();
        String uploadId = uploadPartRequest.getUploadId();
        String fileName = uploadPartRequest.getFileName();
        Integer partNumber = uploadPartRequest.getPartNumber();
        String baseUploadPath = uploadConfig.getLocal().getBaseUploadPath();

        // 1. 参数校验
        Assert.notNull(file, "分片文件不能为空");
        Assert.hasText(fileHash, "文件Hash不能为空");
        Assert.hasText(uploadId, "上传任务ID不能为空");
        Assert.hasText(fileName, "文件名不能为空");
        Assert.notNull(partNumber, "分片索引不能为空");

        // 2. 构建保存路径
        String baseDir = Paths.get(baseUploadPath, "tmp", uploadId).toString();
        File dir = new File(baseDir);
        if (!dir.exists() && !dir.mkdirs()) {
            throw new IOException("创建上传目录失败：" + baseDir);
        }

        File partFile = new File(dir, partNumber + ".part");
        if (partFile.exists()) {
            log.warn("分片文件已存在，uploadId={}, partNumber={}", uploadId, partNumber);
            return;
        }

        // 3. 保存文件
        file.transferTo(partFile);

        // 4. 更新数据
        int rowCount = this.uploadTaskMapper.update(null, Wrappers.<UploadTask>lambdaUpdate()
                .setSql("status = " + UploadStatusEnum.UPLOADING.getCode())
                .setSql("part_number = part_number + 1")
                .eq(UploadTask::getUploadId, uploadId)
        );
        if (rowCount > 0) {
            log.info("保存分片成功: uploadId={}, partNumber={}, path={}", uploadId, partNumber, partFile.getAbsolutePath());
        }
    }

    @Override
    @Transactional(timeout = 30)
    public String mergeUpload(UploadMergeRequest uploadMergeRequest) throws IOException{

        String uploadId = uploadMergeRequest.getUploadId();
        String baseUploadPath = uploadConfig.getLocal().getBaseUploadPath();
        String localDomain = uploadConfig.getLocal().getDomain();
        UploadTask uploadTask;

        try {
            // 1. 参数校验
            Assert.hasText(uploadId, "上传任务ID不能为空");

            // 2. 获取上传任务信息
            uploadTask = uploadTaskMapper.selectOne(
                    Wrappers.<UploadTask>lambdaQuery()
                            .eq(UploadTask::getUploadId, uploadId)
                            .eq(UploadTask::getUserId, SecurityUtils.getUserId())
            );

            if (uploadTask == null) {
                throw new IllegalArgumentException("上传任务不存在或不属于当前用户");
            }

            // 3. 检查是否所有分片已上传完成
            if (uploadTask.getPartNumber() < uploadTask.getTotalParts()) {
                throw new IllegalStateException("还有分片未上传完成，无法合并");
            }

            // 4. 准备文件路径
            String baseDir = Paths.get(baseUploadPath, "tmp", uploadId).toString();
            String finalFilePath = Paths.get(baseUploadPath, uploadTask.getFilePath()).toString();

            File finalFile = new File(finalFilePath);
            File parentDir = finalFile.getParentFile();
            if (!parentDir.exists() && !parentDir.mkdirs()) {
                throw new IOException("创建最终文件目录失败：" + parentDir.getAbsolutePath());
            }

            // 5. 合并分片文件
            File[] partFileArray = new File(baseDir).listFiles((dir, name) -> name.endsWith(".part"));
            if (partFileArray == null || partFileArray.length != uploadTask.getTotalParts()) {
                throw new IOException("分片文件数量不匹配");
            }

            // 按照文件名中的数字部分排序（无论是0开始还是1开始都能合并）
            List<File> partFiles = Arrays.stream(partFileArray)
                    .sorted(Comparator.comparingInt(f -> Integer.parseInt(f.getName().replace(".part", ""))))
                    .collect(Collectors.toList());
            List<String> hashList = Lists.newArrayList();
            try (FileOutputStream fos = new FileOutputStream(finalFile, true)) {
                for (File partFile : partFiles) {
                    try (FileInputStream fis = new FileInputStream(partFile)) {
                        byte[] buffer = new byte[8192];
                        int bytesRead;
                        while ((bytesRead = fis.read(buffer)) != -1) {
                            fos.write(buffer, 0, bytesRead);
                        }
                    }
                    String hash = FileUtils.computeMD5(partFile);
                    hashList.add(hash);
                }
            }

            // 6. 删除所有分片文件
            boolean allDeleted = true;
            for (File partFile : partFiles) {
                if (!partFile.delete()) {
                    allDeleted = false;
                    log.warn("删除分片文件失败: {}", partFile.getAbsolutePath());
                }
            }

            // 7. 如果所有分片删除成功，尝试删除分片目录
            File tempDir = new File(baseDir);
            if (allDeleted && tempDir.exists()) {
                File[] remainingFiles = tempDir.listFiles();
                if (remainingFiles != null && remainingFiles.length == 0) {
                    if (!tempDir.delete()) {
                        log.warn("删除临时目录失败: {}", tempDir.getAbsolutePath());
                    }
                } else {
                    log.warn("临时目录不为空，跳过删除: {}", tempDir.getAbsolutePath());
                }
            }

            // 8. 校验文件完整性
            String hashValue = FileUtils.calHash(hashList);
            if (!StringUtils.equals(hashValue, uploadTask.getFileHash())) {
                throw new IllegalStateException("文件丢失上传失败");
            }


            // 9. 更新任务状态为已完成
            UploadTask update = new UploadTask();
            update.setId(uploadTask.getId());
            update.setStatus(UploadStatusEnum.MERGED.getCode());
            uploadTaskMapper.updateById(update);
            log.info("文件合并成功: uploadId={}, filePath={}", uploadId, finalFilePath);

            // 10. 写入文件表
            SysFile file = this.sysFileService.selectByHash(uploadTask.getFileHash());
            if (file == null) {
                file = new SysFile();
                file.setFileHash(uploadTask.getFileHash());
                file.setFileName(uploadTask.getFileName());
                file.setFileType(uploadTask.getFileType());
                file.setFileSize(uploadTask.getFileSize());
                file.setFileUrl(localDomain + "/" + uploadTask.getFilePath());
                file.setFilePath(uploadTask.getFilePath());
                file.setStorageType(uploadConfig.getStrategy());
                file.setStatus(1);
                file.setCreatedAt(new Date());
                file.setUpdatedAt(new Date());
                this.sysFileService.save(file);
            }
            return file.getFileUrl();

        } catch (Exception e) {

            log.error("上传文件失败:{}", e.getMessage(), e);
            throw new IOException("上传文件失败:" + e.getMessage());
        }

    }

    @Override
    protected void doClean(UploadTask task) {
        String baseUploadPath = uploadConfig.getLocal().getBaseUploadPath();
        String uploadId = task.getUploadId();
        File tmpDir = Paths.get(baseUploadPath, "tmp", uploadId).toFile();
        if (tmpDir.exists()) {
            boolean success = deleteDirectory(tmpDir);
            if (success) {
                log.info("删除临时目录成功: {}", tmpDir.getAbsolutePath());
            } else {
                log.warn("删除临时目录失败: {}", tmpDir.getAbsolutePath());
            }
        }
    }

    private boolean deleteDirectory(File dir) {
        if (!dir.exists()) return false;
        File[] files = dir.listFiles();
        if (files != null) {
            for (File file : files) {
                if (!file.delete()) {
                    log.warn("删除文件失败: {}", file.getAbsolutePath());
                }
            }
        }
        return dir.delete();
    }


}

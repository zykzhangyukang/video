package com.coderman.video.service.impl;

import com.aliyun.oss.ClientConfiguration;
import com.aliyun.oss.OSSClient;
import com.aliyun.oss.common.auth.DefaultCredentialProvider;
import com.aliyun.oss.model.*;
import com.baomidou.mybatisplus.core.toolkit.Wrappers;
import com.coderman.video.config.UploadConfig;
import com.coderman.video.enums.UploadStatusEnum;
import com.coderman.video.mapper.UploadPartMapper;
import com.coderman.video.mapper.UploadTaskMapper;
import com.coderman.video.model.SysFile;
import com.coderman.video.model.UploadPart;
import com.coderman.video.model.UploadTask;
import com.coderman.video.request.UploadInitRequest;
import com.coderman.video.request.UploadMergeRequest;
import com.coderman.video.request.UploadPartRequest;
import com.coderman.video.service.SysFileService;
import com.coderman.video.service.UploadService;
import com.coderman.video.utils.FileUtils;
import com.coderman.video.utils.SecurityUtils;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.boot.autoconfigure.condition.ConditionalOnProperty;
import org.springframework.stereotype.Component;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.util.Assert;
import org.springframework.web.multipart.MultipartFile;

import javax.annotation.PostConstruct;
import javax.annotation.Resource;
import java.io.IOException;
import java.util.Comparator;
import java.util.Date;
import java.util.List;
import java.util.stream.Collectors;

@Slf4j
@Component("ossUploadStrategy")
@ConditionalOnProperty(name = "upload.strategy", havingValue = "oss")
public class OssUploadService extends UploadService {

    @Value(value = "${spring.application.name:}")
    private String applicationName;
    @Resource
    private UploadConfig uploadConfig;
    @Resource
    private UploadTaskMapper uploadTaskMapper;
    @Resource
    private UploadPartMapper uploadPartMapper;
    @Resource
    private SysFileService sysFileService;

    private OSSClient ossClient;

    @PostConstruct
    public void initOssClient() {
        if (ossClient == null) {
            UploadConfig.Oss config = uploadConfig.getOss();
            ClientConfiguration clientConf = new ClientConfiguration();
            clientConf.setMaxConnections(1024);
            clientConf.setSocketTimeout(50000);
            clientConf.setConnectionTimeout(50000);

            ossClient = new OSSClient(
                    config.getEndpoint(),
                    new DefaultCredentialProvider(config.getAccessKeyId(), config.getAccessKeySecret()),
                    clientConf
            );
        }
    }

    @Override
    public String initUpload(UploadInitRequest request) {

        String fileHash = request.getFileHash();
        String fileName = request.getFileName();
        Integer totalParts = request.getTotalParts();
        Long fileSize = request.getFileSize();

        // 参数校验
        Assert.hasText(fileHash, "文件Hash不能为空!");
        Assert.hasText(fileName, "文件名不能为空!");
        Assert.notNull(totalParts, "分片总数不能为空!");
        Assert.isTrue(totalParts > 0, "分片总数必须大于0!");
        Assert.notNull(fileSize, "文件大小不能为空!");
        Assert.isTrue(fileSize > 0, "文件大小必须大于0!");

        UploadConfig.Oss config = uploadConfig.getOss();

        // OSS中的文件路径，通常包含目录结构，可根据需求组合
        String objectName = FileUtils.genFilePath(applicationName, fileName, FileUtils.FileModuleEnum.COMMON_MODULE);
        InitiateMultipartUploadRequest uploadRequest = new InitiateMultipartUploadRequest(config.getBucketName(), objectName);
        InitiateMultipartUploadResult uploadResult = ossClient.initiateMultipartUpload(uploadRequest);
        log.info("OSS 分片上传初始化：objectName={}, ossUploadId={}", objectName, uploadResult.getUploadId());

        // 生成上传任务
        UploadTask newTask = new UploadTask();
        newTask.setUploadId(uploadResult.getUploadId());
        newTask.setFileHash(fileHash);
        newTask.setFileName(fileName);
        newTask.setFileType(FileUtils.getFileType(fileName));
        newTask.setFilePath(objectName);
        newTask.setFileSize(fileSize);
        newTask.setTotalParts(totalParts);
        newTask.setPartNumber(0);
        newTask.setStatus(0);
        newTask.setUserId(SecurityUtils.getUserId());
        this.uploadTaskMapper.insert(newTask);
        return uploadResult.getUploadId();
    }

    @Override
    @Transactional(timeout = 30)
    public void uploadPart(UploadPartRequest request) throws IOException {

        MultipartFile file = request.getFile();
        String fileHash = request.getFileHash();
        String uploadId = request.getUploadId();
        String fileName = request.getFileName();
        Integer partNumber = request.getPartNumber();
        String bucketName = uploadConfig.getOss().getBucketName();

        // 1. 参数校验
        Assert.notNull(file, "分片文件不能为空");
        Assert.hasText(fileHash, "文件Hash不能为空");
        Assert.hasText(uploadId, "上传任务ID不能为空");
        Assert.hasText(fileName, "文件名不能为空");
        Assert.notNull(partNumber, "分片索引不能为空");

        UploadTask uploadTask = this.uploadTaskMapper.selectOne(Wrappers.<UploadTask>lambdaQuery()
                .eq(UploadTask::getUploadId, uploadId)
                .last("limit 1"));
        if (uploadTask == null) {
            throw new IllegalArgumentException("上传任务不存在");
        }

        com.aliyun.oss.model.UploadPartRequest req = new com.aliyun.oss.model.UploadPartRequest();
        req.setBucketName(bucketName);
        req.setKey(uploadTask.getFilePath());
        req.setUploadId(uploadId);
        req.setInputStream(file.getInputStream());
        req.setPartSize(file.getSize());
        req.setPartNumber(partNumber);
        UploadPartResult uploadPartResult = ossClient.uploadPart(req);

        // 4. 更新数据
        this.uploadTaskMapper.update(null, Wrappers.<UploadTask>lambdaUpdate()
                .setSql("status = " + UploadStatusEnum.UPLOADING.getCode())
                .setSql("part_number = part_number + 1")
                .eq(UploadTask::getUploadId, uploadId)
        );

        UploadPart entity = new UploadPart();
        entity.setPartNumber(partNumber);
        entity.setTag(uploadPartResult.getETag());
        entity.setCreatedAt(new Date());
        entity.setUploadId(uploadTask.getUploadId());
        this.uploadPartMapper.insert(entity);
        log.info("保存分片成功: uploadId={}, partNumber={}, eTag={}", uploadId, partNumber, uploadPartResult.getETag());
    }

    @Override
    @Transactional(timeout = 30)
    public String mergeUpload(UploadMergeRequest request) throws IOException {
        String bucketName = uploadConfig.getOss().getBucketName();
        String uploadId = request.getUploadId();
        UploadTask uploadTask;

        try {
            // 1. 参数校验
            Assert.hasText(uploadId, "上传任务ID不能为空!");

            // 2. 获取上传任务信息
            uploadTask = uploadTaskMapper.selectOne(
                    Wrappers.<UploadTask>lambdaQuery()
                            .eq(UploadTask::getUploadId, uploadId)
                            .eq(UploadTask::getUserId, SecurityUtils.getUserId())
            );
            if (uploadTask == null) {
                throw new IllegalArgumentException("上传任务不存在或不属于当前用户");
            }

            // 3. 读取分片信息
            List<UploadPart> uploadParts = this.uploadPartMapper.selectList(Wrappers.<UploadPart>lambdaQuery()
                    .eq(UploadPart::getUploadId, uploadId));

            List<PartETag> partETags = uploadParts.stream()
                    .map(e -> new PartETag(e.getPartNumber(), e.getTag()))
                    .sorted(Comparator.comparingInt(PartETag::getPartNumber))
                    .collect(Collectors.toList());

            // 4. 合并分片
            CompleteMultipartUploadRequest req = new CompleteMultipartUploadRequest(bucketName,
                    uploadTask.getFilePath(), uploadId, partETags);
            CompleteMultipartUploadResult uploadResult = ossClient.completeMultipartUpload(req);

            // 5. 更新任务状态为已完成
            UploadTask update = new UploadTask();
            update.setId(uploadTask.getId());
            update.setStatus(UploadStatusEnum.MERGED.getCode());
            uploadTaskMapper.updateById(update);
            log.info("文件合并成功: uploadId={}, fileUrl={}", uploadId, uploadResult.getLocation());

            // 6. 写入文件表
            SysFile file = this.sysFileService.selectByHash(uploadTask.getFileHash());
            if (file == null) {
                file = new SysFile();
                file.setFileHash(uploadTask.getFileHash());
                file.setFileName(uploadTask.getFileName());
                file.setFileType(uploadTask.getFileType());
                file.setFileSize(uploadTask.getFileSize());
                file.setFileUrl(uploadResult.getLocation());
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
            throw new IOException("上传文件失败 :" + e.getMessage());
        }
    }
}
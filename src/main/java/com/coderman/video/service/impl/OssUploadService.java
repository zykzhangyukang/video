package com.coderman.video.service.impl;

import com.aliyun.oss.ClientConfiguration;
import com.aliyun.oss.OSSClient;
import com.aliyun.oss.common.auth.DefaultCredentialProvider;
import com.aliyun.oss.model.InitiateMultipartUploadRequest;
import com.aliyun.oss.model.InitiateMultipartUploadResult;
import com.coderman.video.config.UploadConfig;
import com.coderman.video.mapper.UploadTaskMapper;
import com.coderman.video.model.UploadTask;
import com.coderman.video.request.UploadInitRequest;
import com.coderman.video.request.UploadMergeRequest;
import com.coderman.video.request.UploadPartRequest;
import com.coderman.video.service.UploadService;
import com.coderman.video.utils.FileUtils;
import com.coderman.video.utils.SecurityUtils;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.boot.autoconfigure.condition.ConditionalOnProperty;
import org.springframework.stereotype.Component;
import org.springframework.util.Assert;

import javax.annotation.PostConstruct;
import javax.annotation.Resource;
import java.io.IOException;

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
        newTask.setPartIndex(0);
        newTask.setStatus(0);
        newTask.setUserId(SecurityUtils.getUserId());
        this.uploadTaskMapper.insert(newTask);
        return uploadResult.getUploadId();
    }

    @Override
    public void uploadPart(UploadPartRequest request) throws IOException {
    }

    @Override
    public String mergeUpload(UploadMergeRequest request) throws IOException {
        return null;
    }
}
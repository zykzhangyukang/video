package com.coderman.video.service;

import com.coderman.video.request.UploadInitRequest;
import com.coderman.video.request.UploadPartRequest;

import java.io.IOException;

public interface UploadService {
    /**
     * 初始化分片上传任务
     * @param request 请求参数
     * @return
     */
    String uploadInit(UploadInitRequest request);

    /**
     * 分片上传
     * @param uploadPartRequest 请求参数
     */
    void uploadPart(UploadPartRequest uploadPartRequest) throws IOException;
}

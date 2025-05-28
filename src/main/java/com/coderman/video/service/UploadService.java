package com.coderman.video.service;

import com.coderman.video.request.UploadCheckRequest;
import com.coderman.video.request.UploadInitRequest;
import com.coderman.video.request.UploadPartRequest;
import com.coderman.video.vo.UploadCheckVO;

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
    /**
     * 秒传接口
     * @param request 请求参数
     * @return
     */
    UploadCheckVO uploadCheck(UploadCheckRequest request);
}

package com.coderman.video.service.impl;

import com.coderman.video.request.UploadCheckRequest;
import com.coderman.video.request.UploadInitRequest;
import com.coderman.video.request.UploadMergeRequest;
import com.coderman.video.request.UploadPartRequest;
import com.coderman.video.service.UploadService;
import com.coderman.video.vo.UploadCheckVO;
import lombok.extern.slf4j.Slf4j;

import java.io.IOException;

@Slf4j
//@Component("ossUploadStrategy")
public class OssUploadService implements UploadService {

    @Override
    public UploadCheckVO checkUpload(UploadCheckRequest request) {
        return null;
    }

    @Override
    public String initUpload(UploadInitRequest request) {
        return null;
    }

    @Override
    public void uploadPart(UploadPartRequest request) throws IOException {

    }

    @Override
    public String mergeUpload(UploadMergeRequest request) throws IOException {
        return null;
    }
}

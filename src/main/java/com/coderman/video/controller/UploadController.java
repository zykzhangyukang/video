package com.coderman.video.controller;

import com.coderman.video.request.UploadCheckRequest;
import com.coderman.video.request.UploadInitRequest;
import com.coderman.video.request.UploadMergeRequest;
import com.coderman.video.request.UploadPartRequest;
import com.coderman.video.service.UploadService;
import com.coderman.video.utils.ResultUtil;
import com.coderman.video.vo.ResultVO;
import com.coderman.video.vo.UploadCheckVO;
import io.swagger.annotations.ApiModelProperty;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.ResponseBody;

import javax.annotation.Resource;
import java.io.IOException;

/**
 * @author ：zhangyukang
 * @date ：2025/05/29 16:25
 */
@Controller
public class UploadController {

    @Resource
    private UploadService uploadService;

    @ApiModelProperty(value = "分配上传任务id")
    @PostMapping(value = "/api/upload/check")
    @ResponseBody
    public ResultVO<UploadCheckVO> uploadCheck(@RequestBody UploadCheckRequest request) {
        UploadCheckVO uploadCheck = this.uploadService.uploadCheck(request);
        return ResultUtil.getSuccess(UploadCheckVO.class, uploadCheck);
    }

    @ApiModelProperty(value = "分配上传任务id")
    @PostMapping(value = "/api/upload/init")
    @ResponseBody
    public ResultVO<String> uploadInit(@RequestBody UploadInitRequest request) {
        String uploadId = this.uploadService.uploadInit(request);
        return ResultUtil.getSuccess(String.class, uploadId);
    }

    @ApiModelProperty(value = "上传分片")
    @PostMapping(value = "/api/upload/part")
    @ResponseBody
    public ResultVO<Void> uploadPart(UploadPartRequest uploadPartRequest) throws IOException {
        this.uploadService.uploadPart(uploadPartRequest);
        return ResultUtil.getSuccess();
    }

    @ApiModelProperty(value = "分片合并")
    @PostMapping(value = "/api/upload/merge")
    @ResponseBody
    public ResultVO<String> uploadMerge(@RequestBody UploadMergeRequest uploadMergeRequest) throws IOException {
        String src = this.uploadService.uploadMerge(uploadMergeRequest);
        return ResultUtil.getSuccess(String.class, src);
    }
}

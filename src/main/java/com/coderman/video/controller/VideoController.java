package com.coderman.video.controller;

import com.coderman.video.request.UploadInitRequest;
import com.coderman.video.request.UploadPartRequest;
import com.coderman.video.request.VideoPageRequest;
import com.coderman.video.service.CategoryService;
import com.coderman.video.service.UploadService;
import com.coderman.video.service.VideoService;
import com.coderman.video.utils.ResultUtil;
import com.coderman.video.vo.CategoryVO;
import com.coderman.video.vo.ResultVO;
import io.swagger.annotations.ApiModelProperty;
import io.swagger.annotations.ApiOperation;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.ResponseBody;

import javax.annotation.Resource;
import java.io.IOException;
import java.util.List;
import java.util.Map;

@Controller
public class VideoController {

    @Resource
    private VideoService videoService;
    @Resource
    private CategoryService categoryService;
    @Resource
    private UploadService uploadService;

    @ApiOperation(value = "视频发布页面", notes = "视频发布页面")
    @GetMapping(value = {"/publish"})
    public String videoPublishPage(Model model) {
        List<CategoryVO> videoCategories = this.categoryService.selectAllCategory();
        model.addAttribute("categories", videoCategories);
        return "publish";
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
    public ResultVO<Void> uploadMerge() {
        return ResultUtil.getSuccess();
    }

    @PostMapping(value = "/api/videos")
    @ResponseBody
    public ResultVO<Map<String, Object>> getVideoPage(@RequestBody VideoPageRequest videoPageRequest) {
        return this.videoService.getVideoPage(videoPageRequest);
    }
}

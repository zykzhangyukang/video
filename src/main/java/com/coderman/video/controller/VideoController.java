package com.coderman.video.controller;

import com.coderman.video.request.VideoPageRequest;
import com.coderman.video.service.CategoryService;
import com.coderman.video.service.VideoService;
import com.coderman.video.utils.ResultUtil;
import com.coderman.video.vo.CategoryVO;
import com.coderman.video.vo.ResultVO;
import io.swagger.annotations.ApiOperation;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.ResponseBody;

import javax.annotation.Resource;
import java.util.List;
import java.util.Map;
import java.util.concurrent.TimeUnit;

@Controller
public class VideoController {

    @Resource
    private VideoService videoService;

    @Resource
    private CategoryService categoryService;

    @ApiOperation(value = "视频发布页面", notes = "视频发布页面")
    @GetMapping(value = {"/publish"})
    public String videoPublishPage(Model model) {

        // 视频分类
        List<CategoryVO> videoCategories = this.categoryService.selectAllCategory();

        model.addAttribute("categories", videoCategories);
        return "publish";
    }

    @PostMapping(value = "/api/videos")
    @ResponseBody
    public ResultVO<Map<String, Object>> getVideoPage(@RequestBody VideoPageRequest videoPageRequest) {
        return this.videoService.getVideoPage(videoPageRequest);
    }

    @PostMapping(value = "/api/videos/upload")
    @ResponseBody
    public ResultVO<Void> videoUpload() throws InterruptedException {
        TimeUnit.MILLISECONDS.sleep(200);
        return ResultUtil.getSuccess();
    }
}

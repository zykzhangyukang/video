package com.coderman.video.controller;

import com.coderman.video.request.VideoPageRequest;
import com.coderman.video.service.CategoryService;
import com.coderman.video.service.VideoService;
import com.coderman.video.vo.CategoryVO;
import com.coderman.video.vo.VideoVO;
import io.swagger.annotations.ApiOperation;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;

import javax.annotation.Resource;
import java.util.List;

/**
 * @author ：zhangyukang
 * @date ：2025/05/20 17:54
 */
@Controller
public class IndexController {

    @Resource
    private CategoryService categoryService;

    @Resource
    private VideoService videoService;

    @ApiOperation(value = "首页路由", notes = "首页路由")
    @GetMapping(value = {"/"})
    public String indexPage(Model model, VideoPageRequest videoPageRequest) {

        // 视频分类
        List<CategoryVO> videoCategories = this.categoryService.selectAllCategory();

        // 列表首屏
        List<VideoVO> videos = this.videoService.selectFirstPage(videoPageRequest);

        model.addAttribute("categories", videoCategories);
        model.addAttribute("videos", videos);
        model.addAttribute("activeName",videoPageRequest.getName());

        return "index";
    }

}

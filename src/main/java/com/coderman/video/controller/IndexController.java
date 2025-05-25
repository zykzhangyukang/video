package com.coderman.video.controller;

import com.coderman.video.constant.CommonConstant;
import com.coderman.video.request.VideoPageRequest;
import com.coderman.video.service.CategoryService;
import com.coderman.video.service.VideoService;
import com.coderman.video.vo.CategoryVO;
import com.coderman.video.vo.VideoVO;
import com.google.common.collect.Lists;
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

    @ApiOperation(value = "测试路由", notes = "测试路由")
    @GetMapping(value = {"/test"})
    public String testPage() {
        return "test";
    }


    @ApiOperation(value = "首页路由", notes = "首页路由")
    @GetMapping(value = {"/"})
    public String indexPage(Model model, VideoPageRequest videoPageRequest) {

        // 视频分类
        List<CategoryVO> videoCategories = this.categoryService.selectAllCategory();

        // 列表首屏
        Long totalVideos = this.videoService.selectFirstCount(videoPageRequest);
        List<VideoVO> videos = Lists.newArrayListWithCapacity(totalVideos.intValue());
        if (totalVideos > 0) {
            videos = this.videoService.selectFirstPage(videoPageRequest);
        }

        model.addAttribute("categories", videoCategories);
        model.addAttribute("videos", videos);
        model.addAttribute("totalVideos", totalVideos);
        model.addAttribute("pageSize", CommonConstant.VIDEO_PAGE_SIZE);
        return "index";
    }

}

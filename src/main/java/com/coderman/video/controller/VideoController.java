package com.coderman.video.controller;

import com.coderman.video.request.VideoPageRequest;
import com.coderman.video.service.VideoService;
import com.coderman.video.vo.ResultVO;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.ResponseBody;

import javax.annotation.Resource;
import java.util.Map;

@Controller
public class VideoController {

    @Resource
    private VideoService videoService;

    @PostMapping(value = "/api/getVideoPage")
    @ResponseBody
    public ResultVO<Map<String, Object>> getVideoPage(@RequestBody VideoPageRequest videoPageRequest) {
        return this.videoService.getVideoPage(videoPageRequest);
    }
}

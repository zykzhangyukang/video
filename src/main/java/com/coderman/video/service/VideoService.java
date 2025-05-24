package com.coderman.video.service;

import com.coderman.video.request.VideoPageRequest;
import com.coderman.video.vo.ResultVO;

import java.util.Map;

public interface VideoService {

    /**
     * 视频列表
     * @param videoPageRequest 参数
     * @return
     */
    ResultVO<Map<String, Object>> getVideoPage(VideoPageRequest videoPageRequest);
}

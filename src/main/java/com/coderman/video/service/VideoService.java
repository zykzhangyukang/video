package com.coderman.video.service;

import com.coderman.video.request.VideoPageRequest;
import com.coderman.video.vo.ResultVO;
import com.coderman.video.vo.VideoVO;

import java.util.List;
import java.util.Map;

public interface VideoService {

    /**
     * 视频列表
     * @param videoPageRequest 参数
     * @return
     */
    ResultVO<Map<String, Object>> getVideoPage(VideoPageRequest videoPageRequest);

    /**
     * 首页第一屏视频
     * @param videoPageRequest 参数
     * @return
     */
    List<VideoVO> selectFirstPage(VideoPageRequest videoPageRequest);

}

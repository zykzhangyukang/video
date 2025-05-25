package com.coderman.video.service.impl;

import com.coderman.video.constant.CommonConstant;
import com.coderman.video.mapper.VideoMapper;
import com.coderman.video.request.VideoPageRequest;
import com.coderman.video.service.VideoService;
import com.coderman.video.utils.ResultUtil;
import com.coderman.video.vo.ResultVO;
import com.coderman.video.vo.VideoVO;
import com.google.common.collect.Lists;
import org.springframework.stereotype.Service;

import javax.annotation.Resource;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

@Service
public class VideoServiceImpl implements VideoService {

    @Resource
    private VideoMapper videoMapper;


    @Override
    public ResultVO<Map<String, Object>> getVideoPage(VideoPageRequest videoPageRequest) {

        if (videoPageRequest.getPage() == null || videoPageRequest.getPage() <= 0) {
            videoPageRequest.setPage(1);
        }
        if (videoPageRequest.getPage() == null || videoPageRequest.getPage() <= 0 || videoPageRequest.getSize() > CommonConstant.VIDEO_PAGE_SIZE) {
            videoPageRequest.setSize(CommonConstant.VIDEO_PAGE_SIZE);
        }

        videoPageRequest.setOffset((videoPageRequest.getPage() - 1) * videoPageRequest.getSize());
        Long total = videoMapper.countVideoPage(videoPageRequest);

        List<VideoVO> records = Lists.newArrayList();
        if (total > 0) {
            records = videoMapper.selectVideoPage(videoPageRequest);
        }


        Map<String, Object> result = new HashMap<>();
        result.put("total", total);
        result.put("list", records);
        return ResultUtil.getSuccessMap(Map.class, result);
    }

    @Override
    public List<VideoVO> selectFirstPage(VideoPageRequest videoPageRequest) {
        videoPageRequest.setSize(CommonConstant.VIDEO_PAGE_SIZE);
        return videoMapper.selectFirstPage(videoPageRequest);
    }

    @Override
    public Long selectFirstCount(VideoPageRequest videoPageRequest) {
        return videoMapper.countVideoPage(videoPageRequest);
    }
}

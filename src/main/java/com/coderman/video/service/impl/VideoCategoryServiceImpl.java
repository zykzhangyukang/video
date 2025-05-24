package com.coderman.video.service.impl;

import com.baomidou.mybatisplus.core.toolkit.Wrappers;
import com.coderman.video.mapper.VideoCategoryMapper;
import com.coderman.video.model.VideoCategory;
import com.coderman.video.service.VideoCategoryService;
import com.coderman.video.vo.VideoCategoryVO;
import org.springframework.stereotype.Service;

import javax.annotation.Resource;
import java.util.List;
import java.util.stream.Collectors;

@Service
public class VideoCategoryServiceImpl implements VideoCategoryService {

    @Resource
    private VideoCategoryMapper videoCategoryMapper;

    @Override
    public List<VideoCategoryVO> selectAllCategory() {
        List<VideoCategory> videoCategories = this.videoCategoryMapper.selectList(
                Wrappers.<VideoCategory>lambdaQuery()
                        .orderByAsc(VideoCategory::getSortOrder)
        );
        return videoCategories.stream()
                .map(vc -> {
                    VideoCategoryVO vo = new VideoCategoryVO();
                    vo.setName(vc.getName());
                    vo.setLabel(vc.getDescription() != null && !vc.getDescription().isEmpty() ? vc.getDescription() : vc.getName());
                    return vo;
                })
                .collect(Collectors.toList());

    }
}

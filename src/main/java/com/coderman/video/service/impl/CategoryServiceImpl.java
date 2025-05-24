package com.coderman.video.service.impl;

import com.baomidou.mybatisplus.core.toolkit.Wrappers;
import com.coderman.video.mapper.CategoryMapper;
import com.coderman.video.model.Category;
import com.coderman.video.service.CategoryService;
import com.coderman.video.vo.CategoryVO;
import org.springframework.stereotype.Service;

import javax.annotation.Resource;
import java.util.List;
import java.util.stream.Collectors;

@Service
public class CategoryServiceImpl implements CategoryService {

    @Resource
    private CategoryMapper categoryMapper;

    @Override
    public List<CategoryVO> selectAllCategory() {
        List<Category> videoCategories = this.categoryMapper.selectList(
                Wrappers.<Category>lambdaQuery()
                        .select(Category::getId, Category::getDescription, Category::getName)
                        .orderByAsc(Category::getSortOrder)
        );
        return videoCategories.stream()
                .map(vc -> {
                    CategoryVO vo = new CategoryVO();
                    vo.setId(vc.getId());
                    vo.setName(vc.getName());
                    vo.setLabel(vc.getDescription() != null && !vc.getDescription().isEmpty() ? vc.getDescription() : vc.getName());
                    return vo;
                })
                .collect(Collectors.toList());

    }
}

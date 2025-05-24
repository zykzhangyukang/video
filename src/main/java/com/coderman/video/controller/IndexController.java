package com.coderman.video.controller;

import com.coderman.video.service.CategoryService;
import com.coderman.video.vo.CategoryVO;
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

    @ApiOperation(value = "首页路由", notes = "首页路由")
    @GetMapping(value = {"/"})
    public String indexPage(Model model) {
        // 取所有视频分类
        List<CategoryVO> videoCategories = this.categoryService.selectAllCategory();

        // 把数据放到Model里，供模板使用
        model.addAttribute("categories", videoCategories);

        return "index";
    }

}

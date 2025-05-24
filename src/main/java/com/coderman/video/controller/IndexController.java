package com.coderman.video.controller;

import io.swagger.annotations.ApiOperation;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.GetMapping;

/**
 * @author ：zhangyukang
 * @date ：2025/05/20 17:54
 */
@Controller
public class IndexController {

    @ApiOperation(value = "首页路由", notes = "首页路由")
    @GetMapping(value = {"/"})
    public String indexPage() {
        return "index";
    }
}

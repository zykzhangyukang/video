package com.coderman.video.controller;

import com.coderman.video.utils.ResultUtil;
import com.coderman.video.vo.ResultVO;
import io.swagger.annotations.ApiOperation;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.ResponseBody;

/**
 * @author ：zhangyukang
 * @date ：2025/05/20 17:54
 */
public class IndexController {

    @ApiOperation(value = "首页路由", notes = "首页路由")
    @GetMapping(value = {"/index", "/"})
    public String indexPage() {
        return "index";
    }

    @ApiOperation(value = "视频列表", notes = "视频列表")
    @GetMapping(value = "/list")
    @ResponseBody
    public ResultVO<String> list() {
        return ResultUtil.getSuccess(String.class, "请求成功!");
    }
}

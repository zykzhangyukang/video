package com.coderman.video.handler;

import com.alibaba.fastjson.JSON;
import com.coderman.video.constant.ResultConstant;
import com.coderman.video.utils.RequestUtils;
import com.coderman.video.utils.ResultUtil;
import org.springframework.security.access.AccessDeniedException;
import org.springframework.security.web.access.AccessDeniedHandler;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.io.IOException;

/**
 * @author ：zhangyukang
 * @date ：2025/05/20 16:34
 */
public class MyAccessDeniedHandler implements AccessDeniedHandler {

    @Override
    public void handle(HttpServletRequest request,
                       HttpServletResponse response,
                       AccessDeniedException accessDeniedException)
            throws IOException {

        if (RequestUtils.isAjax(request)) {
            response.setContentType("application/json;charset=UTF-8");
            response.setStatus(HttpServletResponse.SC_FORBIDDEN);
            response.getWriter().write(JSON.toJSONString(ResultUtil.getFail(ResultConstant.RESULT_CODE_403, "权限不足，请联系管理员")));
        } else {
            response.sendError(HttpServletResponse.SC_FORBIDDEN, accessDeniedException.getMessage());
        }
    }
}


package com.coderman.video.handler;

import com.alibaba.fastjson.JSON;
import com.coderman.video.constant.ResultConstant;
import com.coderman.video.utils.RequestUtils;
import com.coderman.video.utils.ResultUtil;
import org.springframework.security.core.AuthenticationException;
import org.springframework.security.web.AuthenticationEntryPoint;

import javax.servlet.ServletException;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.io.IOException;

/**
 * @author ：zhangyukang
 * @date ：2025/05/20 16:00
 */

public class MyAuthenticationEntryPoint implements AuthenticationEntryPoint {

    private final AuthenticationEntryPoint loginPageEntryPoint;

    public MyAuthenticationEntryPoint(AuthenticationEntryPoint loginPageEntryPoint) {
        this.loginPageEntryPoint = loginPageEntryPoint;
    }
    @Override
    public void commence(HttpServletRequest request,
                         HttpServletResponse response,
                         AuthenticationException authException) throws IOException, ServletException {

        if (RequestUtils.isAjax(request)) {
            response.setContentType("application/json;charset=UTF-8");
            response.setStatus(HttpServletResponse.SC_OK);
            response.getWriter().write(JSON.toJSONString(ResultUtil.getFail(ResultConstant.RESULT_CODE_401, "未认证，请先登录")));
        } else {
            // 普通请求，走默认跳转逻辑
            loginPageEntryPoint.commence(request, response, authException);
        }
    }
}

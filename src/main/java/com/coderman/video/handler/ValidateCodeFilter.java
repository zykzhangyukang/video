package com.coderman.video.handler;

import com.coderman.video.constant.CommonConstant;
import org.springframework.lang.NonNull;
import org.springframework.stereotype.Component;
import org.springframework.web.filter.OncePerRequestFilter;

import javax.servlet.FilterChain;
import javax.servlet.ServletException;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.io.IOException;
import java.net.URLEncoder;

/**
 * @author ：zhangyukang
 * @date ：2025/05/20 17:38
 */
@Component
public class ValidateCodeFilter extends OncePerRequestFilter {

    @Override
    protected void doFilterInternal(HttpServletRequest request,
                                    @NonNull HttpServletResponse response,
                                    @NonNull FilterChain filterChain)
            throws ServletException, IOException {

        // 只拦截登录请求
        if ("/doLogin".equals(request.getRequestURI()) && request.getMethod().equalsIgnoreCase("POST")) {
            String captcha = request.getParameter("captcha");
            String sessionCaptcha = (String) request.getSession().getAttribute(CommonConstant.SESSION_KEY_IMAGE_CODE);
            if (captcha == null || !captcha.equalsIgnoreCase(sessionCaptcha)) {
                response.sendRedirect(request.getContextPath() + "/login?error=" + URLEncoder.encode("验证码错误", "UTF-8"));
                return;
            }
        }

        filterChain.doFilter(request, response);
    }
}

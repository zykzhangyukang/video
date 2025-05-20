package com.coderman.video.handler;

import com.coderman.video.model.User;
import com.coderman.video.service.UserService;
import com.coderman.video.utils.SecurityUtils;
import lombok.extern.slf4j.Slf4j;
import org.springframework.security.core.Authentication;
import org.springframework.security.web.authentication.AuthenticationSuccessHandler;
import org.springframework.stereotype.Component;

import javax.annotation.Resource;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.io.IOException;
import java.util.Date;

/**
 * @author ：zhangyukang
 * @date ：2025/05/20 12:22
 */
@Component
@Slf4j
public class MyAuthenticationSuccessHandler implements AuthenticationSuccessHandler {

    @Resource
    private UserService userService;

    @Override
    public void onAuthenticationSuccess(HttpServletRequest request,
                                        HttpServletResponse response,
                                        Authentication authentication) throws IOException {
        // 更新用户登录时间
        User user = new User();
        user.setId(SecurityUtils.getUserId());
        user.setLastLoginAt(new Date());
        this.userService.updateById(user);

        response.sendRedirect("/");
    }
}


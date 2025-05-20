package com.coderman.video.handler;

import com.coderman.video.model.User;
import com.coderman.video.service.UserService;
import org.springframework.security.core.authority.AuthorityUtils;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.stereotype.Service;

import javax.annotation.Resource;

/**
 * 用户信息获取
 * @author ：zhangyukang
 * @date ：2025/05/20 10:59
 */
@Service
public class MyUserDetailsService implements UserDetailsService {

    @Resource
    private UserService userService;

    @Override
    public UserDetails loadUserByUsername(String username) throws UsernameNotFoundException {

        User user = this.userService.selectByName(username);
        if (user == null) {
            throw new UsernameNotFoundException("用户不存在");
        }

        return new LoginUser(user.getId(),
                user.getUsername(),
                user.getPassword(),
                user.getNickname(),
                user.getAvatarUrl(),
                AuthorityUtils.createAuthorityList(user.getRoles()),
                user.getEnabled() == 0
                );
    }
}


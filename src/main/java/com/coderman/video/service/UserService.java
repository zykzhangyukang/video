package com.coderman.video.service;

import com.baomidou.mybatisplus.extension.service.IService;
import com.coderman.video.model.User;

public interface UserService extends IService<User> {

    /**
     * 根据用户名查询用户信息
     *
     * @param username 用户名
     * @return
     */
    User selectByName(String username);
}

package com.coderman.video.handler;

import lombok.Data;
import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.core.userdetails.UserDetails;

import java.util.Collection;
import java.util.List;

/**
 * @author ：zhangyukang
 * @date ：2025/05/20 11:00
 */
@Data
public class LoginUser implements UserDetails {
    private Long id;
    private String username;
    private String password;
    private String nickname;
    private String avatarUrl;
    private boolean locked;
    private boolean isAdmin;
    private List<GrantedAuthority> authorities;

    public LoginUser(Long id, String username, String password, String nickname, String avatarUrl, List<GrantedAuthority> authorities, boolean locked) {
        this.id = id;
        this.username = username;
        this.password = password;
        this.nickname = nickname;
        this.avatarUrl = avatarUrl;
        this.authorities = authorities;
        this.locked = locked;
        this.isAdmin = authorities.stream().anyMatch(a -> a.getAuthority().equals("ROLE_ADMIN"));
    }

    @Override public Collection<? extends GrantedAuthority> getAuthorities() { return authorities; }
    @Override public String getPassword() { return password; }
    @Override public String getUsername() { return username; }

    @Override
    public boolean isAccountNonExpired() {
        return true; // 账号不过期
    }

    @Override
    public boolean isAccountNonLocked() {
        return !this.locked;
    }

    @Override
    public boolean isCredentialsNonExpired() {
        return true; // 凭证不过期
    }

    @Override
    public boolean isEnabled() {
        return true; // 账号启用状态
    }

    // 省略其它必要的重写...

    public String getNickname() { return nickname; }
    public String getAvatarUrl() { return avatarUrl; }
}


package com.coderman.video.config;

import com.coderman.video.handler.*;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.security.config.annotation.authentication.builders.AuthenticationManagerBuilder;
import org.springframework.security.config.annotation.method.configuration.EnableGlobalMethodSecurity;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.config.annotation.web.configuration.EnableWebSecurity;
import org.springframework.security.config.annotation.web.configuration.WebSecurityConfigurerAdapter;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.security.web.AuthenticationEntryPoint;
import org.springframework.security.web.authentication.LoginUrlAuthenticationEntryPoint;
import org.springframework.security.web.authentication.UsernamePasswordAuthenticationFilter;
import org.springframework.security.web.authentication.rememberme.JdbcTokenRepositoryImpl;
import org.springframework.security.web.authentication.rememberme.PersistentTokenRepository;

import javax.annotation.Resource;
import javax.sql.DataSource;

/**
 * @author ：zhangyukang
 * @date ：2025/05/20 10:18
 */
@Configuration
@EnableWebSecurity
@EnableGlobalMethodSecurity(prePostEnabled = true)
public class SecurityConfig extends WebSecurityConfigurerAdapter {

    @Resource
    private MyUserDetailsService myUserDetailsService;

    @Resource
    private DataSource dataSource;
    @Resource
    private ValidateCodeFilter validateCodeFilter;
    @Resource
    private MyAuthenticationFailureHandler myAuthenticationFailureHandler;
    @Resource
    private MyAuthenticationSuccessHandler myAuthenticationSuccessHandler;

    @Bean
    public PersistentTokenRepository persistentTokenRepository() {
        JdbcTokenRepositoryImpl jdbcTokenRepository = new JdbcTokenRepositoryImpl();
        jdbcTokenRepository.setDataSource(dataSource);
        jdbcTokenRepository.setCreateTableOnStartup(false);
        return jdbcTokenRepository;
    }

    @Override
    protected void configure(AuthenticationManagerBuilder auth) throws Exception {
        auth.userDetailsService(myUserDetailsService).passwordEncoder(passwordEncoder());
    }

    @Override
    protected void configure(HttpSecurity http) throws Exception {
        http
                .headers()
                .frameOptions()
                .sameOrigin()
                .and()
                .addFilterBefore(validateCodeFilter, UsernamePasswordAuthenticationFilter.class)
                .csrf().disable() // 禁用 CSRF（表单项目可开启，前后端分离建议关闭）
                .authorizeRequests()
                .antMatchers("/login", "/libs/**", "/css/**", "/js/**","/code/image").permitAll() // 放行请求
                .anyRequest().authenticated() // 其他都要认证
                .and()
                .formLogin()
                .loginPage("/login")                   // 自定义登录页面
                .loginProcessingUrl("/doLogin")        // 登录请求地址（form 表单的 action）
                .failureHandler(myAuthenticationFailureHandler) // 失败处理器
                .successHandler(myAuthenticationSuccessHandler) // 成功处理器
                // .defaultSuccessUrl("/index", true)  // 取消默认跳转，防止冲突
                // .failureUrl("/login?error")         // 不建议和 failureHandler 一起用
                .and()
                .rememberMe()
                .tokenRepository(persistentTokenRepository())  // token 持久化仓库
                .tokenValiditySeconds(7 * 24 * 60 * 60)         // token有效期，一周
                .userDetailsService(myUserDetailsService)       // 处理自动登录逻辑
                .and()
                .logout()
                .logoutUrl("/logout")
                .logoutSuccessUrl("/") // 退出成功后跳转页面
                .and()
                .exceptionHandling()
                .authenticationEntryPoint(authenticationEntryPoint());
                //.accessDeniedHandler(new MyAccessDeniedHandler()); 全局异常会处理
    }

    @Bean
    public AuthenticationEntryPoint authenticationEntryPoint() {
        LoginUrlAuthenticationEntryPoint loginEntryPoint = new LoginUrlAuthenticationEntryPoint("/login");
        return new MyAuthenticationEntryPoint(loginEntryPoint);
    }


    @Bean
    public PasswordEncoder passwordEncoder() {
        return new BCryptPasswordEncoder();
    }
}


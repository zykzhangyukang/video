package com.coderman.video.config;

import com.coderman.video.utils.FileUtils;
import org.springframework.context.annotation.Configuration;
import org.springframework.context.annotation.Profile;
import org.springframework.web.servlet.config.annotation.ResourceHandlerRegistry;
import org.springframework.web.servlet.config.annotation.WebMvcConfigurer;

import javax.annotation.Resource;

/**
 * 本地文件映射 (开发环境可用)
 *
 * @author ：zhangyukang
 * @date ：2025/05/29 15:06
 */
@Configuration
@Profile(value = "dev")
public class WebMvcConfig implements WebMvcConfigurer {

    @Resource
    private UploadConfig uploadConfig;

    @Override
    public void addResourceHandlers(ResourceHandlerRegistry registry) {
        registry.addResourceHandler("/" + FileUtils.dir + "/**")
                .addResourceLocations("file:" + uploadConfig.getBaseUploadPath() + "/" + FileUtils.dir + "/");
    }
}


package com.coderman.video.config;

import io.swagger.annotations.ApiModelProperty;
import lombok.Data;
import org.springframework.boot.context.properties.ConfigurationProperties;
import org.springframework.context.annotation.Configuration;

/**
 * @author ：zhangyukang
 * @date ：2025/05/29 14:06
 */
@Configuration
@ConfigurationProperties(value = "upload")
@Data
public class UploadConfig {

    @ApiModelProperty(value = "本地上传目录")
    private String baseUploadPath;

    @ApiModelProperty(value = "过期文件清除")
    private long expireMinutes;

    @ApiModelProperty(value = "本地文件域名")
    private String localDomain;

    @ApiModelProperty(value = "oss文件域名")
    private String ossDomain;
}

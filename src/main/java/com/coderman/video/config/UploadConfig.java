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

    @ApiModelProperty(value = "上传策略")
    private String strategy;

    @ApiModelProperty(value = "过期文件清除")
    private long expireMinutes;

    @ApiModelProperty("本地上传配置")
    private Local local = new Local();

    @ApiModelProperty("OSS上传配置")
    private Oss oss = new Oss();

    @Data
    public static class Local {
        @ApiModelProperty(value = "本地文件域名")
        private String domain;
        @ApiModelProperty(value = "本地上传目录")
        private String baseUploadPath;
    }

    @Data
    public static class Oss {
        @ApiModelProperty(value = "oss文件域名")
        private String domain;
        private String endpoint;
        private String accessKeyId;
        private String accessKeySecret;
        private String bucketName;
    }
}

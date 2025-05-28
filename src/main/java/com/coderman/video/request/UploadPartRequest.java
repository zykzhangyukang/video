package com.coderman.video.request;

import io.swagger.annotations.ApiModelProperty;
import lombok.Data;
import org.springframework.web.multipart.MultipartFile;

/**
 * @author ：zhangyukang
 * @date ：2025/05/28 16:12
 */
@Data
public class UploadPartRequest {

    @ApiModelProperty(value = "任务id")
    private String uploadId;

    @ApiModelProperty(value = "分片索引")
    private Integer partIndex;

    @ApiModelProperty(value = "文件")
    private MultipartFile file;

    @ApiModelProperty(value = "文件hash")
    private String fileHash;

    @ApiModelProperty(value = "文件名")
    private String fileName;
}

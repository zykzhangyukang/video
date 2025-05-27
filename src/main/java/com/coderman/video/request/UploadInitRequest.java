package com.coderman.video.request;

import io.swagger.annotations.ApiModelProperty;
import lombok.Data;

@Data
public class UploadInitRequest {

    @ApiModelProperty(value = "文件hash")
    private String fileHash;

    @ApiModelProperty(value = "文件名")
    private String fileName;

    @ApiModelProperty(value = "文件大小")
    private Long fileSize;

    @ApiModelProperty(value = "分片总数")
    private Integer totalParts;
}

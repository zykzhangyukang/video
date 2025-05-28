package com.coderman.video.request;

import io.swagger.annotations.ApiModelProperty;
import lombok.Data;

/**
 * @author ：zhangyukang
 * @date ：2025/05/28 18:54
 */
@Data
public class UploadMergeRequest {

    @ApiModelProperty(value = "上传任务id")
    private String uploadId;
}

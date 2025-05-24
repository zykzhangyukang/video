package com.coderman.video.request;

import io.swagger.annotations.ApiModelProperty;
import lombok.Data;

@Data
public class VideoPageRequest {

    @ApiModelProperty(value = "当前页")
    private Integer page;

    @ApiModelProperty(value = "显示条数")
    private Integer size;

    @ApiModelProperty(value = "分页偏移量")
    private Integer offset;

    @ApiModelProperty(value = "分类id")
    private Long categoryId;

    @ApiModelProperty(value = "关键词")
    private String keyword;
}

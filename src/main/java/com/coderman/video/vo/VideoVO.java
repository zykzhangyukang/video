package com.coderman.video.vo;

import com.coderman.video.model.BaseModel;
import io.swagger.annotations.ApiModelProperty;
import lombok.Data;
import lombok.EqualsAndHashCode;

import java.util.Date;

@EqualsAndHashCode(callSuper = true)
@Data
public class VideoVO extends BaseModel {

    @ApiModelProperty(value = "视频ID")
    private Long id;

    @ApiModelProperty(value = "所属分类ID")
    private Long categoryId;

    @ApiModelProperty(value = "视频标题")
    private String title;

    @ApiModelProperty(value = "封面图URL")
    private String cover;

    @ApiModelProperty(value = "视频描述")
    private String description;

    @ApiModelProperty(value = "分类")
    private String categoryName;

    @ApiModelProperty(value = "状态")
    private Integer status;

    @ApiModelProperty(value = "创建时间")
    private Date createdAt;
}

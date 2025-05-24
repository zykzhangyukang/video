package com.coderman.video.vo;

import com.coderman.video.model.BaseModel;
import io.swagger.annotations.ApiModelProperty;
import lombok.Data;
import lombok.EqualsAndHashCode;

@EqualsAndHashCode(callSuper = true)
@Data
public class VideoCategoryVO extends BaseModel {

    @ApiModelProperty(value = "分类名")
    private String name;

    @ApiModelProperty(value = "显示的标签")
    private String label;
}

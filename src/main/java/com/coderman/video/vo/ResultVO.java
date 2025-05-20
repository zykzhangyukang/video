package com.coderman.video.vo;

import com.coderman.video.model.BaseModel;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.EqualsAndHashCode;
import lombok.NoArgsConstructor;

/**
 * @author ：zhangyukang
 * @date ：2025/05/20 15:51
 */
@EqualsAndHashCode(callSuper = true)
@Data
@AllArgsConstructor
@NoArgsConstructor
public class ResultVO<T> extends BaseModel {

    /**
     * 状态码
     */
    private int code;

    /**
     * 返回消息
     */
    private String msg;


    /**
     * 响应结果
     */
    private T result;
}


package com.coderman.video.config;

import com.coderman.video.constant.ResultConstant;
import com.coderman.video.exception.VideoException;
import com.coderman.video.utils.RequestUtils;
import com.coderman.video.utils.ResultUtil;
import org.springframework.security.access.AccessDeniedException;
import org.springframework.web.bind.annotation.ControllerAdvice;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.ResponseBody;

import javax.servlet.http.HttpServletRequest;

/**
 * @author ：zhangyukang
 * @date ：2025/05/20 15:22
 */
@ControllerAdvice
public class GlobalExceptionHandler {

    @ExceptionHandler(Exception.class)
    @ResponseBody
    public Object handleException(HttpServletRequest request, Exception ex) {
        if (RequestUtils.isAjax(request)) {
            if (ex instanceof AccessDeniedException) {
                return ResultUtil.getFail(ResultConstant.RESULT_CODE_403, "权限不足，请联系管理员");
            }
            return ResultUtil.getFail(ResultConstant.RESULT_CODE_500, "系统异常");
        } else {
            throw new VideoException(ex);
        }
    }

}


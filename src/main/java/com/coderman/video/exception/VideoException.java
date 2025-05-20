package com.coderman.video.exception;

/**
 * @author ：zhangyukang
 * @date ：2025/05/20 17:19
 */
public class VideoException extends RuntimeException{

    public VideoException(Throwable cause) {
        super(cause);
    }

    public VideoException(String message, Throwable cause) {
        super(message, cause);
    }

    public VideoException(String message) {
        super(message);
    }
}

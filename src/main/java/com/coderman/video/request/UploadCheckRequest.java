package com.coderman.video.request;

import lombok.Data;

/**
 * @author ：zhangyukang
 * @date ：2025/05/28 16:41
 */
@Data
public class UploadCheckRequest {
    private String fileHash;
    private Long fileSize;
    private String fileName;
}

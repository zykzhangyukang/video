package com.coderman.video.vo;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

/**
 * @author ：zhangyukang
 * @date ：2025/05/28 16:42
 */
@Data
@AllArgsConstructor
@NoArgsConstructor
public class UploadCheckVO {
    private Boolean isFastUpload; // 是否可以秒传
    private String fileUrl;       // 文件访问路径
    private String uploadId;      // 已上传任务的ID（可选）
    private Integer nextPartIndex;    // 上传到第几块
}

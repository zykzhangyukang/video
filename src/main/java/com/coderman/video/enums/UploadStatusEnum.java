package com.coderman.video.enums;

/**
 * @author ：zhangyukang
 * @date ：2025/05/28 16:45
 */
public enum UploadStatusEnum {
    INIT(0, "初始化"),
    UPLOADING(1, "上传中"),
    UPLOADED(2, "已上传"),
    MERGED(3, "已合并"),
    CANCELED(4, "取消"),
    FAILED(5, "失败");

    private final int code;
    private final String description;

    UploadStatusEnum(int code, String description) {
        this.code = code;
        this.description = description;
    }

    public int getCode() {
        return code;
    }

    public String getDescription() {
        return description;
    }

    public static UploadStatusEnum fromCode(int code) {
        for (UploadStatusEnum status : UploadStatusEnum.values()) {
            if (status.code == code) {
                return status;
            }
        }
        throw new IllegalArgumentException("未知的上传状态码: " + code);
    }
}

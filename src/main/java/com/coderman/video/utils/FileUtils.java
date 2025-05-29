package com.coderman.video.utils;

import lombok.SneakyThrows;
import org.apache.commons.lang3.RandomStringUtils;
import org.apache.commons.lang3.StringUtils;
import org.apache.commons.lang3.time.DateFormatUtils;
import org.springframework.util.Assert;

import java.net.URLEncoder;
import java.nio.charset.StandardCharsets;
import java.time.Instant;
import java.util.Date;
import java.util.HashMap;
import java.util.Map;
import java.util.Optional;

/**
 * @author ：zhangyukang
 * @date ：2025/05/28 16:01
 */

public class FileUtils {

    private static final String dir = "data";

    public enum FileModuleEnum {
        COMMON_MODULE("common"),
        USER_MODULE("user"),
        PRODUCT_MODULE("product");
        private final String code;

        FileModuleEnum(String code) { this.code = code; }
        public String getCode() { return code; }
    }

    private static final Map<String, String> MIME_MAP = new HashMap<>();
    static {
        MIME_MAP.put("jpg", "image/jpeg");
        MIME_MAP.put("jpeg", "image/jpeg");
        MIME_MAP.put("png", "image/png");
        MIME_MAP.put("gif", "image/gif");
        MIME_MAP.put("mp4", "video/mp4");
        MIME_MAP.put("mp3", "audio/mpeg");
        MIME_MAP.put("pdf", "application/pdf");
        MIME_MAP.put("doc", "application/msword");
        MIME_MAP.put("docx", "application/vnd.openxmlformats-officedocument.wordprocessingml.document");
        MIME_MAP.put("xls", "application/vnd.ms-excel");
        MIME_MAP.put("xlsx", "application/vnd.openxmlformats-officedocument.spreadsheetml.sheet");
        MIME_MAP.put("zip", "application/zip");
        MIME_MAP.put("rar", "application/x-rar-compressed");
        MIME_MAP.put("txt", "text/plain");
    }

    public static String getFileType(String fileName) {
        if (fileName == null || !fileName.contains(".")) {
            return "application/octet-stream";
        }
        String ext = fileName.substring(fileName.lastIndexOf('.') + 1).toLowerCase();
        return MIME_MAP.getOrDefault(ext, "application/octet-stream");
    }

    public static boolean isImage(String ext) {
        return ext != null && ext.matches("(?i)jpg|jpeg|png|gif|bmp|webp");
    }

    /**
     * 生成文件路径，格式：
     * {applicationName}/{module}/{yyyy/MM}/{fileHash}.{suffix}
     *
     * @param originalFilename 源文件名，带后缀
     * @param fileModuleEnum   业务模块枚举
     * @return 结构化路径字符串
     */
    @SneakyThrows
    public static String genFilePath(String originalFilename, FileModuleEnum fileModuleEnum) {

        fileModuleEnum = Optional.ofNullable(fileModuleEnum).orElse(FileModuleEnum.COMMON_MODULE);
        Assert.isTrue(StringUtils.isNotBlank(originalFilename), "文件原始名称不能为空!!");

        int index = originalFilename.lastIndexOf(".");
        Assert.isTrue(index > 0, String.format("根据文件名%s获取文件类型失败!!", originalFilename));

        // 文件类型
        String fileType = originalFilename.substring(index + 1);
        Assert.isTrue(StringUtils.isNotBlank(fileType), String.format("根据文件名%s获取文件类型失败!!", originalFilename));

        Integer second = (int) Instant.now().getEpochSecond();
        String day = DateFormatUtils.format(new Date(), "yyyy-MM-dd");
        String newFileName;
        if (isImage(fileType)) {
            newFileName = second + "_" + RandomStringUtils.randomAlphanumeric(4) + "." + fileType;
        } else {
            newFileName = second + "_" + URLEncoder.encode(originalFilename, StandardCharsets.UTF_8.name());
        }
        return String.format("%s/%s/%s/%s/%s", dir, fileModuleEnum.getCode(), day, fileType, newFileName);
    }

    // 测试主方法
    public static void main(String[] args) {
        String fileName = "example.png";

        System.out.println("FileType: " + getFileType(fileName));
        System.out.println("FilePath: " + genFilePath(fileName, FileModuleEnum.USER_MODULE));
    }
}


package com.coderman.video.utils;

import lombok.SneakyThrows;
import org.apache.commons.lang3.RandomStringUtils;
import org.apache.commons.lang3.StringUtils;
import org.apache.commons.lang3.time.DateFormatUtils;
import org.springframework.util.Assert;

import java.io.File;
import java.io.FileInputStream;
import java.io.IOException;
import java.nio.charset.StandardCharsets;
import java.security.MessageDigest;
import java.time.Instant;
import java.util.Date;
import java.util.List;
import java.util.Optional;

/**
 * @author ：zhangyukang
 * @date ：2025/05/28 16:01
 */

public class FileUtils {

    public static final String dir = "uploads";

    public enum FileModuleEnum {
        COMMON_MODULE("common"),
        USER_MODULE("user"),
        PRODUCT_MODULE("product");
        private final String code;

        FileModuleEnum(String code) { this.code = code; }
        public String getCode() { return code; }
    }

    public static String getFileType(String fileName) {
        if (fileName == null || !fileName.contains(".")) {
            return "";
        }
        return fileName.substring(fileName.lastIndexOf('.') + 1).toLowerCase();
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
            newFileName = second + "_" + RandomStringUtils.randomAlphanumeric(5) + "." + fileType;
        }
        return String.format("%s/%s/%s/%s/%s", dir, fileModuleEnum.getCode(), day, fileType, newFileName);
    }

    public static String computeMD5(File file) throws IOException {
        try (FileInputStream fis = new FileInputStream(file)) {
            MessageDigest md = MessageDigest.getInstance("MD5");
            byte[] buffer = new byte[8192];
            int bytesRead;
            while ((bytesRead = fis.read(buffer)) != -1) {
                md.update(buffer, 0, bytesRead);
            }
            byte[] digest = md.digest();
            StringBuilder sb = new StringBuilder();
            for (byte b : digest) {
                sb.append(String.format("%02x", b & 0xff));
            }
            return sb.toString();
        } catch (Exception e) {
            throw new IOException("计算文件MD5失败", e);
        }
    }

    public static String calHash(List<String> hashList) {
        if (hashList == null || hashList.isEmpty()) {
            throw new IllegalArgumentException("hash 列表不能为空");
        }
        try {
            // 拼接所有 hash 字符串
            StringBuilder sb = new StringBuilder();
            for (String h : hashList) {
                if (h == null || h.trim().isEmpty()) {
                    throw new IllegalArgumentException("hash 列表中存在空值");
                }
                sb.append(h.trim());
            }
            // 计算拼接后字符串的 MD5
            MessageDigest md = MessageDigest.getInstance("MD5");
            byte[] digest = md.digest(sb.toString().getBytes(StandardCharsets.UTF_8));
            // 转成十六进制字符串
            StringBuilder result = new StringBuilder();
            for (byte b : digest) {
                result.append(String.format("%02x", b & 0xff));
            }

            return result.toString();
        } catch (Exception e) {
            throw new RuntimeException("计算聚合 hash 失败", e);
        }
    }
}


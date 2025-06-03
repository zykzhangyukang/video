package com.coderman.video.utils;

import lombok.extern.slf4j.Slf4j;

import java.io.BufferedReader;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStreamReader;
import java.security.SecureRandom;
import java.util.Arrays;
import java.util.List;

@Slf4j
public class FfmpegUtil {

    private static final String FFMPEG_PATH = "F:\\files\\uploads\\common\\2025-05-30\\mp4\\ffmpeg.exe";

    public static void runHlsEncryptedCommand(String input, String m3u8Path, String segmentPattern, String keyInfoPath) {
        List<String> command = Arrays.asList(
                FFMPEG_PATH, "-i", input,
                "-c:v", "libx264",
                "-c:a", "aac",
                "-b:v", "1500k",
                "-hls_time", "10",
                "-hls_playlist_type", "vod",
                "-hls_key_info_file", keyInfoPath,
                "-hls_segment_filename", segmentPattern,
                "-f", "hls", m3u8Path
        );

        log.info("执行加密转码命令: {}", String.join(" ", command));
        runCommand(command);
    }


    public static void runHlsCommand(String input, String m3u8Path, String segmentPattern) {
        List<String> command = Arrays.asList(
                FFMPEG_PATH, "-i", input,
                "-c:v", "libx264",         // 视频编码为 H.264
                "-c:a", "aac",             // 音频编码为 AAC
                "-b:v", "1500k",           // 视频码率，可调整
                "-hls_time", "10",
                "-hls_playlist_type", "vod",
                "-hls_segment_filename", segmentPattern,
                "-f", "hls", m3u8Path
        );

        log.info("准备执行 FFmpeg 转码命令: {}", String.join(" ", command));
        runCommand(command);
    }

    private static void runCommand(List<String> command) {
        try {
            ProcessBuilder pb = new ProcessBuilder(command);
            pb.redirectErrorStream(true);
            Process process = pb.start();

            try (BufferedReader reader = new BufferedReader(new InputStreamReader(process.getInputStream()))) {
                String line;
                while ((line = reader.readLine()) != null) {
                    log.info("[FFmpeg] {}", line);
                }
            }

            int exitCode = process.waitFor();
            log.info("FFmpeg 转码进程已退出，Exit Code: {}", exitCode);
            if (exitCode != 0) {
                log.warn("FFmpeg 转码非正常退出，可能存在错误，Exit Code: {}", exitCode);
            }
        } catch (Exception e) {
            log.error("执行 FFmpeg 命令失败", e);
        }
    }

    public static void generateKeyFile(String keyFilePath) throws IOException {
        byte[] key = new byte[16];
        new SecureRandom().nextBytes(key);

        try (FileOutputStream fos = new FileOutputStream(keyFilePath)) {
            fos.write(key);
        }
        log.info("密钥文件已生成：{}", keyFilePath);
    }

    public static void main(String[] args) {
        String dir = "F:\\files\\uploads\\common\\2025-05-30\\mp4";

//        runHlsCommand(dir + "\\input.mp4", dir + "\\hls\\output.m3u8", dir + "\\hls\\seg_%03d.ts");
        runHlsEncryptedCommand(dir + "\\input.mp4", dir + "\\hls\\output.m3u8", dir + "\\hls\\seg_%03d.ts", dir + "\\keyinfo.txt");
    }
}


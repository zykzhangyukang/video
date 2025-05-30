package com.coderman.video.job;

import com.baomidou.mybatisplus.core.toolkit.Wrappers;
import com.coderman.video.config.UploadConfig;
import com.coderman.video.enums.UploadStatusEnum;
import com.coderman.video.mapper.UploadTaskMapper;
import com.coderman.video.model.UploadTask;
import lombok.extern.slf4j.Slf4j;
import org.springframework.boot.autoconfigure.condition.ConditionalOnProperty;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Component;

import javax.annotation.Resource;
import java.io.File;
import java.nio.file.Paths;
import java.time.LocalDateTime;
import java.util.List;

/**
 * 本地文件清理定时器
 * @author ：zhangyukang
 * @date ：2025/05/29 14:11
 */
@Slf4j
@Component
@ConditionalOnProperty(name = "upload.strategy", havingValue = "local")
public class UploadCleanupJob {

    @Resource
    private UploadConfig uploadConfig;
    @Resource
    private UploadTaskMapper uploadTaskMapper;

    /**
     * 清理无效任务和临时文件
     */
    // "*/5 * * * * ?"
    @Scheduled(cron = "0 */5 * * * ?")
    public void cleanExpiredUploadTasks() {
        long expireMinutes = uploadConfig.getLocal().getExpireMinutes();
        String baseUploadPath = uploadConfig.getLocal().getBaseUploadPath();
        LocalDateTime expireTime = LocalDateTime.now().minusMinutes(expireMinutes);
        log.info("开始清理 {} 分钟前未完成的上传任务", expireMinutes);

        List<UploadTask> expiredTasks = uploadTaskMapper.selectList(
                Wrappers.<UploadTask>lambdaQuery()
                        .in(UploadTask::getStatus,
                                UploadStatusEnum.INIT.getCode(),
                                UploadStatusEnum.UPLOADING.getCode())
                        .lt(UploadTask::getCreatedAt, expireTime)
        );

        int deletedFileCount = 0;
        for (UploadTask task : expiredTasks) {
            String uploadId = task.getUploadId();
            File tmpDir = Paths.get(baseUploadPath, "tmp", uploadId).toFile();
            if (tmpDir.exists()) {
                boolean success = deleteDirectory(tmpDir);
                if (success) {
                    deletedFileCount++;
                    log.info("删除临时目录成功: {}", tmpDir.getAbsolutePath());
                } else {
                    log.warn("删除临时目录失败: {}", tmpDir.getAbsolutePath());
                }
            }

            // 标记数据库状态为失败
            UploadTask update = new UploadTask();
            update.setId(task.getId());
            update.setStatus(UploadStatusEnum.FAILED.getCode());
            uploadTaskMapper.updateById(update);
        }

        log.info("清理完成，共处理 {} 个过期任务，成功删除临时目录 {} 个", expiredTasks.size(), deletedFileCount);
    }

    private boolean deleteDirectory(File dir) {
        if (!dir.exists()) return false;
        File[] files = dir.listFiles();
        if (files != null) {
            for (File file : files) {
                if (!file.delete()) {
                    log.warn("删除文件失败: {}", file.getAbsolutePath());
                }
            }
        }
        return dir.delete();
    }

}

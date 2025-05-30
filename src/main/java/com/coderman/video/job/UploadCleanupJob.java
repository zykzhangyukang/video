package com.coderman.video.job;

import com.coderman.video.service.UploadService;
import lombok.extern.slf4j.Slf4j;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Component;

import javax.annotation.Resource;

/**
 * 本地文件清理定时器
 * @author ：zhangyukang
 * @date ：2025/05/29 14:11
 */
@Slf4j
@Component
public class UploadCleanupJob {

    @Resource
    private UploadService uploadService;

    /**
     * 清理无效任务和临时文件
     */
    // "*/5 * * * * ?"
    @Scheduled(cron = "0 */5 * * * ?")
    public void cleanExpiredUploadTasks() {

        this.uploadService.scheduleUploadDataCleanup();
    }

}

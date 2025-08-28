package com.website.backend.system.service.impl;

import lombok.extern.slf4j.Slf4j;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Service;
import com.website.backend.system.service.GuestUserService;

@Slf4j
@Service
public class ScheduledTasks {

    private final GuestUserService guestUserService;

    public ScheduledTasks(GuestUserService guestUserService) {
        this.guestUserService = guestUserService;
    }

    /**
     * 每天凌晨1点清理过期的游客用户
     * 使用cron表达式：秒 分 时 日 月 周 年（年可选）
     * 0 0 1 * * ? 表示每天凌晨1点执行
     */
    @Scheduled(cron = "0 0 1 * * ?")
    public void cleanupExpiredGuests() {
        log.info("开始清理过期的游客用户");
        guestUserService.deleteExpiredGuests();
        log.info("过期游客用户清理完成");
    }
}
package com.website.backend.common.config;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Configuration;
import org.springframework.scheduling.annotation.EnableScheduling;
import org.springframework.scheduling.annotation.Scheduled;

import com.website.backend.system.service.GuestUserService;
import com.website.backend.system.service.VerificationCodeService;
import com.website.backend.system.service.IpRateLimitService;

import lombok.extern.slf4j.Slf4j;

@Configuration
@EnableScheduling
@Slf4j
public class SchedulerConfig {

    @Autowired
    private GuestUserService guestUserService;

    @Autowired
    private VerificationCodeService verificationCodeService;

    @Autowired
    private IpRateLimitService ipRateLimitService;

    @Scheduled(cron = "0 0 * * * ?")
    public void cleanExpiredGuestUsers() {
        guestUserService.deleteExpiredGuests();
    }

    @Scheduled(cron = "0 0/30 * * * ?")
    public void cleanExpiredVerificationCodes() {
        verificationCodeService.deleteExpiredCodes();
    }

    @Scheduled(cron = "0 0 */2 * * ?")
    public void cleanExpiredIpRateLimits() {
        ipRateLimitService.clearExpiredRateLimits();
    }
}
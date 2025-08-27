package com.website.backend.service.impl;

import com.website.backend.service.RateLimitService;
import com.website.backend.service.IpRateLimitService;
import com.website.backend.entity.IpRateLimit;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.time.LocalDateTime;
import java.time.temporal.ChronoUnit;
import java.util.Optional;

@Service
public class RateLimitServiceImpl implements RateLimitService {

    private final IpRateLimitService ipRateLimitService;

    private static final int TIME_WINDOW_SECONDS = 10;

    private static final int MAX_REQUESTS = 5;

    private static final long BLOCK_DURATION_DAYS = 1;

    @Autowired
    public RateLimitServiceImpl(IpRateLimitService ipRateLimitService) {
        this.ipRateLimitService = ipRateLimitService;
    }

    @Override
    public boolean isIpBlocked(String ip) {
        return ipRateLimitService.isIpBlocked(ip);
    }

    @Override
    public boolean recordDownloadRequest(String ip) {
        if (isIpBlocked(ip)) {
            return true;
        }

        // 获取或创建IP限制记录
        Optional<IpRateLimit> rateLimitOptional = ipRateLimitService.findByIpAddress(ip);
        LocalDateTime currentTime = LocalDateTime.now();
        IpRateLimit rateLimit;

        // 检查时间窗口是否过期
        if (!rateLimitOptional.isPresent()) {
            // 创建新记录
            rateLimit = new IpRateLimit();
            rateLimit.setIpAddress(ip);
            rateLimit.setRequestCount(1);
            rateLimit.setWindowStartTime(currentTime);
            rateLimit.setWindowEndTime(currentTime.plus(TIME_WINDOW_SECONDS, ChronoUnit.SECONDS));
            rateLimit.setIsBlocked(false);
            rateLimit.setCreateTime(currentTime);
            rateLimit.setUpdateTime(currentTime);
            ipRateLimitService.saveIpRateLimit(rateLimit);
        } else {
            rateLimit = rateLimitOptional.get();
            if (rateLimit.getWindowEndTime().isBefore(currentTime)) {
                // 时间窗口已过期，重置计数
                rateLimit.setRequestCount(1);
                rateLimit.setWindowStartTime(currentTime);
                rateLimit.setWindowEndTime(currentTime.plus(TIME_WINDOW_SECONDS, ChronoUnit.SECONDS));
                rateLimit.setUpdateTime(currentTime);
                ipRateLimitService.updateIpRateLimit(rateLimit);
            } else {
                // 增加请求计数
                rateLimit.setRequestCount(rateLimit.getRequestCount() + 1);
                rateLimit.setUpdateTime(currentTime);
                ipRateLimitService.updateIpRateLimit(rateLimit);
            }
        }

        // 检查是否超过最大请求数
        if (rateLimit.getRequestCount() > MAX_REQUESTS) {
            blockIp(ip);
            return true;
        }

        return false;
    }

    @Override
    public void blockIp(String ip) {
        ipRateLimitService.blockIp(ip);
    }

}
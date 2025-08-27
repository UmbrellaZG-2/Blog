package com.website.backend.service.impl;

import com.website.backend.entity.IpRateLimit;
import com.website.backend.mapper.IpRateLimitMapper;
import com.website.backend.service.IpRateLimitService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.time.LocalDateTime;
import java.time.temporal.ChronoUnit;
import java.util.Optional;

@Service
public class IpRateLimitServiceImpl implements IpRateLimitService {

    @Autowired
    private IpRateLimitMapper ipRateLimitMapper;

    private static final int TIME_WINDOW_SECONDS = 10;
    private static final int MAX_REQUESTS = 5;
    private static final long BLOCK_DURATION_DAYS = 1;

    @Override
    public void saveIpRateLimit(IpRateLimit ipRateLimit) {
        ipRateLimit.setCreateTime(LocalDateTime.now());
        ipRateLimit.setUpdateTime(LocalDateTime.now());
        ipRateLimitMapper.insert(ipRateLimit);
    }

    @Override
    public Optional<IpRateLimit> findByIpAddress(String ipAddress) {
        // 使用原生MyBatis方法
        return ipRateLimitMapper.findByIpAddress(ipAddress);
    }

    @Override
    public void updateIpRateLimit(IpRateLimit ipRateLimit) {
        ipRateLimit.setUpdateTime(LocalDateTime.now());
        ipRateLimitMapper.update(ipRateLimit);
    }

    @Override
    public boolean isIpBlocked(String ipAddress) {
        Optional<IpRateLimit> limitOptional = findByIpAddress(ipAddress);
        if (limitOptional.isPresent()) {
            IpRateLimit limit = limitOptional.get();
            return limit.getIsBlocked() && limit.getBlockEndTime().isAfter(LocalDateTime.now());
        }
        return false;
    }

    @Override
    public boolean recordDownloadRequest(String ipAddress) {
        if (isIpBlocked(ipAddress)) {
            return true;
        }

        Optional<IpRateLimit> limitOptional = findByIpAddress(ipAddress);
        LocalDateTime now = LocalDateTime.now();

        if (limitOptional.isPresent()) {
            IpRateLimit limit = limitOptional.get();
            // 检查时间窗口是否过期
            if (limit.getWindowEndTime().isBefore(now)) {
                // 重置时间窗口和请求计数
                limit.setWindowStartTime(now);
                limit.setWindowEndTime(now.plus(TIME_WINDOW_SECONDS, ChronoUnit.SECONDS));
                limit.setRequestCount(1);
            } else {
                // 增加请求计数
                limit.setRequestCount(limit.getRequestCount() + 1);
            }
            updateIpRateLimit(limit);

            // 检查是否超过最大请求数
            if (limit.getRequestCount() > MAX_REQUESTS) {
                blockIp(ipAddress);
                return true;
            }
        } else {
            // 创建新的访问限制记录
            IpRateLimit newLimit = new IpRateLimit();
            newLimit.setIpAddress(ipAddress);
            newLimit.setRequestCount(1);
            newLimit.setWindowStartTime(now);
            newLimit.setWindowEndTime(now.plus(TIME_WINDOW_SECONDS, ChronoUnit.SECONDS));
            newLimit.setIsBlocked(false);
            saveIpRateLimit(newLimit);
        }

        return false;
    }

    @Override
    public void blockIp(String ipAddress) {
        Optional<IpRateLimit> limitOptional = findByIpAddress(ipAddress);
        if (limitOptional.isPresent()) {
            IpRateLimit limit = limitOptional.get();
            limit.setIsBlocked(true);
            limit.setBlockEndTime(LocalDateTime.now().plus(BLOCK_DURATION_DAYS, ChronoUnit.DAYS));
            updateIpRateLimit(limit);
        } else {
            IpRateLimit newLimit = new IpRateLimit();
            newLimit.setIpAddress(ipAddress);
            newLimit.setRequestCount(0);
            newLimit.setWindowStartTime(LocalDateTime.now());
            newLimit.setWindowEndTime(LocalDateTime.now().plus(TIME_WINDOW_SECONDS, ChronoUnit.SECONDS));
            newLimit.setIsBlocked(true);
            newLimit.setBlockEndTime(LocalDateTime.now().plus(BLOCK_DURATION_DAYS, ChronoUnit.DAYS));
            saveIpRateLimit(newLimit);
        }
    }

    @Override
    public void deleteExpiredLimits() {
        ipRateLimitMapper.deleteExpiredLimits();
    }
}
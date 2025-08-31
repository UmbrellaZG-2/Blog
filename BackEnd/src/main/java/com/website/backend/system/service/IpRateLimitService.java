package com.website.backend.system.service;

import com.website.backend.system.entity.IpRateLimit;

import java.util.Optional;

public interface IpRateLimitService {

    /**
     * 保存IP访问限制记录
     */
    void saveIpRateLimit(IpRateLimit ipRateLimit);

    /**
     * 根据IP地址查找访问限制记录
     */
    Optional<IpRateLimit> findByIpAddress(String ipAddress);

    /**
     * 更新IP访问限制记录
     */
    void updateIpRateLimit(IpRateLimit ipRateLimit);

    /**
     * 判断IP是否被限制
     */
    boolean isRateLimited(String ip, String endpoint);

    /**
     * 记录访问限制
     */
    void recordRateLimit(String ip, String endpoint, long durationSeconds);

    /**
     * 阻止IP访问
     */
    void blockIp(String ipAddress);

    /**
     * 删除过期的访问限制记录
     */
    void clearExpiredRateLimits();
}
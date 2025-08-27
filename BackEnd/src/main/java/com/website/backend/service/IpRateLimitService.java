package com.website.backend.service;

import com.website.backend.entity.IpRateLimit;

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
     * 判断IP是否被阻止
     */
    boolean isIpBlocked(String ipAddress);

    /**
     * 记录下载请求
     */
    boolean recordDownloadRequest(String ipAddress);

    /**
     * 阻止IP访问
     */
    void blockIp(String ipAddress);

    /**
     * 删除过期的访问限制记录
     */
    void deleteExpiredLimits();
}
package com.website.backend.system.service.impl;

import com.website.backend.system.service.IpRateLimitService;
import com.website.backend.system.entity.IpRateLimit;
import com.website.backend.system.repository.IpRateLimitRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.time.LocalDateTime;
import java.util.Optional;

@Service
public class IpRateLimitServiceImpl implements IpRateLimitService {

    @Autowired
    private IpRateLimitRepository ipRateLimitRepository;

    @Override
    public void saveIpRateLimit(IpRateLimit ipRateLimit) {
        ipRateLimitRepository.save(ipRateLimit);
    }

    @Override
    public Optional<IpRateLimit> findByIpAddress(String ipAddress) {
        return ipRateLimitRepository.findByIpAddress(ipAddress);
    }

    @Override
    public void updateIpRateLimit(IpRateLimit ipRateLimit) {
        ipRateLimitRepository.save(ipRateLimit);
    }

    @Override
    public boolean isRateLimited(String ip, String endpoint) {
        Optional<IpRateLimit> rateLimitOptional = ipRateLimitRepository.findByIpAndEndpoint(ip, endpoint);
        if (rateLimitOptional.isPresent()) {
            IpRateLimit rateLimit = rateLimitOptional.get();
            return rateLimit.getExpiryTime().isAfter(LocalDateTime.now());
        }
        return false;
    }

    @Override
    public void recordRateLimit(String ip, String endpoint, long durationSeconds) {
        IpRateLimit rateLimit = new IpRateLimit();
        rateLimit.setIp(ip);
        rateLimit.setEndpoint(endpoint);
        rateLimit.setExpiryTime(LocalDateTime.now().plusSeconds(durationSeconds));
        ipRateLimitRepository.save(rateLimit);
    }

    @Override
    public void blockIp(String ipAddress) {
        // 实现阻止IP访问的逻辑
    }

    @Override
    public void clearExpiredRateLimits() {
        ipRateLimitRepository.deleteByExpiryTimeBefore(LocalDateTime.now());
    }
}
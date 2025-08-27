package com.website.backend.mapper;

import com.baomidou.mybatisplus.core.mapper.BaseMapper;
import com.website.backend.entity.IpRateLimit;
import org.apache.ibatis.annotations.Mapper;
import java.util.Optional;

@Mapper
public interface IpRateLimitMapper extends BaseMapper<IpRateLimit> {
    
    Optional<IpRateLimit> findByIpAddress(String ipAddress);
    
    int deleteExpiredLimits();
}
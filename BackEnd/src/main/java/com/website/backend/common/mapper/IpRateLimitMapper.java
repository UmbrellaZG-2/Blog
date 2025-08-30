package com.website.backend.common.mapper;

import com.website.backend.system.entity.IpRateLimit;
import org.apache.ibatis.annotations.*;

import java.util.List;
import java.util.Optional;

@Mapper
public interface IpRateLimitMapper {
    
    // 基本CRUD操作
    @Insert("INSERT INTO ip_rate_limit (id, ip_address, request_count, last_request_time) VALUES (#{id}, #{ipAddress}, #{requestCount}, #{lastRequestTime})")
    int insert(IpRateLimit ipRateLimit);
    
    @Select("SELECT * FROM ip_rate_limit WHERE id = #{id}")
    Optional<IpRateLimit> findById(Long id);
    
    @Select("SELECT * FROM ip_rate_limit")
    List<IpRateLimit> findAll();
    
    @Update("UPDATE ip_rate_limit SET ip_address = #{ipAddress}, request_count = #{requestCount}, last_request_time = #{lastRequestTime} WHERE id = #{id}")
    int update(IpRateLimit ipRateLimit);
    
    @Delete("DELETE FROM ip_rate_limit WHERE id = #{id}")
    int deleteById(Long id);
    
    // 自定义查询方法
    @Select("SELECT * FROM ip_rate_limit WHERE ip_address = #{ipAddress}")
    Optional<IpRateLimit> findByIpAddress(String ipAddress);
}
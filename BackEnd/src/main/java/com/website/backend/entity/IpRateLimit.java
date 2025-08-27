package com.website.backend.entity;

import lombok.Data;
import java.time.LocalDateTime;

@Data
public class IpRateLimit {

    private Long id;
    // 注意：自增ID需要在数据库中设置，MyBatis不会自动处理

    private String ipAddress;
    
    private Integer requestCount;
    
    private LocalDateTime windowStartTime;
    
    private LocalDateTime windowEndTime;
    
    private Boolean isBlocked;
    
    private LocalDateTime blockEndTime;
    
    private LocalDateTime createTime;
    
    private LocalDateTime updateTime;
    
    @Override
    public String toString() {
        return "IpRateLimit{" +
                "id=" + id +
                ", ipAddress='" + ipAddress + '\'' +
                ", requestCount=" + requestCount +
                ", isBlocked=" + isBlocked +
                '}';
    }
}
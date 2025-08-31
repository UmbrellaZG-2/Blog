package com.website.backend.system.entity;

import jakarta.persistence.*;
import lombok.Data;
import java.time.LocalDateTime;

@Data
@Entity
@Table(name = "ip_rate_limits")
public class IpRateLimit {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;
    // 注意：自增ID需要在数据库中设置，MyBatis不会自动处理

    private String ip;
    
    private String endpoint;
    
    private LocalDateTime expiryTime;
    
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
                ", ip='" + ip + '\'' +
                ", endpoint='" + endpoint + '\'' +
                ", expiryTime=" + expiryTime +
                ", ipAddress='" + ipAddress + '\'' +
                ", requestCount=" + requestCount +
                ", windowStartTime=" + windowStartTime +
                ", windowEndTime=" + windowEndTime +
                ", isBlocked=" + isBlocked +
                ", blockEndTime=" + blockEndTime +
                ", createTime=" + createTime +
                ", updateTime=" + updateTime +
                '}';
    }
}
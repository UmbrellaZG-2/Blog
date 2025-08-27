package com.website.backend.entity;

import com.baomidou.mybatisplus.annotation.IdType;
import com.baomidou.mybatisplus.annotation.TableId;
import com.baomidou.mybatisplus.annotation.TableName;
import lombok.Data;
import java.time.LocalDateTime;

@Data
@TableName("ip_rate_limits")
public class IpRateLimit {

    @TableId(type = IdType.AUTO)
    private Long id;

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
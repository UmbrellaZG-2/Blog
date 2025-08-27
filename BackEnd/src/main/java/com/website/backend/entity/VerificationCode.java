package com.website.backend.entity;

import lombok.Data;
import java.time.LocalDateTime;

@Data
public class VerificationCode {

    private Long id;
    // 注意：自增ID需要在数据库中设置，MyBatis不会自动处理

    private String username;
    
    private String code;
    
    private LocalDateTime expireTime;
    
    private LocalDateTime createTime;
    
    private LocalDateTime updateTime;
    
    @Override
    public String toString() {
        return "VerificationCode{" +
                "id=" + id +
                ", username='" + username + '\'' +
                ", code='" + code + '\'' +
                ", expireTime=" + expireTime +
                '}';
    }
}
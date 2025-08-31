package com.website.backend.system.entity;

import jakarta.persistence.*;
import lombok.Data;
import java.time.LocalDateTime;

@Data
@Entity
@Table(name = "verification_codes")
public class VerificationCode {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;
    // 注意：自增ID需要在数据库中设置，MyBatis不会自动处理

    private String email;
    
    private String code;
    
    private LocalDateTime expiryDate;
    
    private LocalDateTime createTime;
    
    private LocalDateTime updateTime;
    
    @Override
    public String toString() {
        return "VerificationCode{" +
                "id=" + id +
                ", email='" + email + '\'' +
                ", code='" + code + '\'' +
                ", expiryDate=" + expiryDate +
                ", createTime=" + createTime +
                ", updateTime=" + updateTime +
                '}';
    }
}
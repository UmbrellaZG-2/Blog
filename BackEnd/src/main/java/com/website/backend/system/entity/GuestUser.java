package com.website.backend.system.entity;

import lombok.Data;
import jakarta.persistence.*;
import java.io.Serializable;
import java.time.LocalDateTime;

@Data
@Entity
@Table(name = "guest_users")
public class GuestUser implements Serializable {

    private static final long serialVersionUID = 1L;

    @Id
    private String id;
    // 注意：UUID生成需要在代码中手动处理，MyBatis不会自动生成

    private String username;
    
    private String password;
    
    private String role;
    
    private LocalDateTime expireTime;
    
    private LocalDateTime createTime;
    
    private LocalDateTime updateTime;
    
    @Override
    public String toString() {
        return "GuestUser{" +
                "id='" + id + '\'' +
                ", username='" + username + '\'' +
                ", role='" + role + '\'' +
                ", expireTime=" + expireTime +
                '}';
    }
}
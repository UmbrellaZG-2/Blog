package com.website.backend.entity;

import com.baomidou.mybatisplus.annotation.IdType;
import com.baomidou.mybatisplus.annotation.TableId;
import com.baomidou.mybatisplus.annotation.TableName;
import lombok.Data;
import java.time.LocalDateTime;

@Data
@TableName("guest_users")
public class GuestUser {

    @TableId(type = IdType.ASSIGN_UUID)
    private String id;

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
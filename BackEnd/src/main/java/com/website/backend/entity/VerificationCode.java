package com.website.backend.entity;

import com.baomidou.mybatisplus.annotation.IdType;
import com.baomidou.mybatisplus.annotation.TableId;
import com.baomidou.mybatisplus.annotation.TableName;
import lombok.Data;
import java.time.LocalDateTime;

@Data
@TableName("verification_codes")
public class VerificationCode {

    @TableId(type = IdType.AUTO)
    private Long id;

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
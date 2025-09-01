package com.website.backend.system.entity;

import lombok.Data;
import jakarta.persistence.*;
import org.hibernate.annotations.GenericGenerator;
import java.io.Serializable;
import java.time.LocalDateTime;

@Data
@Entity
@Table(name = "guest_users")
public class GuestUser implements Serializable {

    private static final long serialVersionUID = 1L;

    @Id
    @GeneratedValue(generator = "uuid2")
    @GenericGenerator(name = "uuid2", strategy = "uuid2")
    @Column(columnDefinition = "VARCHAR(36)")
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
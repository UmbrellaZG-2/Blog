package com.website.backend.mapper;

import com.website.backend.entity.GuestUser;
import org.apache.ibatis.annotations.*;

import java.util.List;
import java.util.Optional;

@Mapper
public interface GuestUserMapper {
    
    // 基本CRUD操作
    @Insert("INSERT INTO guest_users (id, username, password, role, create_time, update_time, expire_time) VALUES (#{id}, #{username}, #{password}, #{role}, #{createTime}, #{updateTime}, #{expireTime})")
    int insert(GuestUser guestUser);
    
    @Select("SELECT * FROM guest_users WHERE id = #{id}")
    Optional<GuestUser> findById(String id);
    
    @Select("SELECT * FROM guest_users")
    List<GuestUser> findAll();
    
    @Update("UPDATE guest_users SET username = #{username}, password = #{password}, role = #{role}, update_time = #{updateTime}, expire_time = #{expireTime} WHERE id = #{id}")
    int update(GuestUser guestUser);
    
    @Delete("DELETE FROM guest_users WHERE id = #{id}")
    int deleteById(String id);
    
    // 自定义查询方法
    Optional<GuestUser> findByUsername(String username);
    
    int deleteExpiredGuests();
}
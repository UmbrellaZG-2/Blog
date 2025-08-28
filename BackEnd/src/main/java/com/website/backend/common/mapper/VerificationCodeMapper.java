package com.website.backend.common.mapper;

import com.website.backend.system.entity.VerificationCode;
import org.apache.ibatis.annotations.*;

import java.util.List;
import java.util.Optional;

@Mapper
public interface VerificationCodeMapper {
    
    // 基本CRUD操作
    @Insert("INSERT INTO verification_code (id, username, code, create_time, expire_time) VALUES (#{id}, #{username}, #{code}, #{createTime}, #{expireTime})")
    int insert(VerificationCode verificationCode);
    
    @Select("SELECT * FROM verification_code WHERE id = #{id}")
    Optional<VerificationCode> findById(String id);
    
    @Select("SELECT * FROM verification_code")
    List<VerificationCode> findAll();
    
    @Update("UPDATE verification_code SET username = #{username}, code = #{code}, create_time = #{createTime}, expire_time = #{expireTime} WHERE id = #{id}")
    int update(VerificationCode verificationCode);
    
    @Delete("DELETE FROM verification_code WHERE id = #{id}")
    int deleteById(String id);
    
    // 自定义查询方法
    @Select("SELECT * FROM verification_code WHERE username = #{username}")
    Optional<VerificationCode> findByUsername(String username);
}
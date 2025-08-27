package com.website.backend.mapper;

import com.baomidou.mybatisplus.core.mapper.BaseMapper;
import com.website.backend.entity.VerificationCode;
import org.apache.ibatis.annotations.Mapper;
import java.util.Optional;

@Mapper
public interface VerificationCodeMapper extends BaseMapper<VerificationCode> {
    
    Optional<VerificationCode> findByUsername(String username);
    
    int deleteExpiredCodes();
}
package com.website.backend.service;

import com.website.backend.entity.VerificationCode;

import java.util.Optional;

public interface VerificationCodeService {

    /**
     * 保存验证码到数据库
     */
    void saveVerificationCode(VerificationCode verificationCode);

    /**
     * 根据用户名查找验证码
     */
    Optional<VerificationCode> findByUsername(String username);

    /**
     * 删除验证码
     */
    void deleteVerificationCode(Long id);

    /**
     * 删除过期的验证码
     */
    void deleteExpiredCodes();
    
    /**
     * 生成验证码
     * @param email 邮箱
     * @return 生成的验证码
     */
    String generateCode(String email);
    
    /**
     * 验证验证码
     * @param email 邮箱
     * @param code 验证码
     * @return 验证结果
     */
    boolean validateCode(String email, String code);
}
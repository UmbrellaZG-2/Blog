package com.website.backend.system.service;

public interface VerificationCodeService {

    /**
     * 生成验证码
     */
    String generateCode(String email);

    /**
     * 验证验证码
     */
    boolean validateCode(String email, String code);

    /**
     * 删除验证码
     */
    void deleteVerificationCode(String email);

    /**
     * 删除过期验证码
     */
    void deleteExpiredCodes();
}
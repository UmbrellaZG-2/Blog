package com.website.backend.system.service;

import com.website.backend.system.entity.VerificationCode;

import java.util.Optional;

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
}
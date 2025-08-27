package com.website.backend.service.impl;

import com.website.backend.entity.VerificationCode;
import com.website.backend.mapper.VerificationCodeMapper;
import com.website.backend.service.VerificationCodeService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.time.LocalDateTime;
import java.util.Optional;

@Service
public class VerificationCodeServiceImpl implements VerificationCodeService {

    @Autowired
    private VerificationCodeMapper verificationCodeMapper;

    @Override
    public void saveVerificationCode(VerificationCode verificationCode) {
        verificationCode.setCreateTime(LocalDateTime.now());
        verificationCode.setUpdateTime(LocalDateTime.now());
        verificationCodeMapper.insert(verificationCode);
    }

    @Override
    public Optional<VerificationCode> findByUsername(String username) {
        // 使用原生MyBatis方法
        return verificationCodeMapper.findByUsername(username);
    }

    @Override
    public void deleteVerificationCode(Long id) {
        // 将Long类型的id转换为String类型
        verificationCodeMapper.deleteById(String.valueOf(id));
    }

    @Override
    public void deleteExpiredCodes() {
        verificationCodeMapper.deleteExpiredCodes();
    }
}
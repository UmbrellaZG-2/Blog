package com.website.backend.service.impl;

import com.website.backend.entity.VerificationCode;
import com.website.backend.mapper.VerificationCodeMapper;
import com.website.backend.service.VerificationCodeService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.time.LocalDateTime;
import java.util.Optional;
import java.util.Random;

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
    
    @Override
    public String generateCode(String email) {
        // 生成6位随机数字验证码
        Random random = new Random();
        StringBuilder code = new StringBuilder();
        for (int i = 0; i < 6; i++) {
            code.append(random.nextInt(10));
        }
        
        // 创建验证码实体
        VerificationCode verificationCode = new VerificationCode();
        verificationCode.setUsername(email);
        verificationCode.setCode(code.toString());
        verificationCode.setExpireTime(LocalDateTime.now().plusMinutes(5)); // 5分钟有效期
        
        // 保存到数据库
        saveVerificationCode(verificationCode);
        
        return code.toString();
    }
    
    @Override
    public boolean validateCode(String email, String code) {
        Optional<VerificationCode> optionalCode = findByUsername(email);
        if (optionalCode.isPresent()) {
            VerificationCode verificationCode = optionalCode.get();
            // 检查验证码是否匹配且未过期
            if (verificationCode.getCode().equals(code) && 
                verificationCode.getExpireTime().isAfter(LocalDateTime.now())) {
                // 验证成功后删除验证码
                deleteVerificationCode(verificationCode.getId());
                return true;
            }
        }
        return false;
    }
}
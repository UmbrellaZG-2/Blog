package com.website.backend.system.service.impl;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.stereotype.Service;

import com.website.backend.system.service.VerificationCodeService;

import java.util.Random;
import java.util.concurrent.TimeUnit;

@Service
public class VerificationCodeServiceImpl implements VerificationCodeService {

    @Autowired
    private RedisTemplate<String, String> redisTemplate;

    private final Random random = new Random();
    
    // 验证码过期时间（分钟）
    private static final long CODE_EXPIRE_MINUTES = 5;
    
    // Redis中验证码的key前缀
    private static final String VERIFICATION_CODE_PREFIX = "verification:code:";

    @Override
    public String generateCode(String email) {
        // 生成6位数字验证码
        String code = String.format("%06d", random.nextInt(999999));
        
        // 将验证码存储到Redis中，设置过期时间
        String key = VERIFICATION_CODE_PREFIX + email;
        redisTemplate.opsForValue().set(key, code, CODE_EXPIRE_MINUTES, TimeUnit.MINUTES);
        
        System.out.println("Generated verification code for email " + email + ": " + code);
        
        return code;
    }

    @Override
    public boolean validateCode(String email, String code) {
        String key = VERIFICATION_CODE_PREFIX + email;
        String storedCode = redisTemplate.opsForValue().get(key);
        
        if (storedCode != null && storedCode.equals(code)) {
            // 验证成功后删除验证码
            redisTemplate.delete(key);
            return true;
        }
        
        return false;
    }

    @Override
    public void deleteVerificationCode(String email) {
        String key = VERIFICATION_CODE_PREFIX + email;
        redisTemplate.delete(key);
    }

    @Override
    public void deleteExpiredCodes() {
        // Redis会自动处理过期键的删除，这里不需要额外实现
        // 如果需要手动清理，可以使用Redis的SCAN命令查找并删除过期键
        System.out.println("清理过期验证码任务执行完成 - Redis自动处理过期键");
    }
}
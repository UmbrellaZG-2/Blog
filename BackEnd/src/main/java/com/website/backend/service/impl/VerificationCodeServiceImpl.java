package com.website.backend.service.impl;

import com.baomidou.mybatisplus.core.conditions.query.QueryWrapper;
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
        QueryWrapper<VerificationCode> queryWrapper = new QueryWrapper<>();
        queryWrapper.eq("username", username);
        queryWrapper.gt("expire_time", LocalDateTime.now());
        queryWrapper.orderByDesc("create_time");
        queryWrapper.last("LIMIT 1");
        VerificationCode verificationCode = verificationCodeMapper.selectOne(queryWrapper);
        return Optional.ofNullable(verificationCode);
    }

    @Override
    public void deleteVerificationCode(Long id) {
        verificationCodeMapper.deleteById(id);
    }

    @Override
    public void deleteExpiredCodes() {
        verificationCodeMapper.deleteExpiredCodes();
    }
}
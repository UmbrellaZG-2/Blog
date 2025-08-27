package com.website.backend.service.impl;

import com.website.backend.entity.GuestUser;
import com.website.backend.mapper.GuestUserMapper;
import com.website.backend.service.GuestUserService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.time.LocalDateTime;
import java.util.Optional;

@Service
public class GuestUserServiceImpl implements GuestUserService {

    @Autowired
    private GuestUserMapper guestUserMapper;

    @Override
    public void saveGuestUser(GuestUser guestUser) {
        guestUser.setCreateTime(LocalDateTime.now());
        guestUser.setUpdateTime(LocalDateTime.now());
        guestUserMapper.insert(guestUser);
    }

    @Override
    public Optional<GuestUser> findByUsername(String username) {
        // 使用原生MyBatis方法
        return guestUserMapper.findByUsername(username);
    }

    @Override
    public boolean existsByUsername(String username) {
        // 直接调用findByUsername并检查是否存在
        return guestUserMapper.findByUsername(username).isPresent();
    }

    @Override
    public void deleteExpiredGuests() {
        guestUserMapper.deleteExpiredGuests();
    }
}
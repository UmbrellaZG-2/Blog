package com.website.backend.service.impl;

import com.baomidou.mybatisplus.core.conditions.query.QueryWrapper;
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
        QueryWrapper<GuestUser> queryWrapper = new QueryWrapper<>();
        queryWrapper.eq("username", username);
        queryWrapper.gt("expire_time", LocalDateTime.now());
        GuestUser guestUser = guestUserMapper.selectOne(queryWrapper);
        return Optional.ofNullable(guestUser);
    }

    @Override
    public boolean existsByUsername(String username) {
        QueryWrapper<GuestUser> queryWrapper = new QueryWrapper<>();
        queryWrapper.eq("username", username);
        queryWrapper.gt("expire_time", LocalDateTime.now());
        return guestUserMapper.selectCount(queryWrapper) > 0;
    }

    @Override
    public void deleteExpiredGuests() {
        guestUserMapper.deleteExpiredGuests();
    }
}
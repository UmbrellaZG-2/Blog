package com.website.backend.service;

import com.website.backend.entity.GuestUser;

import java.util.Optional;

public interface GuestUserService {

    /**
     * 保存游客用户到数据库
     */
    void saveGuestUser(GuestUser guestUser);

    /**
     * 根据用户名查找游客用户
     */
    Optional<GuestUser> findByUsername(String username);

    /**
     * 检查游客用户是否存在
     */
    boolean existsByUsername(String username);

    /**
     * 删除过期的游客用户
     */
    void deleteExpiredGuests();
}
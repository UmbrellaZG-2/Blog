package com.website.backend.system.service.impl;

import com.website.backend.system.service.GuestUserService;
import com.website.backend.system.entity.GuestUser;
import com.website.backend.system.repository.GuestUserRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.time.LocalDateTime;
import java.util.List;
import java.util.Optional;

@Service
public class GuestUserServiceImpl implements GuestUserService {

    @Autowired
    private GuestUserRepository guestUserRepository;

    @Override
    public GuestUser saveGuestUser(GuestUser guestUser) {
        return guestUserRepository.save(guestUser);
    }

    @Override
    public Optional<GuestUser> findByUsername(String username) {
        return guestUserRepository.findByUsername(username);
    }

    @Override
    public boolean existsByUsername(String username) {
        return guestUserRepository.existsByUsername(username);
    }

    @Override
    public void deleteExpiredGuests() {
        List<GuestUser> expiredGuests = guestUserRepository.findByExpireTimeBefore(LocalDateTime.now());
        if (!expiredGuests.isEmpty()) {
            guestUserRepository.deleteAll(expiredGuests);
        }
    }
}

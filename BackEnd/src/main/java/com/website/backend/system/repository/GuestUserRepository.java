package com.website.backend.system.repository;

import com.website.backend.system.entity.GuestUser;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.time.LocalDateTime;
import java.util.List;
import java.util.Optional;

@Repository
public interface GuestUserRepository extends JpaRepository<GuestUser, Long> {
    
    Optional<GuestUser> findByUsername(String username);
    
    boolean existsByUsername(String username);
    
    @Query("SELECT g FROM GuestUser g WHERE g.expireTime < :time")
    List<GuestUser> findByExpireTimeBefore(@Param("time") LocalDateTime time);
}
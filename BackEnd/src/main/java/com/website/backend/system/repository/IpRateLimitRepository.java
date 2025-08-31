package com.website.backend.system.repository;

import com.website.backend.system.entity.IpRateLimit;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.time.LocalDateTime;
import java.util.Optional;

@Repository
public interface IpRateLimitRepository extends JpaRepository<IpRateLimit, Long> {
    Optional<IpRateLimit> findByIpAddress(String ipAddress);
    
    Optional<IpRateLimit> findByIpAndEndpoint(String ip, String endpoint);
    
    @Query("SELECT i FROM IpRateLimit i WHERE i.expiryTime < :time")
    void deleteByExpiryTimeBefore(@Param("time") LocalDateTime time);
}
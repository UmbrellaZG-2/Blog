package com.website.backend.system.repository;

import com.website.backend.system.entity.VerificationCode;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.time.LocalDateTime;
import java.util.Optional;

@Repository
public interface VerificationCodeRepository extends JpaRepository<VerificationCode, Long> {
    Optional<VerificationCode> findByEmailAndCode(String email, String code);
    
    @Query("SELECT v FROM VerificationCode v WHERE v.expiryDate < :time")
    void deleteByExpiryDateBefore(@Param("time") LocalDateTime time);
}
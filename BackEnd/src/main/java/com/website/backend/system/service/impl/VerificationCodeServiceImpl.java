package com.website.backend.system.service.impl;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.mail.javamail.JavaMailSender;
import org.springframework.mail.javamail.MimeMessageHelper;
import org.springframework.stereotype.Service;

import com.website.backend.system.service.VerificationCodeService;
import com.website.backend.system.entity.VerificationCode;
import com.website.backend.system.repository.VerificationCodeRepository;

import jakarta.mail.MessagingException;
import jakarta.mail.internet.MimeMessage;
import java.time.LocalDateTime;
import java.util.Optional;
import java.util.Random;

@Service
public class VerificationCodeServiceImpl implements VerificationCodeService {

    @Autowired
    private VerificationCodeRepository verificationCodeRepository;

    @Autowired
    private JavaMailSender mailSender;

    private final Random random = new Random();

    @Override
    public String generateCode(String email) {
        String code = String.format("%06d", random.nextInt(999999));
        VerificationCode verificationCode = new VerificationCode();
        verificationCode.setEmail(email);
        verificationCode.setCode(code);
        verificationCode.setExpiryDate(LocalDateTime.now().plusMinutes(5));
        verificationCodeRepository.save(verificationCode);
        return code;
    }

    @Override
    public boolean validateCode(String email, String code) {
        Optional<VerificationCode> verificationCode = verificationCodeRepository.findByEmailAndCode(email, code);
        if (verificationCode.isPresent()) {
            VerificationCode vc = verificationCode.get();
            if (vc.getExpiryDate().isAfter(LocalDateTime.now())) {
                verificationCodeRepository.delete(vc);
                return true;
            } else {
                verificationCodeRepository.delete(vc);
            }
        }
        return false;
    }

    @Override
    public void sendVerificationEmail(String to, String code) throws MessagingException {
        MimeMessage message = mailSender.createMimeMessage();
        MimeMessageHelper helper = new MimeMessageHelper(message, true);
        helper.setTo(to);
        helper.setSubject("验证码");
        helper.setText("您的验证码是: " + code + "，有效期5分钟", true);
        mailSender.send(message);
    }

    @Override
    public void deleteVerificationCode(Long id) {
        verificationCodeRepository.deleteById(id);
    }

    @Override
    public void deleteExpiredCodes() {
        verificationCodeRepository.deleteByExpiryDateBefore(LocalDateTime.now());
    }
}
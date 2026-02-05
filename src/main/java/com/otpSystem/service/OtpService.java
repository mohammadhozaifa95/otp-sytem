package com.otpSystem.service;



import java.time.LocalDateTime;
import java.util.List;
import java.util.Random;

import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import com.otpSystem.dto.OtpRequest;
import com.otpSystem.dto.OtpResponse;
import com.otpSystem.entity.OtpEntity;
import com.otpSystem.repositry.OtpRepository;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;

@Service
@RequiredArgsConstructor
@Slf4j
public class OtpService {

    private final OtpRepository otpRepository;
    private final Random random = new Random();

    private static final int OTP_EXPIRY_SECONDS = 30;

    // Separate method without @Transactional for API calls
    public OtpResponse generateOtp(OtpRequest request) {
        try {
            return generateOtpInternal(request);
        } catch (Exception e) {
            log.error("Error generating OTP: {}", e.getMessage(), e);
            throw new RuntimeException("Failed to generate OTP");
        }
    }

    // Internal method with transaction
    @Transactional
    public OtpResponse generateOtpInternal(OtpRequest request) {
        if (request.getDigits() != 3 && request.getDigits() != 6) {
            throw new IllegalArgumentException("Only 3 or 6 digit OTP allowed");
        }

        String otp = generateOtpByDigits(request.getDigits());
        LocalDateTime now = LocalDateTime.now();

        OtpEntity entity = new OtpEntity();
        entity.setOtp(otp);
        entity.setDigits(request.getDigits());
        entity.setCreatedAt(now);
        entity.setExpiresAt(now.plusSeconds(OTP_EXPIRY_SECONDS));
        entity.setIsExpired(false);

        otpRepository.save(entity);
        
        log.info("Generated OTP: {} ({} digits)", otp, request.getDigits());
        
        return new OtpResponse(otp);
    }

    // Remove @Transactional from scheduled method
    @Scheduled(fixedRate = 30000)
    public void autoGenerateOtp() {
        try {
            log.info("Auto-generating OTPs...");
            autoGenerateOtpInternal();
        } catch (Exception e) {
            log.error("Error in auto-generating OTP: {}", e.getMessage(), e);
        }
    }

    @Transactional
    public void autoGenerateOtpInternal() {
        LocalDateTime now = LocalDateTime.now();
        List<OtpEntity> expiredOtps = otpRepository.findExpiredOtps(now);

        log.info("Found {} expired OTPs", expiredOtps.size());

        for (OtpEntity oldOtp : expiredOtps) {
            // Mark old as expired
            oldOtp.setIsExpired(true);
            otpRepository.save(oldOtp);

            // Generate new OTP
            String newOtpValue = generateOtpByDigits(oldOtp.getDigits());

            OtpEntity newOtp = new OtpEntity();
            newOtp.setOtp(newOtpValue);
            newOtp.setDigits(oldOtp.getDigits());
            newOtp.setCreatedAt(now);
            newOtp.setExpiresAt(now.plusSeconds(OTP_EXPIRY_SECONDS));
            newOtp.setIsExpired(false);

            otpRepository.save(newOtp);

            log.info("OTP refreshed: {} -> {} ({} digits)", 
                oldOtp.getOtp(), newOtpValue, newOtp.getDigits());
        }
    }

    private String generateOtpByDigits(int digits) {
        if (digits == 3) {
            return String.format("%03d", 100 + random.nextInt(900));
        }
        return String.format("%06d", 100000 + random.nextInt(900000));
    }
}

    
        
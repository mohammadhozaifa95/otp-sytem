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

@Service
@RequiredArgsConstructor
@Transactional
public class OtpService {

    private final OtpRepository otpRepository;
    private final Random random = new Random();

    private static final int OTP_EXPIRY_SECONDS = 30;

   
    public OtpResponse generateOtp(OtpRequest request) {

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

        return new OtpResponse(otp); // ONLY OTP
    }

  
    @Scheduled(fixedRate = 30000)
    public void autoGenerateOtp() {

        List<OtpEntity> expiredOtps =
                otpRepository.findExpiredOtps(LocalDateTime.now());

        for (OtpEntity oldOtp : expiredOtps) {

        
            oldOtp.setIsExpired(true);
            otpRepository.save(oldOtp);

           
            String newOtpValue = generateOtpByDigits(oldOtp.getDigits());

            OtpEntity newOtp = new OtpEntity();
            newOtp.setOtp(newOtpValue);
            newOtp.setDigits(oldOtp.getDigits());
            newOtp.setCreatedAt(LocalDateTime.now());
            newOtp.setExpiresAt(LocalDateTime.now().plusSeconds(OTP_EXPIRY_SECONDS));
            newOtp.setIsExpired(false);

            otpRepository.save(newOtp);

            System.out.println(
                "OTP refreshed: " + oldOtp.getOtp() +
                " -> " + newOtpValue +
                " | Digits: " + newOtp.getDigits()
            );
        }
    }

    private String generateOtpByDigits(int digits) {

        if (digits == 3) {
            return String.valueOf(100 + random.nextInt(900));
        }

        return String.valueOf(100000 + random.nextInt(900000));
    }
}

    
        
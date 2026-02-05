package com.otpSystem.controller;


import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.CrossOrigin;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import com.otpSystem.dto.OtpRequest;
import com.otpSystem.dto.OtpResponse;
import com.otpSystem.service.OtpService;

import lombok.RequiredArgsConstructor;

@RestController
@CrossOrigin("*")
@RequestMapping("/api/otp")
@RequiredArgsConstructor
public class OtpController {

    private final OtpService otpService;

    @PostMapping("/generate")
    public ResponseEntity<OtpResponse> generateOtp(
            @RequestBody OtpRequest request) {

        OtpResponse response = otpService.generateOtp(request);
        return ResponseEntity.ok(response);
    }
}

    

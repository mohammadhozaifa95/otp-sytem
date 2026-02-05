package com.otpSystem;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.scheduling.annotation.EnableScheduling;

@SpringBootApplication
@EnableScheduling
public class OtpSystemApplication {

	public static void main(String[] args) {
		SpringApplication.run(OtpSystemApplication.class, args);
	}

}

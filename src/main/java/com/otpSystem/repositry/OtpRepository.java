package com.otpSystem.repositry;



import java.time.LocalDateTime;
import java.util.List;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import com.otpSystem.entity.OtpEntity;

@Repository
public interface OtpRepository extends JpaRepository<OtpEntity, Long> {

    // Expired but not marked
    @Query("SELECT o FROM OtpEntity o WHERE o.expiresAt <= :now AND o.isExpired = false")
    List<OtpEntity> findExpiredOtps(@Param("now") LocalDateTime now);

}

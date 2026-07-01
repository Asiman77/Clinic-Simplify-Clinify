package az.clinify.demo.otp;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.Optional;

@Repository
public interface OtpCodeRepository extends JpaRepository<OtpCode, Long> {

    // Nömrəyə görə OTP məlumatını gətirir (Həm update, həm də verify funksiyalarında istifadə edəcəksən)
package com.example.otp.repository;

import com.example.otp.entity.OtpCode;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.Optional;

@Repository
public interface OtpCodeRepository extends JpaRepository<OtpCode, Long> {

    // Last Added Otp code
    Optional<OtpCode> findFirstByPhoneNumberOrderByIdDesc(String phoneNumber);
}}

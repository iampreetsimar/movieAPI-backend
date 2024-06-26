package com.example.movieApi.controllers;

import java.time.Instant;
import java.util.Date;
import java.util.Random;
import java.util.Objects;

import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import com.example.movieApi.auth.entities.ForgotPassword;
import com.example.movieApi.auth.entities.User;
import com.example.movieApi.auth.repositories.ForgotPasswordRepository;
import com.example.movieApi.auth.repositories.UserRepository;
import com.example.movieApi.auth.utils.ChangePassword;
import com.example.movieApi.dto.MailBody;
import com.example.movieApi.service.EmailService;

@RestController
@RequestMapping("/forgotPassword")
public class ForgotPasswordController {

    private final UserRepository userRepository;
    private final EmailService emailService;
    private final ForgotPasswordRepository forgotPasswordRepository;

    private final PasswordEncoder passwordEncoder;

    public ForgotPasswordController(UserRepository userRepository, EmailService emailService,
                                    ForgotPasswordRepository forgotPasswordRepository,
                                    PasswordEncoder passwordEncoder) {

        this.userRepository = userRepository;
        this.emailService = emailService;
        this.forgotPasswordRepository = forgotPasswordRepository;
        this.passwordEncoder = passwordEncoder;
    }
    
    @PostMapping("/verifyMail/{email}")
    public ResponseEntity<String> verifyEmailHandler(@PathVariable String email) {
        User user = userRepository.findByEmail(email)
            .orElseThrow(() -> new UsernameNotFoundException("Please provide a valid email"));

        int otp = otpGenerator();
        MailBody mailBody = MailBody.builder()
            .to(email)
            .text("This is the OTP for your Forgot Password request : " + otp)
            .subject("OTP for Forgot Password request")
            .build();

        ForgotPassword fp = ForgotPassword.builder()
            .otp(otp)
            .expirationTime(new Date(System.currentTimeMillis() + 70 * 1000))
            .user(user)
            .build();

        emailService.sendSimpleMessage(mailBody);
        forgotPasswordRepository.save(fp);

        return ResponseEntity.ok("Email sent for OTP verification");
    }

    @PostMapping("/verifyOtp/{otp}/{email}")
    public ResponseEntity<String> verifyOtpHandler(@PathVariable Integer otp, @PathVariable String email) {
        User user = userRepository.findByEmail(email)
            .orElseThrow(() -> new UsernameNotFoundException("Please provide a valid email"));

        ForgotPassword fp = forgotPasswordRepository.findByOtpAndUser(otp, user)
            .orElseThrow(() -> new RuntimeException("Invalid OTP for email: " + email));

        if(fp.getExpirationTime().before(Date.from(Instant.now()))) {
            forgotPasswordRepository.deleteById(fp.getFpId());
            return new ResponseEntity<>("OTP expired", HttpStatus.EXPECTATION_FAILED);
        }

        forgotPasswordRepository.deleteById(fp.getFpId());  // to remove forgotPassword record from db
        return ResponseEntity.ok("OTP verified");
    }

    @PostMapping("/changePassword/{email}")
    public ResponseEntity<String> changePasswordHandler(@RequestBody ChangePassword changePassword,
                                                        @PathVariable String email) {

        if(!Objects.equals(changePassword.password(), changePassword.repeatPassword())) {
            return new ResponseEntity<>("Password mismatch! Password must match", HttpStatus.EXPECTATION_FAILED);
        }

        String encodedPassword = passwordEncoder.encode(changePassword.password());
        userRepository.updatePassword(email, encodedPassword);
        return ResponseEntity.ok("Password has been changed");
    }

    private int otpGenerator() {
        Random random = new Random();
        return random.nextInt(000_000, 999_999);
    }
}

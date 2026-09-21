package vn.iotstar.service.impl;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;
import vn.iotstar.entity.OtpToken;
import vn.iotstar.repository.OtpTokenRepository;
import vn.iotstar.service.EmailService;
import vn.iotstar.service.OtpService;
import java.security.SecureRandom;
import java.time.LocalDateTime;

@Service
public class OtpServiceImpl implements OtpService {

    private static final int OTP_MINUTES = 5;

    @Autowired
    private OtpTokenRepository otpTokenRepository;

    @Autowired
    private EmailService emailService;

    @Autowired
    private PasswordEncoder passwordEncoder;

    private String generateOtp() {
        SecureRandom random = new SecureRandom();
        int number = random.nextInt(900000) + 100000;
        return String.valueOf(number);
    }

    private void send(String email, String type, String subject) {
        otpTokenRepository.deleteByEmailAndType(email, type);
        String otp = generateOtp();
        OtpToken token = new OtpToken();
        token.setEmail(email);
        token.setType(type);
        token.setOtpHash(passwordEncoder.encode(otp));
        token.setExpiresAt(LocalDateTime.now().plusMinutes(OTP_MINUTES));
        token.setUsed(false);
        token.setCreatedAt(LocalDateTime.now());
        otpTokenRepository.save(token);

        emailService.sendOtp(email, otp, subject);
    }

    @Override
    public void sendRegisterOtp(String email) {
        send(email, "REGISTER", "Xác nhận đăng ký tài khoản - Mã OTP");
    }

    @Override
    public void sendResetPasswordOtp(String email) {
        send(email, "RESET_PASSWORD", "Yêu cầu đặt lại mật khẩu - Mã OTP");
    }

    private boolean verify(String email, String otp, String type) {
        return otpTokenRepository.findTopByEmailAndTypeAndUsedFalseOrderByCreatedAtDesc(email, type)
                .map(token -> {
                    if (token.getExpiresAt().isBefore(LocalDateTime.now())) {
                        return false;
                    }
                    if (!passwordEncoder.matches(otp, token.getOtpHash())) {
                        return false;
                    }
                    token.setUsed(true);
                    otpTokenRepository.save(token);
                    return true;
                }).orElse(false);
    }

    @Override
    public boolean verifyRegisterOtp(String email, String otp) {
        return verify(email, otp, "REGISTER");
    }

    @Override
    public boolean verifyResetPasswordOtp(String email, String otp) {
        return verify(email, otp, "RESET_PASSWORD");
    }
}

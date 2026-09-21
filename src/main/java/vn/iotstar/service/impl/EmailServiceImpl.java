package vn.iotstar.service.impl;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.mail.SimpleMailMessage;
import org.springframework.mail.javamail.JavaMailSender;
import org.springframework.stereotype.Service;
import vn.iotstar.service.EmailService;

@Service
public class EmailServiceImpl implements EmailService {

    @Autowired(required = false)
    private JavaMailSender mailSender;

    @Override
    public void sendOtp(String email, String otp, String subject) {
        System.out.println("==================================================");
        System.out.println(" [MOCK EMAIL SERVICE] SENDING OTP TO: " + email);
        System.out.println(" SUBJECT: " + subject);
        System.out.println(" OTP CODE: " + otp);
        System.out.println("==================================================");

        if (mailSender != null) {
            try {
                SimpleMailMessage message = new SimpleMailMessage();
                message.setTo(email);
                message.setSubject(subject);
                message.setText("Mã xác thực OTP của bạn là: " + otp + "\nHiệu lực trong 5 phút. Vui lòng không chia sẻ mã này.");
                mailSender.send(message);
            } catch (Exception ignored) {
            }
        }
    }
}

package vn.iotstar.controller;

import jakarta.validation.Valid;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.validation.BindingResult;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.ModelAttribute;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.servlet.mvc.support.RedirectAttributes;
import vn.iotstar.dto.ForgotPasswordDTO;
import vn.iotstar.dto.RegisterDTO;
import vn.iotstar.dto.VerifyOtpDTO;
import vn.iotstar.service.AuthService;
import vn.iotstar.service.OtpService;

@Controller
public class AuthController {

    @Autowired
    private AuthService authService;

    @Autowired
    private OtpService otpService;

    @GetMapping("/login")
    public String login() {
        return "auth/login";
    }

    @GetMapping("/access-denied")
    public String accessDenied() {
        return "auth/access-denied";
    }

    @GetMapping("/register")
    public String registerForm(Model model) {
        model.addAttribute("registerDTO", new RegisterDTO());
        return "auth/register";
    }

    @PostMapping("/register")
    public String handleRegister(@Valid @ModelAttribute("registerDTO") RegisterDTO dto,
                                 BindingResult result,
                                 RedirectAttributes redirect,
                                 Model model) {
        if (result.hasErrors()) {
            return "auth/register";
        }
        try {
            authService.register(dto);
            redirect.addFlashAttribute("success", "Mã OTP xác nhận đã được gửi đến " + dto.getEmail());
            return "redirect:/verify-otp?email=" + dto.getEmail();
        } catch (IllegalArgumentException e) {
            model.addAttribute("errorMessage", e.getMessage());
            return "auth/register";
        }
    }

    @GetMapping("/verify-otp")
    public String verifyOtpForm(@RequestParam(value = "email", required = false) String email, Model model) {
        VerifyOtpDTO dto = new VerifyOtpDTO();
        if (email != null) {
            dto.setEmail(email);
        }
        model.addAttribute("verifyOtpDTO", dto);
        return "auth/verify-otp";
    }

    @PostMapping("/verify-otp")
    public String handleVerifyOtp(@Valid @ModelAttribute("verifyOtpDTO") VerifyOtpDTO dto,
                                  BindingResult result,
                                  RedirectAttributes redirect,
                                  Model model) {
        if (result.hasErrors()) {
            return "auth/verify-otp";
        }
        boolean ok = authService.verifyRegister(dto.getEmail(), dto.getOtp());
        if (ok) {
            redirect.addFlashAttribute("verified", true);
            return "redirect:/login?verified=true";
        } else {
            model.addAttribute("errorMessage", "Mã OTP không đúng hoặc đã hết hạn (quá 5 phút).");
            return "auth/verify-otp";
        }
    }

    @PostMapping("/resend-register-otp")
    public String handleResendOtp(@RequestParam("email") String email, RedirectAttributes redirect) {
        try {
            otpService.sendRegisterOtp(email);
            redirect.addFlashAttribute("success", "Đã gửi lại mã OTP mới đến " + email);
        } catch (Exception e) {
            redirect.addFlashAttribute("errorMessage", "Lỗi gửi lại OTP: " + e.getMessage());
        }
        return "redirect:/verify-otp?email=" + email;
    }

    @GetMapping("/forgot-password")
    public String forgotPasswordForm(Model model) {
        model.addAttribute("forgotPasswordDTO", new ForgotPasswordDTO());
        return "auth/forgot-password";
    }

    @PostMapping("/forgot-password")
    public String handleForgotPassword(@RequestParam("email") String email,
                                       RedirectAttributes redirect,
                                       Model model) {
        try {
            authService.forgotPassword(email);
            redirect.addFlashAttribute("success", "Mã OTP khôi phục mật khẩu đã được gửi đến " + email);
            return "redirect:/reset-password?email=" + email;
        } catch (IllegalArgumentException e) {
            model.addAttribute("errorMessage", e.getMessage());
            model.addAttribute("forgotPasswordDTO", new ForgotPasswordDTO(email, null, null, null));
            return "auth/forgot-password";
        }
    }

    @GetMapping("/reset-password")
    public String resetPasswordForm(@RequestParam(value = "email", required = false) String email, Model model) {
        ForgotPasswordDTO dto = new ForgotPasswordDTO();
        if (email != null) {
            dto.setEmail(email);
        }
        model.addAttribute("forgotPasswordDTO", dto);
        return "auth/reset-password";
    }

    @PostMapping("/reset-password")
    public String handleResetPassword(@ModelAttribute("forgotPasswordDTO") ForgotPasswordDTO dto,
                                      RedirectAttributes redirect,
                                      Model model) {
        if (dto.getPassword() == null || dto.getPassword().length() < 6) {
            model.addAttribute("errorMessage", "Mật khẩu mới phải từ 6 ký tự trở lên.");
            return "auth/reset-password";
        }
        if (!dto.getPassword().equals(dto.getConfirmPassword())) {
            model.addAttribute("errorMessage", "Xác nhận mật khẩu không khớp.");
            return "auth/reset-password";
        }

        boolean valid = authService.verifyResetOtp(dto.getEmail(), dto.getOtp());
        if (!valid) {
            model.addAttribute("errorMessage", "Mã OTP không đúng hoặc đã hết hạn.");
            return "auth/reset-password";
        }

        authService.resetPassword(dto.getEmail(), dto.getPassword());
        redirect.addFlashAttribute("resetSuccess", true);
        return "redirect:/login?reset=true";
    }
}

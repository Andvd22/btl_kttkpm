package btl.kttkpm.quanlybanhangsieuthi.controller;

import btl.kttkpm.quanlybanhangsieuthi.dto.LoginRequest;
import btl.kttkpm.quanlybanhangsieuthi.dto.RegisterRequest;
import btl.kttkpm.quanlybanhangsieuthi.entity.User;
import btl.kttkpm.quanlybanhangsieuthi.service.AuthService;
import jakarta.servlet.http.HttpSession;
import jakarta.validation.Valid;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.validation.BindingResult;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.ModelAttribute;
import org.springframework.web.bind.annotation.PostMapping;

@Controller
public class AuthController {

    private final AuthService authService;

    public AuthController(AuthService authService) {
        this.authService = authService;
    }

    @GetMapping({"/", "/login"})
    public String loginPage(Model model) {
        if (!model.containsAttribute("loginRequest")) {
            model.addAttribute("loginRequest", new LoginRequest());
        }
        return "Login";
    }

    @GetMapping("/register")
    public String registerPage(Model model) {
        if (!model.containsAttribute("registerRequest")) {
            model.addAttribute("registerRequest", new RegisterRequest());
        }
        return "Register";
    }

    @PostMapping("/login")
    public String doLogin(
            @Valid @ModelAttribute("loginRequest") LoginRequest loginRequest,
            BindingResult bindingResult,
            HttpSession session,
            Model model) {
        if (bindingResult.hasErrors()) {
            return "Login";
        }
        User user = authService.login(loginRequest.getUsername(), loginRequest.getPassword());
        if (user == null) {
            model.addAttribute("error", "Sai tài khoản hoặc mật khẩu");
            return "Login";
        }
        session.setAttribute("currentUser", user);
        return "redirect:/home";
    }

    @PostMapping("/register")
    public String doRegister(
            @Valid @ModelAttribute("registerRequest") RegisterRequest registerRequest,
            BindingResult bindingResult,
            Model model) {
        if (bindingResult.hasErrors()) {
            return "Register";
        }
        try {
            authService.register(
                    registerRequest.getFullName(),
                    registerRequest.getPhoneNumber(),
                    registerRequest.getUsername(),
                    registerRequest.getPassword(),
                    registerRequest.getConfirmPassword(),
                    registerRequest.getAddress(),
                    registerRequest.getEmail());
            model.addAttribute("success", "Dang ky thanh cong. Vui long dang nhap");
            model.addAttribute("loginRequest", new LoginRequest());
            return "Login";
        } catch (IllegalArgumentException ex) {
            model.addAttribute("error", ex.getMessage());
            return "Register";
        }
    }

    @GetMapping("/logout")
    public String logout(HttpSession session) {
        session.invalidate();
        return "redirect:/login";
    }
}

package com.fatmakahveci.blog.controller;

import java.security.Principal;

import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.servlet.mvc.support.RedirectAttributes;

import com.fatmakahveci.blog.config.AdminMfaFilter;
import com.fatmakahveci.blog.dao.UserRepository;
import com.fatmakahveci.blog.model.BlogUser;
import com.fatmakahveci.blog.service.TotpService;

import jakarta.servlet.http.HttpSession;

@Controller
public class MfaController {

    private static final String PENDING_SECRET = "ADMIN_MFA_PENDING_SECRET";
    private final UserRepository userRepository;
    private final TotpService totpService;

    public MfaController(UserRepository userRepository, TotpService totpService) {
        this.userRepository = userRepository;
        this.totpService = totpService;
    }

    @GetMapping("/mfa")
    public String challenge(Principal principal, HttpSession session, Model model) {
        BlogUser user = administrator(principal);
        if (Boolean.TRUE.equals(session.getAttribute(AdminMfaFilter.VERIFIED_SESSION_ATTRIBUTE))) {
            return "redirect:/";
        }
        if (!user.isMfaEnabled()) {
            String secret = (String) session.getAttribute(PENDING_SECRET);
            if (secret == null) {
                secret = totpService.generateSecret();
                session.setAttribute(PENDING_SECRET, secret);
            }
            model.addAttribute("setup", true);
            model.addAttribute("secret", secret);
            model.addAttribute("provisioningUri", totpService.provisioningUri(user.getUsername(), secret));
        } else {
            model.addAttribute("setup", false);
        }
        return "mfa";
    }

    @PostMapping("/mfa")
    public String verify(
            @RequestParam("code") String code,
            Principal principal,
            HttpSession session,
            RedirectAttributes redirectAttributes) {
        BlogUser user = administrator(principal);
        String secret;
        if (user.isMfaEnabled()) {
            secret = totpService.decryptSecret(user.getMfaSecretEncrypted());
        } else {
            secret = (String) session.getAttribute(PENDING_SECRET);
        }
        if (!totpService.verify(secret, code.trim())) {
            redirectAttributes.addFlashAttribute("mfaError", "The verification code is invalid or expired.");
            return "redirect:/mfa";
        }
        if (!user.isMfaEnabled()) {
            user.setMfaSecretEncrypted(totpService.encryptSecret(secret));
            user.setMfaEnabled(true);
            userRepository.save(user);
            session.removeAttribute(PENDING_SECRET);
        }
        session.setAttribute(AdminMfaFilter.VERIFIED_SESSION_ATTRIBUTE, true);
        return "redirect:/";
    }

    private BlogUser administrator(Principal principal) {
        if (principal == null) {
            throw new IllegalStateException("Authenticated administrator is required");
        }
        return userRepository.findByUsernameIgnoreCase(principal.getName())
                .filter(user -> user.getRole().name().equals("ADMIN"))
                .orElseThrow(() -> new IllegalStateException("Authenticated administrator is required"));
    }
}

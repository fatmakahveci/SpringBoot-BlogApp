package com.fatmakahveci.blog.controllers;

import static org.assertj.core.api.Assertions.assertThat;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.never;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

import java.security.Principal;
import java.util.Optional;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.mock.web.MockHttpSession;
import org.springframework.ui.ConcurrentModel;
import org.springframework.web.servlet.mvc.support.RedirectAttributesModelMap;

import com.fatmakahveci.blog.config.AdminMfaFilter;
import com.fatmakahveci.blog.controller.MfaController;
import com.fatmakahveci.blog.dao.UserRepository;
import com.fatmakahveci.blog.model.BlogUser;
import com.fatmakahveci.blog.model.UserRole;
import com.fatmakahveci.blog.service.TotpService;

@ExtendWith(MockitoExtension.class)
class MfaControllerTests {

    @Mock
    private UserRepository userRepository;

    @Mock
    private TotpService totpService;

    private MfaController controller;
    private BlogUser administrator;
    private final Principal principal = () -> "admin";

    @BeforeEach
    void setUp() {
        controller = new MfaController(userRepository, totpService);
        administrator = new BlogUser("admin", "encoded", UserRole.ADMIN);
        when(userRepository.findByUsernameIgnoreCase("admin")).thenReturn(Optional.of(administrator));
    }

    @Test
    void createsAnEnrollmentKeyForAnAdministratorWithoutMfa() {
        MockHttpSession session = new MockHttpSession();
        ConcurrentModel model = new ConcurrentModel();
        when(totpService.generateSecret()).thenReturn("GENERATEDKEY");
        when(totpService.provisioningUri("admin", "GENERATEDKEY")).thenReturn("otpauth://enrollment");

        String view = controller.challenge(principal, session, model);

        assertThat(view).isEqualTo("mfa");
        assertThat(model.getAttribute("setup")).isEqualTo(true);
        assertThat(model.getAttribute("secret")).isEqualTo("GENERATEDKEY");
        assertThat(model.getAttribute("provisioningUri")).isEqualTo("otpauth://enrollment");
    }

    @Test
    void redirectsAnAlreadyVerifiedAdministrator() {
        MockHttpSession session = new MockHttpSession();
        session.setAttribute(AdminMfaFilter.VERIFIED_SESSION_ATTRIBUTE, true);

        assertThat(controller.challenge(principal, session, new ConcurrentModel())).isEqualTo("redirect:/");
        verify(totpService, never()).generateSecret();
    }

    @Test
    void rejectsAnInvalidEnrollmentCodeWithoutPersistingTheKey() {
        MockHttpSession session = new MockHttpSession();
        session.setAttribute("ADMIN_MFA_PENDING_SECRET", "PENDINGKEY");
        when(totpService.verify("PENDINGKEY", "000000")).thenReturn(false);
        RedirectAttributesModelMap redirect = new RedirectAttributesModelMap();

        String view = controller.verify("000000", principal, session, redirect);

        assertThat(view).isEqualTo("redirect:/mfa");
        assertThat(redirect.getFlashAttributes()).containsKey("mfaError");
        verify(userRepository, never()).save(any());
    }

    @Test
    void enablesMfaAfterAValidEnrollmentCode() {
        MockHttpSession session = new MockHttpSession();
        session.setAttribute("ADMIN_MFA_PENDING_SECRET", "PENDINGKEY");
        when(totpService.verify("PENDINGKEY", "123456")).thenReturn(true);
        when(totpService.encryptSecret("PENDINGKEY")).thenReturn("encrypted");

        String view = controller.verify(" 123456 ", principal, session, new RedirectAttributesModelMap());

        assertThat(view).isEqualTo("redirect:/");
        assertThat(administrator.isMfaEnabled()).isTrue();
        assertThat(administrator.getMfaSecretEncrypted()).isEqualTo("encrypted");
        assertThat(session.getAttribute(AdminMfaFilter.VERIFIED_SESSION_ATTRIBUTE)).isEqualTo(true);
        verify(userRepository).save(administrator);
    }

    @Test
    void verifiesAnAlreadyEnrolledAdministratorWithoutReplacingTheKey() {
        administrator.setMfaEnabled(true);
        administrator.setMfaSecretEncrypted("encrypted");
        when(totpService.decryptSecret("encrypted")).thenReturn("DECRYPTEDKEY");
        when(totpService.verify("DECRYPTEDKEY", "654321")).thenReturn(true);

        String view = controller.verify(
                "654321", principal, new MockHttpSession(), new RedirectAttributesModelMap());

        assertThat(view).isEqualTo("redirect:/");
        verify(userRepository, never()).save(any());
    }
}

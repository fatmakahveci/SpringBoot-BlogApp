package com.fatmakahveci.blog.controllers;

import static org.assertj.core.api.Assertions.assertThat;
import static org.mockito.Mockito.when;

import java.util.List;

import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.ui.ConcurrentModel;

import com.fatmakahveci.blog.controller.AdminAuditController;
import com.fatmakahveci.blog.dao.AdminAuditEventRepository;

@ExtendWith(MockitoExtension.class)
class AdminAuditControllerTests {

    @Mock
    private AdminAuditEventRepository repository;

    @Test
    void displaysTheMostRecentAdministratorEvents() {
        when(repository.findTop100ByOrderByOccurredAtDesc()).thenReturn(List.of());
        ConcurrentModel model = new ConcurrentModel();

        String view = new AdminAuditController(repository).auditLog(model);

        assertThat(view).isEqualTo("admin_audit");
        assertThat(model.getAttribute("events")).isEqualTo(List.of());
    }
}

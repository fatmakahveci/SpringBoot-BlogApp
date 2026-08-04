package com.fatmakahveci.blog.controller;

import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;

import com.fatmakahveci.blog.dao.AdminAuditEventRepository;

@Controller
public class AdminAuditController {

    private final AdminAuditEventRepository repository;

    public AdminAuditController(AdminAuditEventRepository repository) {
        this.repository = repository;
    }

    @GetMapping("/admin/audit")
    public String auditLog(Model model) {
        model.addAttribute("events", repository.findTop100ByOrderByOccurredAtDesc());
        return "admin_audit";
    }
}

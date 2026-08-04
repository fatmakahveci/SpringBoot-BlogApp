package com.fatmakahveci.blog.dao;

import java.util.List;

import org.springframework.data.jpa.repository.JpaRepository;

import com.fatmakahveci.blog.model.AdminAuditEvent;

public interface AdminAuditEventRepository extends JpaRepository<AdminAuditEvent, Integer> {
    List<AdminAuditEvent> findTop100ByOrderByOccurredAtDesc();
}

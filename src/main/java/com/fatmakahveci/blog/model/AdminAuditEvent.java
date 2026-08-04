package com.fatmakahveci.blog.model;

import java.time.Instant;

import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.Table;

@Entity
@Table(name = "admin_audit_events")
public class AdminAuditEvent {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(columnDefinition = "INTEGER")
    private Integer id;

    @Column(name = "occurred_at", nullable = false, updatable = false)
    private Instant occurredAt;

    @Column(nullable = false, length = 50)
    private String username;

    @Column(name = "http_method", nullable = false, length = 10)
    private String httpMethod;

    @Column(name = "request_path", nullable = false, length = 300)
    private String requestPath;

    @Column(name = "response_status", nullable = false)
    private int responseStatus;

    @Column(name = "client_ip", nullable = false, length = 64)
    private String clientIp;

    @Column(name = "request_id", length = 64)
    private String requestId;

    protected AdminAuditEvent() {
    }

    public AdminAuditEvent(
            Instant occurredAt,
            String username,
            String httpMethod,
            String requestPath,
            int responseStatus,
            String clientIp,
            String requestId) {
        this.occurredAt = occurredAt;
        this.username = username;
        this.httpMethod = httpMethod;
        this.requestPath = requestPath;
        this.responseStatus = responseStatus;
        this.clientIp = clientIp;
        this.requestId = requestId;
    }

    public Integer getId() { return id; }
    public Instant getOccurredAt() { return occurredAt; }
    public String getUsername() { return username; }
    public String getHttpMethod() { return httpMethod; }
    public String getRequestPath() { return requestPath; }
    public int getResponseStatus() { return responseStatus; }
    public String getClientIp() { return clientIp; }
    public String getRequestId() { return requestId; }
}

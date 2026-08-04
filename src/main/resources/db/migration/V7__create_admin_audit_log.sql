CREATE TABLE admin_audit_events (
    id INTEGER PRIMARY KEY AUTOINCREMENT,
    occurred_at TIMESTAMP NOT NULL,
    username VARCHAR(50) NOT NULL,
    http_method VARCHAR(10) NOT NULL,
    request_path VARCHAR(300) NOT NULL,
    response_status INTEGER NOT NULL,
    client_ip VARCHAR(64) NOT NULL,
    request_id VARCHAR(64),
    CONSTRAINT chk_admin_audit_status CHECK (response_status BETWEEN 100 AND 599)
);

CREATE INDEX idx_admin_audit_occurred_at ON admin_audit_events (occurred_at DESC);
CREATE INDEX idx_admin_audit_username ON admin_audit_events (username, occurred_at DESC);


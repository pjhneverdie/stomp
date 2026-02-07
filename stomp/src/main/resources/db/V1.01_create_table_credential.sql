CREATE TABLE credential (
    id INT PRIMARY KEY, -- @MapsId
    balance INT NOT NULL DEFAULT 1, -- priavte int balance = 1;
    last_free_awarded_at DATETIME NOT NULL DEFAULT CURRENT_TIMESTAMP, -- private LocalDateTime lastFreeAwardedAt = LocalDateTime.now();
    last_ad_awarded_at DATETIME NOT NULL DEFAULT '1970-01-01 00:00:00' -- private LocalDateTime lastAdAwardedAt = LocalDateTime.of(1970, 1, 1, 0, 0);,
    created_at DATETIME NOT NULL, -- @Auditing
    updated_at DATETIME NOT NULL -- @Auditing
    CONSTRAINT fk_member_credential FOREIGN KEY (member_id) REFERENCES Member (id) ON DELETE CASCADE
)
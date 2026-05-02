CREATE TABLE IF NOT EXISTS credential (
    member_id INT PRIMARY KEY,
    balance INT NOT NULL DEFAULT 1,
    last_free_awarded_at DATETIME NOT NULL DEFAULT CURRENT_TIMESTAMP,
    last_ad_awarded_at DATETIME NOT NULL DEFAULT '1970-01-01 00:00:00',
    created_at DATETIME NOT NULL,
    updated_at DATETIME NOT NULL,
    CONSTRAINT fk_member_credential
        FOREIGN KEY (member_id) REFERENCES member (id) ON DELETE CASCADE)
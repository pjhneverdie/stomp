CREATE TABLE IF NOT EXISTS trial (
    id BINARY(16) PRIMARY KEY,
    issue_title VARCHAR(255) NOT NULL,
    trial_stage VARCHAR(255) NOT NULL,
    created_at DATETIME NOT NULL,
    updated_at DATETIME NOT NULL
)
CREATE TABLE IF NOT EXISTS chat_room (
    id INT PRIMARY KEY AUTO_INCREMENT,
    chat_room_uuid VARCHAR(36) NOT NULL UNIQUE,
    issue_title VARCHAR(50) NOT NULL,
    trial_stage VARCHAR(50) NOT NULL,
    created_at DATETIME NOT NULL,
    updated_at DATETIME NOT NULL
)
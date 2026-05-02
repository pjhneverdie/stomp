CREATE TABLE IF NOT EXISTS chat_room_member (
    id INT PRIMARY KEY AUTO_INCREMENT,
    chat_room_uuid VARCHAR(36) NOT NULL,
    member_id INT NOT NULL,
    nickname VARCHAR(50) NOT NULL,
    trial_stage VARCHAR(50) NOT NULL,
    created_at DATETIME NOT NULL,
    updated_at DATETIME NOT NULL
)
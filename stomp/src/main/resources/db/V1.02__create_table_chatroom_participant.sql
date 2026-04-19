CREATE TABLE IF NOT EXISTS chatroom_participant (
    id INT PRIMARY KEY AUTO_INCREMENT,
    room_id VARCHAR(36) NOT NULL,
    member_id INT NOT NULL UNIQUE,
    created_at DATETIME NOT NULL,
    updated_at DATETIME NOT NULL,
    CONSTRAINT fk_chatroom_participant FOREIGN KEY (member_id) REFERENCES member (id) ON DELETE CASCADE
)
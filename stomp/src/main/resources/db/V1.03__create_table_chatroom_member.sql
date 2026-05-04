CREATE TABLE IF NOT EXISTS chat_room_member (
    id INT PRIMARY KEY AUTO_INCREMENT,
    chat_room_uuid VARCHAR(36) NOT NULL,
    member_id INT NOT NULL,
    nickname VARCHAR NOT NULL,
    trial_stage VARCHAR NOT NULL,
    created_at DATETIME NOT NULL,
    updated_at DATETIME NOT NULL,
    CONSTRAINT fk_chat_room FOREIGN KEY (chat_room_uuid) REFERENCES chat_room (chat_room_uuid) ON DELETE CASCADE,
    CONSTRAINT fk_chat_room_member FOREIGN KEY (member_id) REFERENCES member (id) ON DELETE CASCADE
)
CREATE TABLE IF NOT EXISTS chat_message (
    id INT PRIMARY KEY AUTO_INCREMENT,
    chat_room_uuid VARCHAR(36) NOT NULL,
    chat_room_member_id INT,
    seq BIGINT NOT NULL,
    message_type VARCHAR(255) NOT NULL,
    created_at DATETIME NOT NULL,
    updated_at DATETIME NOT NULL,
    kafka_partition INT NOT NULL,
    kafka_offset BIGINT NOT NULL,
    CONSTRAINT fk_chat_message_chat_room FOREIGN KEY (chat_room_uuid) REFERENCES chat_room (chat_room_uuid) ON DELETE CASCADE,
    CONSTRAINT fk_chat_message_chat_room_member FOREIGN KEY (chat_room_member_id) REFERENCES chat_room_member (id) ON DELETE CASCADE,
    INDEX idx_chat_message_partition_offset(kafka_partition, kafka_offset DESC)
)
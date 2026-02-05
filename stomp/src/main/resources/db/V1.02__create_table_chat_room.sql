CREATE TABLE chat_room (
    id INT PRIMARY KEY AUTO_INCREMENT, -- GenerationType.IDENTITY
    share_token VARCHAR(255) UNIQUE NOT NULL, -- + NOT BLANK
    status VARCHAR(30) NOT NULL DEFAULT 'ACTIVE', -- @Enumerated private ChatStatus chatStatus = ChatStatus.ACTIVE.toString();
    created_at DATETIME NOT NULL, -- @Auditing
    updated_at DATETIME NOT NULL -- @Auditing
);
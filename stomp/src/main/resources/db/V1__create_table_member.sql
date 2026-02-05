CREATE TABLE member (
    id INT PRIMARY KEY AUTO_INCREMENT, -- GenerationType.IDENTITY
    email VARCHAR(30) UNIQUE NOT NULL, -- + NOT BLANK + REGEX
    nickname VARCHAR(15) UNIQUE NOT NULL, -- + NOT BLANK
    role VARCHAR(30) NOT NULL, -- @Enumerated private MemberRole memberRole = MemberRole.FREE.toString();
    created_at DATETIME NOT NULL, -- @Auditing
    updated_at DATETIME NOT NULL -- @Auditing
);
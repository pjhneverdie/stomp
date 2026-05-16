CREATE TABLE IF NOT EXISTS member (
    id INT PRIMARY KEY AUTO_INCREMENT,
    email VARCHAR UNIQUE NOT NULL,
    picture VARCHAR NOT NULL,
    role VARCHAR NOT NULL,
    credential_id INT NOT NULL UNIQUE,
    created_at DATETIME NOT NULL,
    updated_at DATETIME NOT NULL,
    CONSTRAINT fk_credential FOREIGN KEY (credential) REFERENCES credential (id) ON DELETE CASCADE
);
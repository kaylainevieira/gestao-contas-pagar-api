CREATE TABLE roles (
    id UUID PRIMARY KEY DEFAULT gen_random_uuid(),
    name VARCHAR(50) NOT NULL UNIQUE
);

INSERT INTO roles (name) VALUES ('ROLE_ADMIN'), ('ROLE_USER');

CREATE TABLE usuarios (
    id UUID PRIMARY KEY DEFAULT gen_random_uuid(),
    username VARCHAR(50) NOT NULL UNIQUE,
    password VARCHAR(255) NOT NULL
);

CREATE TABLE usuario_roles (
    usuario_id UUID NOT NULL,
    role_id UUID NOT NULL,
    PRIMARY KEY (usuario_id, role_id),
    FOREIGN KEY (usuario_id) REFERENCES usuarios(id) ON DELETE CASCADE,
    FOREIGN KEY (role_id) REFERENCES roles(id) ON DELETE CASCADE
);

INSERT INTO usuarios (username, password) VALUES ('admin', '$2a$10$zwGQlR6MzVA5YgQvSLprBeczAMDUBGTXYkxp8g.nOyW1gPkVVFZEi');

INSERT INTO usuario_roles (usuario_id, role_id)
SELECT u.id, r.id FROM usuarios u, roles r
WHERE u.username = 'admin' AND r.name = 'ROLE_ADMIN';


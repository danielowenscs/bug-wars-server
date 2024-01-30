INSERT INTO roles (name)
VALUES ('ROLE_ADMIN'), ('ROLE_USER'),('ROLE_GUEST')
ON CONFLICT (name) DO NOTHING;

--username: test_user
--password: sausages
INSERT INTO users (username, password, email)
VALUES
    ('test_user' ,'$2a$10$PcSvcvAMh0UjiS8CsiNbzulmxR4ua0g3PDg.eNQGTfwXPOQdUIMZC', 'test_user@gmail.com')
ON CONFLICT (username) DO NOTHING;

--username: user123
--password: bananas
INSERT INTO users (username, password, email)
VALUES
    ('user123' ,'$2b$12$K8v/9enGmptX7wCNWcOpwuF35f.k7RYvgu50KMi/xVvgdKsAvp/y6', 'user123@gmail.com')
ON CONFLICT (username) DO NOTHING;

--username: guest_user
--password: sausages
INSERT INTO users (username, password, email)
VALUES
    ('guest_user' ,'$2a$10$PcSvcvAMh0UjiS8CsiNbzulmxR4ua0g3PDg.eNQGTfwXPOQdUIMZC', 'teamchillguest@gmail.com')
ON CONFLICT (username) DO NOTHING;

--username: admin_user
--password: sausages
INSERT INTO users (username, password, email)
VALUES
    ('admin_user' ,'$2a$10$PcSvcvAMh0UjiS8CsiNbzulmxR4ua0g3PDg.eNQGTfwXPOQdUIMZC', 'admin123@gmail.com')
ON CONFLICT (username) DO NOTHING;

INSERT INTO user_roles (user_id, role_id)
VALUES
    ((SELECT id FROM users WHERE username = 'test_user'), (SELECT id FROM roles WHERE name = 'ROLE_USER'))
ON CONFLICT (user_id, role_id) DO NOTHING;

INSERT INTO user_roles (user_id, role_id)
VALUES
    ((SELECT id FROM users WHERE username = 'user123'), (SELECT id FROM roles WHERE name = 'ROLE_USER'))
ON CONFLICT (user_id, role_id) DO NOTHING;

INSERT INTO user_roles (user_id, role_id)
VALUES
    ((SELECT id FROM users WHERE username = 'guest_user'), (SELECT id FROM roles WHERE name = 'ROLE_GUEST'))
ON CONFLICT (user_id, role_id) DO NOTHING;

INSERT INTO user_roles (user_id, role_id)
VALUES
    ((SELECT id FROM users WHERE username = 'admin_user'), (SELECT id FROM roles WHERE name = 'ROLE_ADMIN'))
ON CONFLICT (user_id, role_id) DO NOTHING;

--Scripts
INSERT INTO scripts (name,body,date_created,date_updated,owner_id)
VALUES
('Script 1','START','2024-01-01','2024-01-03',(SELECT id FROM users WHERE username = 'test_user')),
('Script 2','ATTACK','2024-01-04','2024-01-04',(SELECT id FROM users WHERE username = 'test_user')),
('Script 3','EAT','2024-01-06','2024-01-10',(SELECT id FROM users WHERE username = 'user123')),
('Script 4','FINISH','2024-01-09','2024-01-09',(SELECT id FROM users WHERE username = 'test_user')),
('Script 5','FORWARD','2024-01-16','2024-01-18',(SELECT id FROM users WHERE username = 'user123')),
('Script 6','LEFT','2024-01-20','2024-01-21',(SELECT id FROM users WHERE username = 'test_user'));

--guest scripts, for all users
INSERT INTO scripts (name,body,date_created,date_updated,owner_id)
VALUES
('Guest Script A','FORWARD','2024-01-28','2024-01-28',(SELECT id FROM users WHERE username = 'guest_user')),
('Guest Script B','LEFT','2024-01-28','2024-01-28',(SELECT id FROM users WHERE username = 'guest_user')),
('Guest Script C','RIGHT','2024-01-28','2024-01-28',(SELECT id FROM users WHERE username = 'guest_user')),
('Guest Script D','EAT','2024-01-28','2024-01-28',(SELECT id FROM users WHERE username = 'guest_user'));

--GameMaps
INSERT INTO game_maps (name,height,width,body)
VALUES
(E'Willy Wonka's Doghouse',5,5,E'11111\n10001\n10101\n11011\n11111'),
('Crusader Saloon',7,7,E'1111111\n1000001\n1000001\n1000001\n1000001\n1000001\n1111111'),
('Fiery Dragon Layer',3,3,E'111\n101\n111');


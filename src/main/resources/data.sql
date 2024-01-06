INSERT INTO roles (name)
VALUES ('ROLE_ADMIN'), ('ROLE_USER')
ON CONFLICT (name) DO NOTHING;

--username: test_user
--password: sausages
INSERT INTO users (username, password, email)
VALUES
    ('test_user' ,'$2a$10$PcSvcvAMh0UjiS8CsiNbzulmxR4ua0g3PDg.eNQGTfwXPOQdUIMZC', 'test_user@gmail.com')
ON CONFLICT (username) DO NOTHING;

INSERT INTO user_roles (user_id, role_id)
VALUES
    ((SELECT id FROM users WHERE username = 'test_user'), (SELECT id FROM roles WHERE name = 'ROLE_USER'))
ON CONFLICT (user_id, role_id) DO NOTHING;

INSERT INTO scripts (date_created, date_updated, owner_id, script_id, body, script_name) 
VALUES ('2023-12-25', '2024-01-01', 1, 1, 'This is a test', 'Test Script' );

INSERT INTO scripts (date_created, date_updated, owner_id, script_id, body, script_name) 
VALUES ('2023-01-05', '2024-01-05', 1, 2, 'This script should be created when the server is run', 'Description of Test Script' );
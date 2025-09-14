-- Sample test data for the banking system
-- This file will be executed on application startup if spring.jpa.hibernate.ddl-auto is set to create or create-drop

-- Insert sample users (passwords are BCrypt encoded for 'password123')
INSERT INTO users (username, password, email, full_name, created_at) VALUES 
('john_doe', '$2a$10$N.zmdr9k7uOCQb376NoUnuTJ8iYqiSfFGjO6NaaXIkVbDzNmsEm9G', 'john@example.com', 'John Doe', NOW()),
('jane_smith', '$2a$10$N.zmdr9k7uOCQb376NoUnuTJ8iYqiSfFGjO6NaaXIkVbDzNmsEm9G', 'jane@example.com', 'Jane Smith', NOW());

-- Insert sample accounts
INSERT INTO accounts (account_number, balance, account_type, user_id, created_at) VALUES 
('1234567890', 1000.00, 'SAVINGS', 1, NOW()),
('0987654321', 2500.50, 'CHECKING', 1, NOW()),
('1111222233', 5000.00, 'SAVINGS', 2, NOW());

-- Insert sample transactions
INSERT INTO transactions (amount, transaction_type, description, account_id, created_at) VALUES 
(1000.00, 'DEPOSIT', 'Initial deposit', 1, NOW()),
(2500.50, 'DEPOSIT', 'Initial deposit', 2, NOW()),
(5000.00, 'DEPOSIT', 'Initial deposit', 3, NOW()),
(100.00, 'WITHDRAWAL', 'ATM withdrawal', 1, NOW()),
(50.00, 'DEPOSIT', 'Cash deposit', 2, NOW());
-- Fix Database Passwords - Run this to make login work
-- This updates all passwords to plain text format

USE banking_system;

-- Update admin user password to plain text
-- Admin email: admin@bank.com
-- Admin password: admin123
UPDATE users SET password = 'admin123' WHERE email = 'admin@bank.com';

-- If you have other users in the database, you need to update their passwords too
-- For example, if you have a user with email 'user@example.com' and you want password 'password123':
-- UPDATE users SET password = 'password123' WHERE email = 'user@example.com';

-- To see all users and their current passwords:
SELECT id, email, password, role FROM users;

-- IMPORTANT: After running this, you can login with:
-- Admin: email = admin@bank.com, password = admin123
-- Other users: Use the plain text password you set


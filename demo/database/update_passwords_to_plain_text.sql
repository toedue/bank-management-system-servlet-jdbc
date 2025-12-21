-- This script updates all passwords in the database to plain text
-- Run this in MySQL to fix login issues

USE banking_system;

-- Update admin password to plain text "admin123"
UPDATE users SET password = 'admin123' WHERE email = 'admin@bank.com';

-- If you have other users, update them too
-- Example: UPDATE users SET password = 'yourpassword' WHERE email = 'user@example.com';

-- Check what passwords are currently in the database
SELECT email, password, role FROM users;


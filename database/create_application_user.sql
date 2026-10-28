-- ==============================================================================
-- Student Placement Management System — Dedicated Application User Provisioning
-- ==============================================================================
-- Usage Instructions:
-- 1. Open MySQL Workbench 8.0 CE or the MySQL 8.0 Command Line Client as root.
-- 2. Replace '<YOUR_SECURE_PASSWORD>' below with your chosen application password.
-- 3. Execute this script.
-- ==============================================================================

-- 1. Ensure placement_db exists (preserves all existing tables and data)
CREATE DATABASE IF NOT EXISTS placement_db;

-- 2. Create the unprivileged application service user
CREATE USER IF NOT EXISTS 'placement_user'@'localhost' IDENTIFIED BY '<YOUR_SECURE_PASSWORD>';

-- 3. Grant minimum required application privileges on placement_db only
--    (CRUD data access + Hibernate schema update capabilities)
GRANT SELECT, INSERT, UPDATE, DELETE, CREATE, ALTER, INDEX, REFERENCES, DROP 
ON placement_db.* TO 'placement_user'@'localhost';

-- 4. Apply privilege updates
FLUSH PRIVILEGES;

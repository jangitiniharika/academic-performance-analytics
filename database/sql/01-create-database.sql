-- ------------------------------------------------------------------
-- 01-create-database.sql
-- Creates the application schema and a dedicated (least privilege)
-- development user. Hibernate creates/updates the TABLES at runtime
-- (spring.jpa.hibernate.ddl-auto=update), so this script only owns
-- the database + user + grants.
-- Idempotent: safe to run many times.
-- ------------------------------------------------------------------

CREATE DATABASE IF NOT EXISTS academic_analytics
    CHARACTER SET utf8mb4
    COLLATE utf8mb4_unicode_ci;

-- The Spring Boot app connects with this dedicated user, NEVER as root.
CREATE USER IF NOT EXISTS 'academic_user'@'localhost' IDENTIFIED BY 'Academic@2026';
CREATE USER IF NOT EXISTS 'academic_user'@'127.0.0.1' IDENTIFIED BY 'Academic@2026';

GRANT ALL PRIVILEGES ON academic_analytics.* TO 'academic_user'@'localhost';
GRANT ALL PRIVILEGES ON academic_analytics.* TO 'academic_user'@'127.0.0.1';

FLUSH PRIVILEGES;

SELECT 'academic_analytics ready' AS status;

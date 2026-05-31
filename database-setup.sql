-- ============================================================
--  GRAND HOTEL MANAGEMENT SYSTEM - MySQL Setup Script
--  Run this in MySQL Workbench or MySQL Command Line
-- ============================================================

-- Step 1: Create the database
CREATE DATABASE IF NOT EXISTS hotel_db;
USE hotel_db;

-- ============================================================
-- NOTE: Spring Boot will auto-create all tables on first run.
-- This script only creates the DB and seeds essential data.
-- ============================================================

-- Step 2: (Optional) Verify tables after running the backend
-- After you run: mvn spring-boot:run
-- the following tables will be created automatically:
--   users, rooms, bookings, complaints, bills,
--   room_amenities, bill_service_items

-- ============================================================
-- Step 3: Admin & Staff accounts are created automatically
-- by DataInitializer.java when backend starts.
-- But if you want to insert them manually, use these:
-- ============================================================

-- PASSWORD HASHES (BCrypt for "Admin@1234" and "Staff@1234")
-- Admin password: Admin@1234
-- Staff password: Staff@1234

-- These are inserted automatically by the backend.
-- Only run the INSERT below if the backend fails to seed them:

/*
INSERT IGNORE INTO users (
  user_id, customer_name, email, country_code, mobile_number,
  address, username, password, role, status,
  failed_login_attempts, locked, must_change_password
) VALUES
(
  'USR-ADMIN-001', 'Hotel Admin', 'admin@hotel.com', '+91', '9000000001',
  'Hotel Main Office', 'admin',
  '$2a$10$N.zmdr9k7uOCQb376NoUnuTJ8iAt6Z5EHsM8RrNim/espFVEGW6fW',
  'ADMIN', 'ACTIVE', 0, 0, 0
),
(
  'USR-STAFF-001', 'Hotel Staff', 'staff@hotel.com', '+91', '9000000002',
  'Hotel Staff Office', 'staff1',
  '$2a$10$slYQmyNdgTY18LMwnbgGO.Y2v8nYb38FHWqmjbHtFMb1gWwHSAJha',
  'STAFF', 'ACTIVE', 0, 0, 0
);
*/

-- ============================================================
-- Step 4: (Optional) Add sample rooms for testing
-- Run this AFTER the backend has started at least once
-- (so the rooms table exists)
-- ============================================================

INSERT IGNORE INTO rooms (room_number, room_type, bed_type, price_per_night, status, max_occupancy, description, room_size)
VALUES
  ('101', 'STANDARD', 'SINGLE',  2500.00, 'AVAILABLE', 1, 'Cozy standard single room', 180),
  ('102', 'STANDARD', 'DOUBLE',  3500.00, 'AVAILABLE', 2, 'Comfortable standard double room', 220),
  ('201', 'DELUXE',   'QUEEN',   5500.00, 'AVAILABLE', 2, 'Spacious deluxe room with queen bed', 300),
  ('202', 'DELUXE',   'KING',    6500.00, 'AVAILABLE', 2, 'Luxurious deluxe king room', 350),
  ('301', 'SUITE',    'KING',   10000.00, 'AVAILABLE', 3, 'Premium suite with living area', 500),
  ('302', 'SUITE',    'KING',   12000.00, 'AVAILABLE', 3, 'Executive suite with city view', 550),
  ('401', 'SUPREME',  'KING',   20000.00, 'AVAILABLE', 4, 'Supreme penthouse suite', 800),
  ('103', 'STANDARD', 'DOUBLE',  3500.00, 'AVAILABLE', 2, 'Standard double with garden view', 220),
  ('203', 'DELUXE',   'QUEEN',   5500.00, 'AVAILABLE', 2, 'Deluxe room with pool view', 320),
  ('303', 'SUITE',    'KING',   11000.00, 'AVAILABLE', 3, 'Corner suite with panoramic view', 520);

-- Add amenities for rooms
INSERT IGNORE INTO room_amenities (room_id, amenity)
SELECT r.id, a.amenity FROM rooms r
JOIN (
  SELECT '101' AS rn, 'WiFi'         AS amenity UNION ALL
  SELECT '101',       'AC'                       UNION ALL
  SELECT '101',       'TV'                       UNION ALL
  SELECT '102',       'WiFi'                     UNION ALL
  SELECT '102',       'AC'                       UNION ALL
  SELECT '102',       'TV'                       UNION ALL
  SELECT '102',       'Mini Bar'                 UNION ALL
  SELECT '201',       'WiFi'                     UNION ALL
  SELECT '201',       'AC'                       UNION ALL
  SELECT '201',       'TV'                       UNION ALL
  SELECT '201',       'Mini Bar'                 UNION ALL
  SELECT '201',       'Balcony'                  UNION ALL
  SELECT '202',       'WiFi'                     UNION ALL
  SELECT '202',       'AC'                       UNION ALL
  SELECT '202',       'TV'                       UNION ALL
  SELECT '202',       'Mini Bar'                 UNION ALL
  SELECT '202',       'Balcony'                  UNION ALL
  SELECT '202',       'Bathtub'                  UNION ALL
  SELECT '301',       'WiFi'                     UNION ALL
  SELECT '301',       'AC'                       UNION ALL
  SELECT '301',       'TV'                       UNION ALL
  SELECT '301',       'Mini Bar'                 UNION ALL
  SELECT '301',       'Balcony'                  UNION ALL
  SELECT '301',       'Bathtub'                  UNION ALL
  SELECT '301',       'Living Area'              UNION ALL
  SELECT '302',       'WiFi'                     UNION ALL
  SELECT '302',       'AC'                       UNION ALL
  SELECT '302',       'TV'                       UNION ALL
  SELECT '302',       'Mini Bar'                 UNION ALL
  SELECT '302',       'Bathtub'                  UNION ALL
  SELECT '302',       'Living Area'              UNION ALL
  SELECT '302',       'Kitchen'                  UNION ALL
  SELECT '401',       'WiFi'                     UNION ALL
  SELECT '401',       'AC'                       UNION ALL
  SELECT '401',       'TV'                       UNION ALL
  SELECT '401',       'Mini Bar'                 UNION ALL
  SELECT '401',       'Balcony'                  UNION ALL
  SELECT '401',       'Bathtub'                  UNION ALL
  SELECT '401',       'Living Area'              UNION ALL
  SELECT '401',       'Kitchen'                  UNION ALL
  SELECT '401',       'Private Pool'
) a ON r.room_number = a.rn;

-- ============================================================
-- VERIFICATION QUERIES - Run these to check setup is correct
-- ============================================================

SELECT 'Users created:' AS info, COUNT(*) AS count FROM users;
SELECT 'Rooms created:'  AS info, COUNT(*) AS count FROM rooms;
SELECT username, role, status FROM users;
SELECT room_number, room_type, price_per_night, status FROM rooms ORDER BY room_number;

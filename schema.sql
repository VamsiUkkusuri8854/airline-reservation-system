-- Database Initialization for Airline Reservation System
CREATE DATABASE IF NOT EXISTS airline_db;
USE airline_db;

-- 1. Users Table
CREATE TABLE IF NOT EXISTS users (
    id INT AUTO_INCREMENT PRIMARY KEY,
    username VARCHAR(50) UNIQUE NOT NULL,
    password VARCHAR(255) NOT NULL,
    name VARCHAR(100) NOT NULL,
    email VARCHAR(100) UNIQUE NOT NULL,
    phone VARCHAR(15) NOT NULL,
    role VARCHAR(15) NOT NULL DEFAULT 'CUSTOMER'
);

-- 2. Flights Table
CREATE TABLE IF NOT EXISTS flights (
    id INT AUTO_INCREMENT PRIMARY KEY,
    flight_number VARCHAR(15) UNIQUE NOT NULL,
    source VARCHAR(50) NOT NULL,
    destination VARCHAR(50) NOT NULL,
    departure_time DATETIME NOT NULL,
    arrival_time DATETIME NOT NULL,
    total_seats INT NOT NULL,
    available_seats INT NOT NULL,
    price DECIMAL(10, 2) NOT NULL,
    status VARCHAR(20) NOT NULL DEFAULT 'ON TIME',
    airline_name VARCHAR(100),
    airline_logo VARCHAR(255),
    is_api_flight BOOLEAN DEFAULT FALSE
);

-- 3. Reservations Table
CREATE TABLE IF NOT EXISTS reservations (
    id INT AUTO_INCREMENT PRIMARY KEY,
    pnr VARCHAR(10) UNIQUE NOT NULL,
    user_id INT NOT NULL,
    flight_id INT NOT NULL,
    booking_date TIMESTAMP DEFAULT CURRENT_TIMESTAMP,
    seats_booked INT NOT NULL,
    seats VARCHAR(255) DEFAULT '',
    status VARCHAR(20) NOT NULL DEFAULT 'CONFIRMED',
    total_amount DECIMAL(10, 2) NOT NULL,
    FOREIGN KEY (user_id) REFERENCES users(id) ON DELETE CASCADE,
    FOREIGN KEY (flight_id) REFERENCES flights(id) ON DELETE CASCADE
);

-- 4. Payments Table
CREATE TABLE IF NOT EXISTS payments (
    id INT AUTO_INCREMENT PRIMARY KEY,
    reservation_id INT NOT NULL,
    payment_method VARCHAR(20) NOT NULL,
    payment_status VARCHAR(20) NOT NULL DEFAULT 'SUCCESS',
    transaction_id VARCHAR(50) UNIQUE NOT NULL,
    amount DECIMAL(10, 2) NOT NULL,
    payment_date TIMESTAMP DEFAULT CURRENT_TIMESTAMP,
    FOREIGN KEY (reservation_id) REFERENCES reservations(id) ON DELETE CASCADE
);

-- Insert dummy flights for out-of-the-box operations
INSERT INTO flights (flight_number, source, destination, departure_time, arrival_time, total_seats, available_seats, price)
VALUES 
('AI-101', 'New York', 'London', '2026-06-01 08:00:00', '2026-06-01 20:00:00', 150, 150, 450.00)
ON DUPLICATE KEY UPDATE flight_number=flight_number;

INSERT INTO flights (flight_number, source, destination, departure_time, arrival_time, total_seats, available_seats, price)
VALUES 
('AI-202', 'Chicago', 'Tokyo', '2026-06-02 10:30:00', '2026-06-03 14:00:00', 200, 200, 850.00)
ON DUPLICATE KEY UPDATE flight_number=flight_number;
	
INSERT INTO flights (flight_number, source, destination, departure_time, arrival_time, total_seats, available_seats, price)
VALUES 
('AI-303', 'San Francisco', 'Paris', '2026-06-03 13:15:00', '2026-06-04 09:30:00', 180, 180, 620.00)
ON DUPLICATE KEY UPDATE flight_number=flight_number;

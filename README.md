# Airline Reservation System (Java & MySQL)

A robust, console-based **Airline Reservation System** built in Java using modern OOP principles, layered MVC/DAO architecture, MySQL database storage, and Maven dependency management.

---

## Technical Features
* **Authentication Security:** Lightweight password hashing using **BCrypt** (no cleartext passwords in DB).
* **Transactional Integrity:** Bookings and cancellations are coordinated as **Atomic Transactions** (ACID compliant) with auto-rollbacks. Seat allocation adjusts immediately, and transactions fail if seats are overbooked.
* **Menu-Driven CLI:** Custom dashboard layouts for **Administrators** and **Customers** with robust scanner exception-handling.
* **Advanced Reports:** Join queries showcase rich details (PNRs, Passenger Names, Flights, Prices, and Transactions) in cleanly formatted ASCII tables.

---

## 1. Database Schema & ER Diagram

```mermaid
erDiagram
    USERS ||--o{ RESERVATIONS : places
    FLIGHTS ||--o{ RESERVATIONS : contains
    RESERVATIONS ||--|| PAYMENTS : has
    
    USERS {
        int id PK
        string username UNIQUE
        string password "BCrypt Hash"
        string name
        string email UNIQUE
        string phone
        string role "ADMIN / CUSTOMER"
    }
    
    FLIGHTS {
        int id PK
        string flight_number UNIQUE
        string source
        string destination
        string departure_time
        string arrival_time
        int total_seats
        int available_seats
        double price
    }
    
    RESERVATIONS {
        int id PK
        string pnr UNIQUE
        int user_id FK
        int flight_id FK
        timestamp booking_date
        int seats_booked
        string status "CONFIRMED / CANCELLED"
        double total_amount
    }
    
    PAYMENTS {
        int id PK
        int reservation_id FK
        string payment_method "CARD / UPI / NET_BANKING"
        string payment_status "SUCCESS / FAILED"
        string transaction_id UNIQUE
        double amount
        timestamp payment_date
    }
```

### Table Relations:
1. **Users to Reservations (`1:N`):** A customer can make multiple flight reservations, but each booking belongs to exactly one user.
2. **Flights to Reservations (`1:N`):** A single flight can have multiple ticket reservations, but each reservation represents bookings on one specific flight.
3. **Reservations to Payments (`1:1`):** Every successful reservation initiates exactly one payment ledger record.

---

## 2. Table Creation SQL Queries

Run the script located in [schema.sql](file:///c:/Users/ACER/OneDrive/Desktop/airlines/schema.sql) on your MySQL Server instance:

```sql
CREATE DATABASE IF NOT EXISTS airline_db;
USE airline_db;

-- 1. Users
CREATE TABLE IF NOT EXISTS users (
    id INT AUTO_INCREMENT PRIMARY KEY,
    username VARCHAR(50) UNIQUE NOT NULL,
    password VARCHAR(255) NOT NULL,
    name VARCHAR(100) NOT NULL,
    email VARCHAR(100) UNIQUE NOT NULL,
    phone VARCHAR(15) NOT NULL,
    role VARCHAR(15) NOT NULL DEFAULT 'CUSTOMER'
);

-- 2. Flights
CREATE TABLE IF NOT EXISTS flights (
    id INT AUTO_INCREMENT PRIMARY KEY,
    flight_number VARCHAR(15) UNIQUE NOT NULL,
    source VARCHAR(50) NOT NULL,
    destination VARCHAR(50) NOT NULL,
    departure_time DATETIME NOT NULL,
    arrival_time DATETIME NOT NULL,
    total_seats INT NOT NULL,
    available_seats INT NOT NULL,
    price DECIMAL(10, 2) NOT NULL
);

-- 3. Reservations
CREATE TABLE IF NOT EXISTS reservations (
    id INT AUTO_INCREMENT PRIMARY KEY,
    pnr VARCHAR(10) UNIQUE NOT NULL,
    user_id INT NOT NULL,
    flight_id INT NOT NULL,
    booking_date TIMESTAMP DEFAULT CURRENT_TIMESTAMP,
    seats_booked INT NOT NULL,
    status VARCHAR(20) NOT NULL DEFAULT 'CONFIRMED',
    total_amount DECIMAL(10, 2) NOT NULL,
    FOREIGN KEY (user_id) REFERENCES users(id) ON DELETE CASCADE,
    FOREIGN KEY (flight_id) REFERENCES flights(id) ON DELETE CASCADE
);

-- 4. Payments
CREATE TABLE IF NOT EXISTS payments (
    id INT AUTO_INCREMENT PRIMARY KEY,
    reservation_id INT NOT NULL,
    payment_method VARCHAR(20) NOT NULL,
    payment_status VARCHAR(20) NOT NULL DEFAULT 'SUCCESS',
    transaction_id VARCHAR(50) UNIQUE NOT NULL,
    amount DECIMAL(10, 2) NOT NULL,
    payment_date TIMESTAMP DEFAULT CURRENT_TIMESTAMP,
    FOREIGN KEY (reservation_id) REFERENCES reservations(id) ON DELETE CASCADE;
);
```

---

## 3. Step-by-Step Execution Instructions

### Step A: Configure MySQL Database
1. Open your MySQL Command Line Client or preferred GUI tool (like MySQL Workbench).
2. Create and execute the database tables using the command:
   ```sql
   SOURCE c:/Users/ACER/OneDrive/Desktop/airlines/schema.sql;
   ```
3. Update [db.properties](file:///c:/Users/ACER/OneDrive/Desktop/airlines/src/main/resources/db.properties) with your database username and password if they differ from the defaults:
   ```properties
   db.url=jdbc:mysql://localhost:3306/airline_db?useSSL=false&serverTimezone=UTC&allowPublicKeyRetrieval=true
   db.username=YOUR_MYSQL_USERNAME
   db.password=YOUR_MYSQL_PASSWORD
   ```

### Step B: Compile the Project
Open a command terminal (e.g., PowerShell or Command Prompt) and compile the project using Apache Maven:
```bash
mvn clean compile
```

### Step C: Execute/Run the Console Application
Start the interactive application from your console by running:
```bash
mvn exec:java
```

---

## 4. Default Credentials (Initialization)
When the application starts for the first time, it automatically verifies and provisions a default **Administrator** profile if none exists:
* **Admin Username:** `admin`
* **Admin Password:** `admin123`

You can log in using `admin` / `admin123` to immediately access the admin control panel and schedule flights, review payments, or inspect general airline statistics!

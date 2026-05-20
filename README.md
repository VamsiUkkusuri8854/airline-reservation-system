<h1 align="center">✈️ Airline Reservation System</h1>

<p align="center">
  <img src="https://img.shields.io/badge/Java-8-orange?style=for-the-badge&logo=java&logoColor=white"/>
  <img src="https://img.shields.io/badge/Spring%20Boot-2.7.18-brightgreen?style=for-the-badge&logo=springboot&logoColor=white"/>
  <img src="https://img.shields.io/badge/MySQL-8.0-blue?style=for-the-badge&logo=mysql&logoColor=white"/>
  <img src="https://img.shields.io/badge/Thymeleaf-3.x-green?style=for-the-badge&logo=thymeleaf&logoColor=white"/>
  <img src="https://img.shields.io/badge/Maven-3.8-red?style=for-the-badge&logo=apachemaven&logoColor=white"/>
  <img src="https://img.shields.io/badge/Docker-Ready-2496ED?style=for-the-badge&logo=docker&logoColor=white"/>
</p>

<p align="center">
  A full-stack <strong>Enterprise Airline Reservation System</strong> built with Java, Spring Boot, MySQL, JPA, and a Thymeleaf web frontend. Supports complete flight search, seat booking, payment processing, PDF ticket generation, and email notifications — all in one platform.
</p>

---

## 📌 Table of Contents

- [About the Project](#-about-the-project)
- [Features](#-features)
- [Tech Stack](#-tech-stack)
- [Project Structure](#-project-structure)
- [Database Schema](#-database-schema)
- [Getting Started](#-getting-started)
- [Running with Docker](#-running-with-docker)
- [Future Enhancements](#-future-enhancements)
- [Contributing](#-contributing)

---

## 🚀 About the Project

The **Airline Reservation System** is an enterprise-grade web application that simulates real-world airline booking workflows. It features a role-based architecture supporting both **Customers** and **Admins**, offering a seamless experience from registration to boarding pass generation.

This project demonstrates:
- **Spring Boot** MVC architecture with layered design (Controller → Service → Repository)
- **Spring Data JPA** with a fully normalized **MySQL** relational database
- **BCrypt** password security and session-based authentication
- **iText PDF** library for professional ticket/receipt generation
- **JavaMail** integration for automated booking confirmation emails
- **Docker** multi-stage build for containerized deployment
- **FlatLaf** modern Swing UI for the desktop module

---

## ✨ Features

### 👤 Customer
| Feature | Description |
|---|---|
| 🔐 Register / Login | Secure authentication with BCrypt password hashing |
| ✈️ Search Flights | Search by source, destination & date |
| 💺 Seat Selection | Visual seat map with real-time availability |
| 💳 Book & Pay | Complete booking with payment simulation |
| 🎫 PDF Ticket | Downloadable boarding pass generated via iText |
| 📧 Email Confirmation | Auto-send booking confirmation to registered email |
| 📋 My Bookings | View all past and upcoming reservations |
| ❌ Cancel Booking | Cancel reservation with PNR number |

### 🛠️ Admin
| Feature | Description |
|---|---|
| ➕ Add / Edit Flights | Full CRUD on flight records |
| 🗓️ Manage Schedule | Update departure/arrival times and status |
| 👥 Manage Users | View all registered customers |
| 📊 View Reservations | Track all bookings across the system |

---

## 🧱 Tech Stack

| Layer | Technology |
|---|---|
| **Language** | Java 8 |
| **Framework** | Spring Boot 2.7.18 |
| **Web Layer** | Spring MVC + Thymeleaf |
| **Persistence** | Spring Data JPA + Hibernate |
| **Database** | MySQL 8.0 |
| **Security** | BCrypt (jBCrypt 0.4) |
| **PDF Generation** | iText PDF 5.5.13 |
| **Email** | JavaMail (Spring Boot Mail) |
| **Desktop UI** | Java Swing + FlatLaf 2.6 |
| **Build Tool** | Apache Maven 3.8 |
| **Containerization** | Docker (multi-stage build) |
| **Dev Tools** | Spring Boot DevTools |

---

## 📁 Project Structure

```
airline-reservation-system/
├── src/
│   └── main/
│       ├── java/com/airline/
│       │   ├── AirlineWebApplication.java     # Spring Boot entry point
│       │   ├── controller/                    # MVC + REST controllers
│       │   │   ├── AuthController.java
│       │   │   ├── FlightController.java
│       │   │   ├── DashboardController.java
│       │   │   └── api/                       # REST API endpoints
│       │   ├── service/                       # Business logic layer
│       │   │   ├── FlightService.java
│       │   │   ├── ReservationService.java
│       │   │   ├── UserService.java
│       │   │   ├── PdfGeneratorService.java
│       │   │   └── EmailService.java
│       │   ├── repository/                    # Spring Data JPA repos
│       │   ├── model/                         # JPA Entity classes
│       │   ├── dao/                           # Data Access Objects
│       │   ├── exception/                     # Custom exceptions
│       │   ├── ui/                            # Swing desktop UI
│       │   └── util/                          # Utility classes
│       └── resources/
│           ├── templates/                     # Thymeleaf HTML templates
│           ├── static/                        # CSS, JS, images
│           ├── application.properties         # App configuration
│           └── db.properties                  # DB credentials
├── schema.sql                                 # DB initialization script
├── Dockerfile                                 # Multi-stage Docker build
├── pom.xml                                    # Maven dependencies
└── README.md
```

---

## 🗄️ Database Schema

```sql
-- Core Tables
users         (id, username, password, name, email, phone, role)
flights       (id, flight_number, source, destination, departure_time,
               arrival_time, total_seats, available_seats, price, status)
reservations  (id, pnr, user_id, flight_id, booking_date,
               seats_booked, seats, status, total_amount)
payments      (id, reservation_id, payment_method, payment_status,
               transaction_id, amount, payment_date)
```

---

## ⚙️ Getting Started

### Prerequisites

- Java 8+
- MySQL 8.0+
- Maven 3.8+
- Git

### 1. Clone the Repository

```bash
git clone https://github.com/VamsiUkkusuri8854/airline-reservation-system.git
cd airline-reservation-system
```

### 2. Set Up the Database

```bash
mysql -u root -p < schema.sql
```

### 3. Configure `application.properties`

```properties
spring.datasource.url=jdbc:mysql://localhost:3306/airline_db
spring.datasource.username=your_mysql_username
spring.datasource.password=your_mysql_password
spring.mail.username=your_email@gmail.com
spring.mail.password=your_app_password
```

### 4. Build & Run

```bash
mvn clean install -DskipTests
mvn spring-boot:run
```

### 5. Access the Application

```
http://localhost:8080
```

> **Default Admin Credentials:** Set up via the database or registration page.

---

## 🐳 Running with Docker

```bash
# Build Docker image
docker build -t airline-reservation-system .

# Run the container
docker run -p 8080:8080 \
  -e SPRING_DATASOURCE_URL=jdbc:mysql://host.docker.internal:3306/airline_db \
  -e SPRING_DATASOURCE_USERNAME=root \
  -e SPRING_DATASOURCE_PASSWORD=yourpassword \
  airline-reservation-system
```

---

## 🔮 Future Enhancements

- [ ] 🌐 Deploy on **Render / Railway** for live public access
- [ ] 📱 Responsive mobile-first UI redesign
- [ ] 🔔 Real-time seat availability with WebSockets
- [ ] 💳 Stripe / Razorpay payment gateway integration
- [ ] 📈 Admin analytics dashboard with charts
- [ ] 🛡️ Spring Security with JWT-based authentication
- [ ] 🧪 JUnit + Mockito unit & integration tests
- [ ] ✈️ Multi-leg / connecting flight support

---

## 🤝 Contributing

Contributions, issues, and feature requests are welcome!

1. Fork the repository
2. Create your feature branch: `git checkout -b feature/amazing-feature`
3. Commit your changes: `git commit -m 'Add amazing feature'`
4. Push to the branch: `git push origin feature/amazing-feature`
5. Open a Pull Request

---

## ©️ Copyright & License

This project is licensed under the [MIT License](LICENSE).

**Copyright (c) 2026 Vamsi Ukkusuri.** All rights reserved.  
*Developed for FlyHigh Airlines Reservation System.*

---

<p align="center">
  Built with ❤️ by <a href="https://github.com/VamsiUkkusuri8854">Vamsi Ukkusuri</a>
</p>

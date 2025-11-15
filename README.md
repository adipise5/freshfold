# 🧺 FreshFold - Laundry Management System

**A full-stack web application for automating laundry services at BITS Pilani.**  
Developed using **Spring Boot (Java)** for the backend, **React.js** for the frontend, and **MySQL** for persistent data storage.

---

## 🚀 Features

- 👨‍🎓 **Student Portal** – Create laundry orders, upload photos, track order status.
- 🧑‍🔧 **Personnel Portal** – Manage assigned orders, update progress.
- 👨‍💼 **Admin Dashboard** – Monitor users, view reports, and analyze performance.
- 📸 **Photo Upload Support** – Stores item photos in local file system.
- 💾 **MySQL Database Integration** – Persistent data storage for all modules.
- 🔒 **Role-based Authentication** – Secure login for Admin, Student, and Personnel.
- ⚡ **RESTful API Architecture** – Backend built with modular controllers, services, and repositories.

---

## 🏗️ Tech Stack

| Layer | Technology |
|--------|-------------|
| **Frontend** | React.js, Axios, React Router, CSS3 |
| **Backend** | Spring Boot 3.2.0 (Java 17) |
| **Database** | MySQL 9.5.0 |
| **ORM** | Hibernate / Spring Data JPA |
| **Build Tools** | Maven, npm |
| **Server** | Embedded Tomcat (Port 8080) |

---

## ⚙️ System Architecture
Frontend (React) → Backend (Spring Boot) → Database (MySQL)

- **Frontend (Port 3000)** calls `http://localhost:8080/api/...`
- **Backend (Port 8080)** handles business logic and database transactions.
- **Database** stores persistent data for users, orders, and uploaded photos.

---




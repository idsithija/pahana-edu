# Pahana Edu Bookshop Management System

A comprehensive Java EE enterprise application for educational bookshop management, built with modern web technologies and following enterprise software development best practices.

**Demo Credentials:**
- **Admin:** username: `admin`, password: `admin123`
- **Operator:** username: `operator`, password: `operator123`

## 📋 Project Overview

The Pahana Edu Bookshop Management System is a sophisticated enterprise-grade solution designed specifically for educational institutions to manage their bookshop operations efficiently. This Java EE-based application provides a robust, scalable, and secure platform that addresses the complex requirements of modern educational bookshop management.

## 🏗️ Architecture

### Technology Stack

- **Backend:** Java EE (JDK 11), Enterprise JavaBeans (EJB), Java Servlets
- **Frontend:** JavaServer Pages (JSP), HTML5, CSS3, JavaScript
- **Database:** PostgreSQL 12+ with JPA/Hibernate ORM
- **Application Server:** Apache Tomcat 9.0 (production), Jetty (development)
- **Build Tool:** Apache Maven 3.6+

## 🛠️ Installation and Setup

### Prerequisites

- Java Development Kit (JDK) 11 or later
- Apache Maven 3.6+
- PostgreSQL 12+
- Apache Tomcat 9.0+

### Quick Setup

```bash
# 1. Database Setup
createdb pahanaedu_bookshop
psql -c "CREATE USER pahanaedu_user WITH PASSWORD 'secure_password_123';"
psql -c "GRANT ALL PRIVILEGES ON DATABASE pahanaedu_bookshop TO pahanaedu_user;"
psql -d pahanaedu_bookshop -U pahanaedu_user -f src/main/resources/database-schema.sql

# 2. Build and Run
mvn clean compile
mvn jetty:run

# 3. Access Application
# URL: http://localhost:8080/pahana-edu-bookshop
# Admin: admin/admin123 | Operator: operator/operator123
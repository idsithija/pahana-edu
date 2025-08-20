# Pahana Edu Bookshop Management System

A comprehensive Java EE enterprise application for educational bookshop management, built with modern web technologies and following enterprise software development best practices.


**Demo Credentials:**
- **Admin:** username: `admin`, password: `admin123`
- **Operator:** username: `operator`, password: `operator123`

## 📋 Project Overview

The Pahana Edu Bookshop Management System is a sophisticated enterprise-grade solution designed specifically for educational institutions to manage their bookshop operations efficiently. This Java EE-based application provides a robust, scalable, and secure platform that addresses the complex requirements of modern educational bookshop management.

### Key Features

- **User Management:** Role-based access control with secure authentication
- **Customer Management:** Comprehensive customer relationship management
- **Inventory Management:** Real-time stock tracking and supplier management
- **Billing System:** Complete point-of-sale with multiple payment methods
- **Dashboard Analytics:** Real-time business intelligence and reporting
- **Responsive Design:** Mobile-friendly interface for all devices
- **Security:** SHA-256 password hashing and comprehensive audit trails
- **Performance:** Optimized database queries and C3P0 connection pooling

## 🏗️ Architecture

### Technology Stack

- **Backend:** Java EE (JDK 11), Enterprise JavaBeans (EJB), Java Servlets
- **Frontend:** JavaServer Pages (JSP), HTML5, CSS3, JavaScript
- **Database:** PostgreSQL 12+ with JPA/Hibernate ORM
- **Application Server:** Apache Tomcat 9.0 (production), Jetty (development)
- **Build Tool:** Apache Maven 3.6+
- **Security:** SHA-256 password hashing, custom authentication

### System Architecture

The application follows a 3-tier enterprise architecture:

1. **Presentation Tier:** Responsive web interface with JSP and modern web technologies
2. **Business Logic Tier:** EJB services with transaction management and business rules
3. **Data Persistence Tier:** JPA entities with PostgreSQL database

## 📁 Project Structure

```
pahana-edu/
├── src/
│   ├── main/
│   │   ├── java/
│   │   │   └── com/pahanaedu/
│   │   │       ├── controller/     # Servlet controllers
│   │   │       ├── dao/           # Data Access Objects
│   │   │       ├── service/       # Business logic services
│   │   │       ├── model/         # JPA Entity classes
│   │   │       └── util/          # Utility classes
│   │   ├── resources/
│   │   │   ├── META-INF/persistence.xml  # JPA configuration
│   │   │   └── database-schema.sql      # Database schema
│   │   └── webapp/
│   │       ├── WEB-INF/
│   │       │   ├── jsp/            # JSP pages
│   │       │   ├── includes/       # Reusable components
│   │       │   └── web.xml         # Web application config
│   │       ├── css/                # Stylesheets
│   │       ├── login.jsp           # Login page
│   │       └── index.jsp           # Welcome page
│   └── test/                       # JUnit test cases
├── docs/                           # Documentation
│   ├── Technical_Documentation.md  # Complete technical docs
│   ├── Architecture_Documentation.md # System architecture docs
│   └── setup/Startup_Guide.md      # Quick setup guide
├── diagrams/                       # PlantUML diagrams
├── pom.xml                         # Maven configuration
└── README.md                       # This file
```

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
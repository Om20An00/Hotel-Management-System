# Grand Hotel Management System

Full-stack Hotel Management System:
- Frontend: Angular 19 + Bootstrap 5
- Backend: Spring Boot 3.2 + JWT
- Database: MySQL 8.0
- Tests: JUnit 5 + Mockito

---

## STEP-BY-STEP SETUP

### Step 1 – MySQL Setup
Open MySQL and run:
  CREATE DATABASE hotel_db;

Then open: backend/src/main/resources/application.properties
Change: spring.datasource.password=YOUR_MYSQL_ROOT_PASSWORD

### Step 2 – Run Backend
Open terminal in hotel-management/backend/ and run:
  mvn spring-boot:run

Wait for: "Started HotelManagementApplication"
Backend runs at: http://localhost:8080
(DB tables and admin/staff accounts created automatically)

### Step 3 – Run Frontend
Open a NEW terminal in hotel-management/frontend/ and run:
  npm install
  ng serve

Open browser at: http://localhost:4200

---

## LOGIN CREDENTIALS

| Role     | Username | Password   |
|----------|----------|------------|
| Admin    | admin    | Admin@1234 |
| Staff    | staff1   | Staff@1234 |
| Customer | register via app  |            |

---

## RUN JUNIT TESTS
  cd backend
  mvn test

---

## TROUBLESHOOTING

Port 8080 busy:
  netstat -ano | findstr :8080
  taskkill /PID <PID> /F

Port 4200 busy:
  ng serve --port 4300

npm errors:
  npm install --legacy-peer-deps

MySQL error:
  Make sure MySQL service is running
  Check password in application.properties

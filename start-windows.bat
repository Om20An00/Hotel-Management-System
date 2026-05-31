@echo off
echo ============================================
echo   Hotel Management System - Startup Script
echo ============================================
echo.

echo Starting Backend (Spring Boot)...
start "Backend" cmd /k "cd /d %~dp0backend && mvn spring-boot:run"

echo Waiting 20 seconds for backend to start...
timeout /t 20 /nobreak

echo Starting Frontend (Angular)...
start "Frontend" cmd /k "cd /d %~dp0frontend && npm install && ng serve"

echo.
echo ============================================
echo Both servers are starting!
echo Backend:  http://localhost:8080
echo Frontend: http://localhost:4200
echo ============================================
echo.
echo Default logins:
echo   Admin  -> username: admin   / password: Admin@1234
echo   Staff  -> username: staff1  / password: Staff@1234
echo   Customer -> Register at http://localhost:4200/auth/register
echo.
pause

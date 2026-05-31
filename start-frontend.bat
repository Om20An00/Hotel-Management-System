@echo off
echo ============================================
echo  Grand Hotel Management System - Frontend
echo ============================================
echo.

cd /d "%~dp0frontend"

echo Installing dependencies (first time only)...
call npm install

echo.
echo Starting Angular frontend...
echo Frontend will run at: http://localhost:4200
echo Press Ctrl+C to stop.
echo.

call ng serve --open

pause

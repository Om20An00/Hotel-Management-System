@echo off
echo ============================================
echo  Grand Hotel Management System - Backend
echo ============================================
echo.

cd /d "%~dp0backend"

echo Setting JVM memory options...
set JAVA_OPTS=-Xms256m -Xmx512m -XX:MaxMetaspaceSize=256m -XX:+UseG1GC
set MAVEN_OPTS=-Xms256m -Xmx512m -XX:MaxMetaspaceSize=256m

echo Starting Spring Boot backend...
echo Backend will run at: http://localhost:8080
echo Press Ctrl+C to stop.
echo.

mvn spring-boot:run -Dspring-boot.run.jvmArguments="-Xms256m -Xmx512m -XX:MaxMetaspaceSize=256m -XX:+UseG1GC"

pause

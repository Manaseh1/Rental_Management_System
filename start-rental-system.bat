@echo off
title Rental Management System
echo =====================================
echo   Starting Rental Management System
echo =====================================

REM Go to script directory (important for relative paths)
cd /d %~dp0

REM Check if JAR exists in build/libs
if not exist "build/libs/" (
    echo ERROR: build/libs folder not found.
    echo Please run: .\gradlew build
    pause
    exit /b
)

REM Find the latest jar automatically
for %%f in (build\libs\*.jar) do set JAR=%%f

if "%JAR%"=="" (
    echo ERROR: No JAR file found in build/libs
    pause
    exit /b
)

echo Starting: %JAR%

REM Start Spring Boot app
start /B java -jar "%JAR%"

echo Waiting for server to start...

REM Wait a few seconds for Spring Boot to boot up
timeout /t 8 /nobreak >nul

REM Open browser automatically
start "" http://localhost:8080

echo =====================================
echo Application is running at:
echo http://localhost:8080
echo =====================================

pause
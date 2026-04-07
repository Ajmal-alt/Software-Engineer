@echo off
echo =====================================================
echo  RTMS - Database Setup
echo =====================================================
echo.
set /p MYSQL_USER=Enter MySQL username (default: root): 
if "%MYSQL_USER%"=="" set MYSQL_USER=root
set /p MYSQL_PASS=Enter MySQL password: 

where mysql >nul 2>&1
if %ERRORLEVEL% NEQ 0 (
    echo [ERROR] mysql command not found.
    echo         Add MySQL bin to PATH.
    echo         e.g. C:\Program Files\MySQL\MySQL Server 8.0\bin
    pause
    exit /b 1
)

echo [INFO] Creating database and tables...
mysql -u %MYSQL_USER% -p%MYSQL_PASS% < sql\rtms_schema.sql

if %ERRORLEVEL% NEQ 0 (
    echo [ERROR] Setup failed. Check credentials and MySQL server.
    pause
    exit /b 1
)

echo.
echo [OK] Database rtms_db created successfully!
echo.
echo  Default login accounts:
echo    Admin   : admin    / Admin@123
echo    Manager : manager1 / Mgr@123
echo    Driver  : driver1  / Drv@123   (Arjun Mehta   - SUPERLICENSE)
echo    Driver  : driver2  / Drv@123   (Ravi Shankar  - A Grade)
echo    Driver  : driver3  / Drv@123   (Priya Kapoor  - B Grade)
echo.
echo  Seed Data Included:
echo    - 3 Drivers        with full career stats
echo    - 6 Staff          across 5 departments
echo    - 6 Race Events    (2 completed, 2 upcoming F1, 1 completed F3, 1 upcoming F3)
echo    - 6 Race Entries   with qualifying and race results
echo    - 6 Sponsors       (Title to Associate, Rs.145M total value)
echo    - 11 Budget Cat.   (Income + Expense)
echo    - 15 Transactions  (Q1 income and expenses)
echo.
echo  Next: Edit config\db.properties with your password,
echo        then run 2_compile.bat
echo.
pause

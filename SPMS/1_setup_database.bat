@echo off
REM ============================================================
REM  SPMS – Step 1: Database Setup
REM  Run this ONCE to create the database and seed data.
REM ============================================================

echo =====================================================
echo  SPMS – Database Setup
echo =====================================================
echo.

REM ── Prompt for MySQL credentials ──────────────────────
set /p MYSQL_USER=Enter MySQL username (default: root): 
if "%MYSQL_USER%"=="" set MYSQL_USER=root

set /p MYSQL_PASS=Enter MySQL password: 

REM ── Check mysql is on PATH ────────────────────────────
where mysql >nul 2>&1
if %ERRORLEVEL% NEQ 0 (
    echo [ERROR] 'mysql' command not found.
    echo         Make sure MySQL is installed and its bin folder is in your PATH.
    echo         Typical path: C:\Program Files\MySQL\MySQL Server 8.0\bin
    pause
    exit /b 1
)

echo.
echo [INFO] Running schema script...
mysql -u %MYSQL_USER% -p%MYSQL_PASS% < sql\spms_schema.sql

if %ERRORLEVEL% NEQ 0 (
    echo [ERROR] Schema creation failed. Check your credentials and MySQL server.
    pause
    exit /b 1
)

echo.
echo [OK] Database 'spms_db' created and seeded successfully!
echo.
echo      Default login accounts:
echo        Admin   : admin      / Admin@123
echo        HR      : hrmanager  / Hr@123
echo        PM      : pm1        / Pm@123
echo        Employee: emp1       / Emp@123
echo        Employee: emp2       / Emp@123
echo.
echo      Next Step: Edit config\db.properties with your DB password,
echo                 then run 2_compile.bat
echo.
pause

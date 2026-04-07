@echo off
echo =====================================================
echo  MMS - Database Setup
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
mysql -u %MYSQL_USER% -p%MYSQL_PASS% < sql\mms_schema.sql

if %ERRORLEVEL% NEQ 0 (
    echo [ERROR] Setup failed. Check credentials and MySQL server.
    pause
    exit /b 1
)

echo.
echo [OK] Database mms_db created successfully!
echo.
echo  Default login accounts:
echo    Admin   : admin    / Admin@123
echo    Manager : manager1 / Mgr@123
echo    Agent   : agent1   / Agt@123
echo    Agent   : agent2   / Agt@123
echo.
echo  Seed Data Included:
echo    - 6 Products   (Software + Services)
echo    - 5 Promotions (% Discount, Flat, Bundle)
echo    - 7 Leads      (across all pipeline stages)
echo    - 4 Customers  (incl. 1 converted from lead)
echo    - 6 Lead Activities (calls, demos, proposals)
echo    - 5 Orders with discount calculation
echo.
echo  Next: Edit config\db.properties with your password,
echo        then run 2_compile.bat
echo.
pause

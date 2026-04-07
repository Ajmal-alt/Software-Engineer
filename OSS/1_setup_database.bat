@echo off
echo =====================================================
echo  OSS - Database Setup
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
mysql -u %MYSQL_USER% -p%MYSQL_PASS% < sql\oss_schema.sql

if %ERRORLEVEL% NEQ 0 (
    echo [ERROR] Setup failed. Check credentials and MySQL server.
    pause
    exit /b 1
)

echo.
echo [OK] Database oss_db created successfully!
echo.
echo  Default login accounts:
echo    Admin    : admin     / Admin@123
echo    Manager  : manager1  / Mgr@123
echo    Customer : customer1 / Cust@123
echo    Customer : customer2 / Cust@123
echo    Customer : customer3 / Cust@123
echo.
echo  Seed Data Included:
echo    - 6 Categories  (Electronics, Clothing, Home, Books, Sports, Beauty)
echo    - 10 Products   with prices, discounts and tax rates
echo    - 3 Customers   with loyalty points and addresses
echo    - 4 Orders      (Pending to Delivered)
echo    - 3 Payments    (UPI, Credit Card, Net Banking)
echo    - 3 Invoices    (auto-generated on payment)
echo    - Cart items    pre-loaded for customer1
echo.
echo  Next: Edit config\db.properties with your password,
echo        then run 2_compile.bat
echo.
pause

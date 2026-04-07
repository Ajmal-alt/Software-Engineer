@echo off
echo =====================================================
echo  MMS - Compile
echo =====================================================
echo.

where javac >nul 2>&1
if %ERRORLEVEL% NEQ 0 (
    echo [ERROR] javac not found. Install JDK and add to PATH.
    pause
    exit /b 1
)

echo [INFO] Java version:
javac -version
echo.

if not exist "lib\mysql-connector-java.jar" (
    echo [ERROR] Missing lib\mysql-connector-java.jar
    echo         Download from: https://dev.mysql.com/downloads/connector/j/
    echo         Place in lib\ folder and rename to mysql-connector-java.jar
    pause
    exit /b 1
)

if not exist "out" mkdir out

set CP=lib\mysql-connector-java.jar
set SRC=src\com\mms

echo [INFO] Compiling util...
javac -cp "%CP%" -d out %SRC%\util\DBConnection.java %SRC%\util\ConsoleUtil.java
if %ERRORLEVEL% NEQ 0 ( echo [FAILED] util & pause & exit /b 1 )
echo [OK] util

echo [INFO] Compiling model...
javac -cp "%CP%" -d out %SRC%\model\User.java %SRC%\model\Lead.java %SRC%\model\Customer.java %SRC%\model\Product.java %SRC%\model\Promotion.java %SRC%\model\Order.java %SRC%\model\LeadActivity.java
if %ERRORLEVEL% NEQ 0 ( echo [FAILED] model & pause & exit /b 1 )
echo [OK] model

echo [INFO] Compiling dao...
javac -cp "%CP%;out" -d out %SRC%\dao\UserDAO.java %SRC%\dao\LeadDAO.java %SRC%\dao\CustomerDAO.java %SRC%\dao\ProductDAO.java %SRC%\dao\PromotionDAO.java %SRC%\dao\OrderDAO.java
if %ERRORLEVEL% NEQ 0 ( echo [FAILED] dao & pause & exit /b 1 )
echo [OK] dao

echo [INFO] Compiling main...
javac -cp "%CP%;out" -d out %SRC%\main\MMSApplication.java
if %ERRORLEVEL% NEQ 0 ( echo [FAILED] main & pause & exit /b 1 )
echo [OK] main

echo.
echo =====================================================
echo  [OK] Compilation successful! Run 3_run.bat now.
echo =====================================================
echo.
pause

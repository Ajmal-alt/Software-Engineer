@echo off
echo =====================================================
echo  MARKETING MANAGEMENT SYSTEM (MMS)
echo =====================================================
echo.

if not exist "out\com\mms\main\MMSApplication.class" (
    echo [ERROR] Not compiled yet. Run 2_compile.bat first.
    pause
    exit /b 1
)

if not exist "lib\mysql-connector-java.jar" (
    echo [ERROR] Missing lib\mysql-connector-java.jar
    pause
    exit /b 1
)

java -cp "out;lib\mysql-connector-java.jar" com.mms.main.MMSApplication

echo.
echo [INFO] Application exited.
pause

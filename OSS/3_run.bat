@echo off
echo =====================================================
echo  ONLINE SHOPPING SYSTEM (OSS)
echo =====================================================
echo.

if not exist "out\com\oss\main\OSSApplication.class" (
    echo [ERROR] Not compiled yet. Run 2_compile.bat first.
    pause
    exit /b 1
)

if not exist "lib\mysql-connector-java.jar" (
    echo [ERROR] Missing lib\mysql-connector-java.jar
    pause
    exit /b 1
)

java -cp "out;lib\mysql-connector-java.jar" com.oss.main.OSSApplication

echo.
echo [INFO] Application exited.
pause

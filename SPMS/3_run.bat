@echo off
REM ============================================================
REM  SPMS – Step 3: Run the Application
REM ============================================================

echo =====================================================
echo  SOFTWARE PERSONNEL MANAGEMENT SYSTEM
echo =====================================================
echo.

REM ── Sanity checks ─────────────────────────────────────
if not exist "out\com\spms\main\SPMSApplication.class" (
    echo [ERROR] Compiled classes not found. Run 2_compile.bat first.
    pause
    exit /b 1
)

if not exist "lib\mysql-connector-java.jar" (
    echo [ERROR] Missing lib\mysql-connector-java.jar
    pause
    exit /b 1
)

REM ── Launch ────────────────────────────────────────────
java -cp "out;lib\mysql-connector-java.jar" com.spms.main.SPMSApplication

echo.
echo [INFO] Application exited.
pause

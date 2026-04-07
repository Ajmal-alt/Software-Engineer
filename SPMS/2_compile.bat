@echo off
REM ============================================================
REM  SPMS – Step 2: Compile Java Source Files (v3)
REM ============================================================

echo =====================================================
echo  SPMS – Compile
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
    pause
    exit /b 1
)

if not exist "out" mkdir out

set CP=lib\mysql-connector-java.jar
set SRC=src\com\spms

echo [INFO] Compiling util...
javac -cp "%CP%" -d out %SRC%\util\DBConnection.java %SRC%\util\ConsoleUtil.java
if %ERRORLEVEL% NEQ 0 ( echo [FAILED] util & pause & exit /b 1 )
echo [OK] util

echo [INFO] Compiling model...
javac -cp "%CP%" -d out %SRC%\model\User.java %SRC%\model\Employee.java %SRC%\model\Attendance.java %SRC%\model\LeaveApplication.java %SRC%\model\LeaveBalance.java %SRC%\model\Payroll.java %SRC%\model\Project.java %SRC%\model\Department.java %SRC%\model\PerformanceReview.java
if %ERRORLEVEL% NEQ 0 ( echo [FAILED] model & pause & exit /b 1 )
echo [OK] model

echo [INFO] Compiling dao...
javac -cp "%CP%;out" -d out %SRC%\dao\UserDAO.java %SRC%\dao\EmployeeDAO.java %SRC%\dao\AttendanceDAO.java %SRC%\dao\LeaveDAO.java %SRC%\dao\PayrollDAO.java %SRC%\dao\ProjectDAO.java %SRC%\dao\PerformanceDAO.java
if %ERRORLEVEL% NEQ 0 ( echo [FAILED] dao & pause & exit /b 1 )
echo [OK] dao

echo [INFO] Compiling main...
javac -cp "%CP%;out" -d out %SRC%\main\SPMSApplication.java
if %ERRORLEVEL% NEQ 0 ( echo [FAILED] main & pause & exit /b 1 )
echo [OK] main

echo.
echo =====================================================
echo  [OK] Compilation successful! Run 3_run.bat now.
echo =====================================================
echo.
pause

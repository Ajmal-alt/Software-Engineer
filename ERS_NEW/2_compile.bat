@echo off
echo =====================================================
echo  ERS - Compile
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
set SRC=src\com\ers

echo [INFO] Compiling util...
javac -cp "%CP%" -d out %SRC%\util\DBConnection.java %SRC%\util\ConsoleUtil.java
if %ERRORLEVEL% NEQ 0 ( echo [FAILED] util & pause & exit /b 1 )
echo [OK] util

echo [INFO] Compiling model...
javac -cp "%CP%" -d out %SRC%\model\User.java %SRC%\model\Student.java %SRC%\model\Subject.java %SRC%\model\Exam.java %SRC%\model\ExamSubject.java %SRC%\model\Registration.java %SRC%\model\Result.java
if %ERRORLEVEL% NEQ 0 ( echo [FAILED] model & pause & exit /b 1 )
echo [OK] model

echo [INFO] Compiling dao...
javac -cp "%CP%;out" -d out %SRC%\dao\UserDAO.java %SRC%\dao\StudentDAO.java %SRC%\dao\ExamDAO.java %SRC%\dao\RegistrationDAO.java %SRC%\dao\ResultDAO.java
if %ERRORLEVEL% NEQ 0 ( echo [FAILED] dao & pause & exit /b 1 )
echo [OK] dao

echo [INFO] Compiling main...
javac -cp "%CP%;out" -d out %SRC%\main\ERSApplication.java
if %ERRORLEVEL% NEQ 0 ( echo [FAILED] main & pause & exit /b 1 )
echo [OK] main

echo.
echo =====================================================
echo  [OK] Compilation successful! Run 3_run.bat now.
echo =====================================================
echo.
pause

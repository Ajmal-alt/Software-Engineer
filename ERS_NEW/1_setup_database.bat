@echo off
echo =====================================================
echo  ERS - Database Setup
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
mysql -u %MYSQL_USER% -p%MYSQL_PASS% < sql\ers_schema.sql

if %ERRORLEVEL% NEQ 0 (
    echo [ERROR] Setup failed. Check credentials and MySQL server.
    pause
    exit /b 1
)

echo.
echo [OK] Database ers_db created successfully!
echo.
echo  Default login accounts:
echo    Admin    : admin     / Admin@123
echo    Examiner : examiner1 / Exm@123
echo    Examiner : examiner2 / Exm@123
echo    Student  : student1  / Std@123   (Arjun Kumar  - CS Sem 4)
echo    Student  : student2  / Std@123   (Priya Sharma - CS Sem 4)
echo    Student  : student3  / Std@123   (Rahul Verma  - EC Sem 2)
echo    Student  : student4  / Std@123   (Neha Singh   - CS Sem 6)
echo.
echo  Seed Data Included:
echo    - 12 Subjects   (CS, EC, Maths)
echo    - 4  Exams      (Sem 4, Sem 2, Sem 6 CS, Supplementary)
echo    - 14 Exam Subject schedule entries with dates and venues
echo    - 4  Registrations (3 confirmed + paid, 1 pending)
echo    - 10 Published results for Sem 4 students
echo.
echo  Next: Edit config\db.properties with your password,
echo        then run 2_compile.bat
echo.
pause

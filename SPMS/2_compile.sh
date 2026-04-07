#!/bin/bash
# ============================================================
#  SPMS – Step 2: Compile (Linux/Mac)
# ============================================================

echo "====================================================="
echo "  SPMS – Compile"
echo "====================================================="

if ! command -v javac &>/dev/null; then
    echo "[ERROR] 'javac' not found. Install JDK 8+."
    exit 1
fi

if [ ! -f "lib/mysql-connector-java.jar" ]; then
    echo "[ERROR] Missing lib/mysql-connector-java.jar"
    echo "        Download: https://dev.mysql.com/downloads/connector/j/"
    exit 1
fi

mkdir -p out

echo "[INFO] Compiling..."

javac -cp "lib/mysql-connector-java.jar" -d out -sourcepath src \
  src/com/spms/util/DBConnection.java \
  src/com/spms/util/ConsoleUtil.java \
  src/com/spms/model/User.java \
  src/com/spms/model/Employee.java \
  src/com/spms/model/Attendance.java \
  src/com/spms/model/LeaveApplication.java \
  src/com/spms/model/LeaveBalance.java \
  src/com/spms/model/Payroll.java \
  src/com/spms/model/Project.java \
  src/com/spms/model/Department.java \
  src/com/spms/model/PerformanceReview.java \
  src/com/spms/dao/UserDAO.java \
  src/com/spms/dao/EmployeeDAO.java \
  src/com/spms/dao/AttendanceDAO.java \
  src/com/spms/dao/LeaveDAO.java \
  src/com/spms/dao/PayrollDAO.java \
  src/com/spms/dao/ProjectDAO.java \
  src/com/spms/dao/PerformanceDAO.java \
  src/com/spms/main/SPMSApplication.java

if [ $? -ne 0 ]; then
    echo "[ERROR] Compilation failed."
    exit 1
fi

echo "[OK] Compiled successfully! Run ./3_run.sh to start."

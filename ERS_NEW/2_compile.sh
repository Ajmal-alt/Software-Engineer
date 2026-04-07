#!/bin/bash
echo "====================================================="
echo " ERS - Compile"
echo "====================================================="
if ! command -v javac &>/dev/null; then echo "[ERROR] javac not found."; exit 1; fi
if [ ! -f "lib/mysql-connector-java.jar" ]; then echo "[ERROR] Missing lib/mysql-connector-java.jar"; exit 1; fi
mkdir -p out
CP="lib/mysql-connector-java.jar"
SRC="src/com/ers"

echo "[INFO] Compiling util..."
javac -cp "$CP" -d out $SRC/util/DBConnection.java $SRC/util/ConsoleUtil.java
[ $? -ne 0 ] && echo "[FAILED] util" && exit 1
echo "[OK] util"

echo "[INFO] Compiling model..."
javac -cp "$CP" -d out $SRC/model/User.java $SRC/model/Student.java $SRC/model/Subject.java $SRC/model/Exam.java $SRC/model/ExamSubject.java $SRC/model/Registration.java $SRC/model/Result.java
[ $? -ne 0 ] && echo "[FAILED] model" && exit 1
echo "[OK] model"

echo "[INFO] Compiling dao..."
javac -cp "$CP:out" -d out $SRC/dao/UserDAO.java $SRC/dao/StudentDAO.java $SRC/dao/ExamDAO.java $SRC/dao/RegistrationDAO.java $SRC/dao/ResultDAO.java
[ $? -ne 0 ] && echo "[FAILED] dao" && exit 1
echo "[OK] dao"

echo "[INFO] Compiling main..."
javac -cp "$CP:out" -d out $SRC/main/ERSApplication.java
[ $? -ne 0 ] && echo "[FAILED] main" && exit 1
echo "[OK] main"

echo ""
echo "[OK] Compilation successful! Run ./3_run.sh"

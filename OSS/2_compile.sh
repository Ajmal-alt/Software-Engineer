#!/bin/bash
echo "====================================================="
echo " OSS - Compile"
echo "====================================================="
if ! command -v javac &>/dev/null; then echo "[ERROR] javac not found."; exit 1; fi
if [ ! -f "lib/mysql-connector-java.jar" ]; then echo "[ERROR] Missing lib/mysql-connector-java.jar"; exit 1; fi
mkdir -p out
CP="lib/mysql-connector-java.jar"
SRC="src/com/oss"

echo "[INFO] Compiling util..."
javac -cp "$CP" -d out $SRC/util/DBConnection.java $SRC/util/ConsoleUtil.java
[ $? -ne 0 ] && echo "[FAILED] util" && exit 1
echo "[OK] util"

echo "[INFO] Compiling model..."
javac -cp "$CP" -d out $SRC/model/User.java $SRC/model/Customer.java $SRC/model/Category.java $SRC/model/Product.java $SRC/model/CartItem.java $SRC/model/Order.java $SRC/model/OrderItem.java $SRC/model/Payment.java $SRC/model/Invoice.java
[ $? -ne 0 ] && echo "[FAILED] model" && exit 1
echo "[OK] model"

echo "[INFO] Compiling dao..."
javac -cp "$CP:out" -d out $SRC/dao/UserDAO.java $SRC/dao/CustomerDAO.java $SRC/dao/ProductDAO.java $SRC/dao/CartDAO.java $SRC/dao/OrderDAO.java $SRC/dao/PaymentDAO.java
[ $? -ne 0 ] && echo "[FAILED] dao" && exit 1
echo "[OK] dao"

echo "[INFO] Compiling main..."
javac -cp "$CP:out" -d out $SRC/main/OSSApplication.java
[ $? -ne 0 ] && echo "[FAILED] main" && exit 1
echo "[OK] main"

echo ""
echo "[OK] Compilation successful! Run ./3_run.sh"

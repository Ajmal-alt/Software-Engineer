#!/bin/bash
# ============================================================
#  SPMS – Step 1: Database Setup (Linux/Mac)
# ============================================================

echo "====================================================="
echo "  SPMS – Database Setup"
echo "====================================================="

read -p "Enter MySQL username (default: root): " MYSQL_USER
MYSQL_USER=${MYSQL_USER:-root}
read -sp "Enter MySQL password: " MYSQL_PASS
echo ""

if ! command -v mysql &>/dev/null; then
    echo "[ERROR] 'mysql' not found. Install MySQL and ensure it's in PATH."
    exit 1
fi

echo "[INFO] Running schema script..."
mysql -u "$MYSQL_USER" -p"$MYSQL_PASS" < sql/spms_schema.sql

if [ $? -ne 0 ]; then
    echo "[ERROR] Schema creation failed."
    exit 1
fi

echo "[OK] Database created and seeded!"
echo ""
echo "  Default accounts:"
echo "    admin     / Admin@123  (ADMIN)"
echo "    hrmanager / Hr@123     (HR)"
echo "    pm1       / Pm@123     (PROJECT_MANAGER)"
echo "    emp1      / Emp@123    (EMPLOYEE)"
echo "    emp2      / Emp@123    (EMPLOYEE)"
echo ""
echo "  Edit config/db.properties with your credentials, then run ./2_compile.sh"

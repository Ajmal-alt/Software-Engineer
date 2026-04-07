#!/bin/bash
echo "====================================================="
echo " MMS - Database Setup"
echo "====================================================="
read -p "MySQL username (default: root): " MYSQL_USER
MYSQL_USER=${MYSQL_USER:-root}
read -sp "MySQL password: " MYSQL_PASS
echo ""
if ! command -v mysql &>/dev/null; then
    echo "[ERROR] mysql not found. Install MySQL and add to PATH."
    exit 1
fi
mysql -u "$MYSQL_USER" -p"$MYSQL_PASS" < sql/mms_schema.sql
if [ $? -ne 0 ]; then echo "[ERROR] Setup failed."; exit 1; fi
echo "[OK] Database mms_db created!"
echo ""
echo "  Accounts: admin/Admin@123 | manager1/Mgr@123 | agent1/Agt@123"
echo ""
echo "Edit config/db.properties then run ./2_compile.sh"

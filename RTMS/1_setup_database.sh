#!/bin/bash
echo "====================================================="
echo " RTMS - Database Setup"
echo "====================================================="
read -p "MySQL username (default: root): " MYSQL_USER
MYSQL_USER=${MYSQL_USER:-root}
read -sp "MySQL password: " MYSQL_PASS
echo ""
if ! command -v mysql &>/dev/null; then echo "[ERROR] mysql not found."; exit 1; fi
mysql -u "$MYSQL_USER" -p"$MYSQL_PASS" < sql/rtms_schema.sql
if [ $? -ne 0 ]; then echo "[ERROR] Setup failed."; exit 1; fi
echo "[OK] Database rtms_db created!"
echo "  admin/Admin@123 | manager1/Mgr@123 | driver1/Drv@123"
echo "Edit config/db.properties then run ./2_compile.sh"

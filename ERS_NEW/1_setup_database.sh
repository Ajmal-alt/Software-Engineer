#!/bin/bash
echo "====================================================="
echo " ERS - Database Setup"
echo "====================================================="
read -p "MySQL username (default: root): " MYSQL_USER
MYSQL_USER=${MYSQL_USER:-root}
read -sp "MySQL password: " MYSQL_PASS
echo ""
if ! command -v mysql &>/dev/null; then echo "[ERROR] mysql not found."; exit 1; fi
mysql -u "$MYSQL_USER" -p"$MYSQL_PASS" < sql/ers_schema.sql
if [ $? -ne 0 ]; then echo "[ERROR] Setup failed."; exit 1; fi
echo "[OK] Database ers_db created!"
echo "  admin/Admin@123 | examiner1/Exm@123 | student1/Std@123"
echo "Edit config/db.properties then run ./2_compile.sh"

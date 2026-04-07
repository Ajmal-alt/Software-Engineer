#!/bin/bash
# ============================================================
#  SPMS – Step 3: Run (Linux/Mac)
# ============================================================

if [ ! -f "out/com/spms/main/SPMSApplication.class" ]; then
    echo "[ERROR] Not compiled yet. Run ./2_compile.sh first."
    exit 1
fi

java -cp "out:lib/mysql-connector-java.jar" com.spms.main.SPMSApplication

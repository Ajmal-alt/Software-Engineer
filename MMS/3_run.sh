#!/bin/bash
if [ ! -f "out/com/mms/main/MMSApplication.class" ]; then
    echo "[ERROR] Not compiled. Run ./2_compile.sh first."
    exit 1
fi
java -cp "out:lib/mysql-connector-java.jar" com.mms.main.MMSApplication

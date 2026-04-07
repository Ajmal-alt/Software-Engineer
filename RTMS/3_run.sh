#!/bin/bash
if [ ! -f "out/com/rtms/main/RTMSApplication.class" ]; then
    echo "[ERROR] Not compiled. Run ./2_compile.sh first."
    exit 1
fi
java -cp "out:lib/mysql-connector-java.jar" com.rtms.main.RTMSApplication

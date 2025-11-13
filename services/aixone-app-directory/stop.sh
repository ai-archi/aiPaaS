#!/bin/bash

# Directory 服务停止脚本
# 用于停止正在运行的 Directory 服务

# 获取项目根目录
PROJECT_DIR="$(cd "$(dirname "${BASH_SOURCE[0]}")" && pwd)"
cd "$PROJECT_DIR"

# 主类名（用于查找进程）
MAIN_CLASS="com.aixone.directory.DirectoryServeApplication"

echo "查找 Directory 服务进程..."
# 查找运行中的进程
PID=$(ps aux | grep "[j]ava.*${MAIN_CLASS}" | awk '{print $2}')

if [ -z "$PID" ]; then
    echo "Directory 服务未运行"
    exit 0
fi

echo "找到 Directory 服务进程: PID=${PID}"
echo "正在停止服务..."

# 优雅停止（发送 SIGTERM 信号）
kill -TERM "$PID" 2>/dev/null

# 等待进程停止（最多等待 10 秒）
WAIT_TIME=0
MAX_WAIT=10
while [ $WAIT_TIME -lt $MAX_WAIT ]; do
    if ! ps -p "$PID" > /dev/null 2>&1; then
        echo "Directory 服务已成功停止"
        exit 0
    fi
    sleep 1
    WAIT_TIME=$((WAIT_TIME + 1))
done

# 如果进程仍在运行，强制停止
if ps -p "$PID" > /dev/null 2>&1; then
    echo "服务未在 ${MAX_WAIT} 秒内停止，强制停止..."
    kill -9 "$PID" 2>/dev/null
    sleep 1
    
    if ps -p "$PID" > /dev/null 2>&1; then
        echo "警告: 无法停止进程 ${PID}"
        exit 1
    else
        echo "Directory 服务已强制停止"
        exit 0
    fi
else
    echo "Directory 服务已成功停止"
    exit 0
fi


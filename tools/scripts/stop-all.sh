#!/bin/bash

# 获取项目根目录
PROJECT_ROOT="$(cd "$(dirname "${BASH_SOURCE[0]}")/../.." && pwd)"

# 停止Java进程
pkill -f "spring-boot:run"

# 停止Python进程
pkill -f "python app.py"

# 停止Node.js进程
pkill -f "next start"

# 停止Nacos
bash "${PROJECT_ROOT}/deploy/nacos/nacos/bin/shutdown.sh"

echo "所有服务已停止"

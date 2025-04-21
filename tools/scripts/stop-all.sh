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
bash "${PROJECT_ROOT}/dist/nacos/bin/shutdown.sh"
# 停止 API Gateway
pkill -f "java -jar ${PROJECT_ROOT}/dist/services/api-gateway-0.0.1-SNAPSHOT.jar"

echo "所有服务已停止"

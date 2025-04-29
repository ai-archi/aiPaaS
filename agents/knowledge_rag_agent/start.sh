#!/bin/bash

# 激活虚拟环境
source ../../.venv/bin/activate

# 设置 PYTHONPATH
export PYTHONPATH=$PYTHONPATH:$(pwd)/src

# 设置环境变量
export NACOS_SERVER_ADDR=${NACOS_SERVER_ADDR:-"127.0.0.1:8848"}
export NACOS_NAMESPACE=${NACOS_NAMESPACE:-"public"}
export NACOS_SERVICE_NAME=${NACOS_SERVICE_NAME:-"knowledge-rag-agent"}
export NACOS_IP=${NACOS_IP:-"127.0.0.1"}
export NACOS_PORT=${NACOS_PORT:-"8002"}

# 启动服务
python src/main.py 
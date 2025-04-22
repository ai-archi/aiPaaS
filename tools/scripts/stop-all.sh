#!/bin/bash

# 获取项目根目录
PROJECT_ROOT="$(cd "$(dirname "${BASH_SOURCE[0]}")/../.." && pwd)"
CONFIG_FILE="${PROJECT_ROOT}/tools/config/application.yaml"

# 设置颜色输出
GREEN='\033[0;32m'
RED='\033[0;31m'
NC='\033[0m' # No Color

# 解析命令行参数
FORCE_STOP=false
while [[ $# -gt 0 ]]; do
    case $1 in
        -f|--force)
            FORCE_STOP=true
            shift
            ;;
        *)
            echo "未知参数: $1"
            echo "用法: $0 [-f|--force]"
            echo "  -f, --force    强制停止所有服务（使用kill -9）"
            exit 1
            ;;
    esac
done

# 打印信息函数
print_info() {
    echo -e "${GREEN}[INFO]${NC} $1"
}

print_error() {
    echo -e "${RED}[ERROR]${NC} $1"
}

# 读取配置文件函数
get_config() {
    local key=$1
    local default_value=$2
    value=$(yq eval "$key" "$CONFIG_FILE")
    if [ "$value" = "null" ]; then
        echo "$default_value"
    else
        echo "$value"
    fi
}

# 停止指定端口的进程
stop_port() {
    local port=$1
    local force=$2
    if [ -z "$port" ]; then
        return 1
    fi
    local pid=$(lsof -ti:${port})
    if [ ! -z "$pid" ]; then
        print_info "正在停止端口 ${port} 的进程 (PID: ${pid})..."
        if [ "$force" = true ]; then
            kill -9 $pid 2>/dev/null || true
        else
            kill $pid 2>/dev/null || true
            # 等待进程退出，最多等待5秒
            local count=0
            while kill -0 $pid 2>/dev/null && [ $count -lt 5 ]; do
                sleep 1
                ((count++))
            done
            # 如果进程仍然存在，使用强制终止
            if kill -0 $pid 2>/dev/null; then
                print_info "进程未响应正常终止信号，使用强制终止..."
                kill -9 $pid 2>/dev/null || true
            fi
        fi
        sleep 1
    fi
}

# 读取Task Agent端口
TASK_AGENT_PORT=$(get_config '.ports.task_agent' '8085')

# 停止Java服务
print_info "停止Java服务..."
if [ "$FORCE_STOP" = true ]; then
    pkill -9 -f "${PROJECT_ROOT}/dist/services/api-gateway.*\.jar"
    pkill -9 -f "${PROJECT_ROOT}/dist/services/auth-service.*\.jar"
    pkill -9 -f "${PROJECT_ROOT}/dist/services/user-service.*\.jar"
    pkill -9 -f "${PROJECT_ROOT}/dist/services/java-agent.*\.jar"
else
    pkill -f "${PROJECT_ROOT}/dist/services/api-gateway.*\.jar"
    pkill -f "${PROJECT_ROOT}/dist/services/auth-service.*\.jar"
    pkill -f "${PROJECT_ROOT}/dist/services/user-service.*\.jar"
    pkill -f "${PROJECT_ROOT}/dist/services/java-agent.*\.jar"
    # 等待进程退出
    sleep 2
    # 检查是否需要强制终止
    for pattern in "api-gateway" "auth-service" "user-service" "java-agent"; do
        if pgrep -f "${PROJECT_ROOT}/dist/services/${pattern}.*\.jar" > /dev/null; then
            print_info "${pattern} 服务未响应正常终止信号，使用强制终止..."
            pkill -9 -f "${PROJECT_ROOT}/dist/services/${pattern}.*\.jar"
        fi
    done
fi

# 停止Python服务
print_info "停止Python服务..."
print_info "正在停止Task Agent (端口: ${TASK_AGENT_PORT})..."
stop_port "${TASK_AGENT_PORT}" "$FORCE_STOP"

# 确保所有Python相关进程都被停止
if [ "$FORCE_STOP" = true ]; then
    pkill -9 -f "${PROJECT_ROOT}/dist/agents/.*/run.py"
    pkill -9 -f "${PROJECT_ROOT}/dist/agents/.*/app.py"
    pkill -9 -f "${PROJECT_ROOT}/dist/penv/bin/python"
else
    pkill -f "${PROJECT_ROOT}/dist/agents/.*/run.py"
    pkill -f "${PROJECT_ROOT}/dist/agents/.*/app.py"
    pkill -f "${PROJECT_ROOT}/dist/penv/bin/python"
    # 等待进程退出
    sleep 2
    # 检查是否需要强制终止
    if pgrep -f "${PROJECT_ROOT}/dist/agents/.*/(run|app)\.py" > /dev/null || \
       pgrep -f "${PROJECT_ROOT}/dist/penv/bin/python" > /dev/null; then
        print_info "Python服务未响应正常终止信号，使用强制终止..."
        pkill -9 -f "${PROJECT_ROOT}/dist/agents/.*/run.py"
        pkill -9 -f "${PROJECT_ROOT}/dist/agents/.*/app.py"
        pkill -9 -f "${PROJECT_ROOT}/dist/penv/bin/python"
    fi
fi

# 停止前端服务
print_info "停止前端服务..."
if [ "$FORCE_STOP" = true ]; then
    pkill -9 -f "${PROJECT_ROOT}/dist/apps/web/.next"
else
    pkill -f "${PROJECT_ROOT}/dist/apps/web/.next"
    sleep 2
    if pgrep -f "${PROJECT_ROOT}/dist/apps/web/.next" > /dev/null; then
        print_info "前端服务未响应正常终止信号，使用强制终止..."
        pkill -9 -f "${PROJECT_ROOT}/dist/apps/web/.next"
    fi
fi

# 停止Nacos
print_info "停止Nacos服务..."
if [ -f "${PROJECT_ROOT}/dist/nacos/bin/shutdown.sh" ]; then
    bash "${PROJECT_ROOT}/dist/nacos/bin/shutdown.sh"
    if [ "$FORCE_STOP" = true ]; then
        # 强制模式下，确保Nacos进程被终止
        sleep 2
        if pgrep -f "${PROJECT_ROOT}/dist/nacos" > /dev/null; then
            print_info "Nacos服务未响应正常终止信号，使用强制终止..."
            pkill -9 -f "${PROJECT_ROOT}/dist/nacos"
        fi
    fi
else
    print_error "Nacos shutdown脚本不存在"
    if [ "$FORCE_STOP" = true ]; then
        print_info "强制终止所有Nacos进程..."
        pkill -9 -f "${PROJECT_ROOT}/dist/nacos"
    fi
fi

# 验证所有服务是否已停止
print_info "验证服务状态..."
sleep 2

# 检查是否还有服务在运行
echo "========================================"
print_info "检查服务状态："
echo "----------------------------------------"
print_info "Nacos服务："
ps aux | grep "${PROJECT_ROOT}/dist/nacos" | grep -v grep
echo "----------------------------------------"
print_info "Java服务："
ps aux | grep "${PROJECT_ROOT}/dist/services/.*\.jar" | grep -v grep
echo "----------------------------------------"
print_info "Python服务："
ps aux | grep -E "${PROJECT_ROOT}/(dist/agents/.*/(run|app)\.py|dist/penv/bin/python)" | grep -v grep
print_info "检查Task Agent端口 ${TASK_AGENT_PORT}..."
if lsof -i:${TASK_AGENT_PORT} >/dev/null 2>&1; then
    print_error "端口 ${TASK_AGENT_PORT} 仍被占用"
    lsof -i:${TASK_AGENT_PORT}
else
    print_info "端口 ${TASK_AGENT_PORT} 已释放"
fi
echo "----------------------------------------"
print_info "前端服务："
ps aux | grep "${PROJECT_ROOT}/dist/apps/web/.next" | grep -v grep
echo "========================================"

# 如果还有服务在运行，显示错误信息
print_info "检查未完全停止的服务："

# 检查Nacos服务
if ps aux | grep "${PROJECT_ROOT}/dist/nacos" | grep -v grep > /dev/null; then
    print_error "- Nacos服务仍在运行"
fi

# 检查Java服务
if ps aux | grep "${PROJECT_ROOT}/dist/services/.*\.jar" | grep -v grep > /dev/null; then
    print_error "- 以下Java服务仍在运行："
    ps aux | grep "${PROJECT_ROOT}/dist/services/.*\.jar" | grep -v grep | awk '{print "  * " $NF}'
fi

# 检查Python服务
if ps aux | grep -E "${PROJECT_ROOT}/(dist/agents/.*/(run|app)\.py|dist/penv/bin/python)" | grep -v grep > /dev/null; then
    print_error "- 以下Python服务仍在运行："
    ps aux | grep -E "${PROJECT_ROOT}/(dist/agents/.*/(run|app)\.py|dist/penv/bin/python)" | grep -v grep | awk '{print "  * " $NF}'
fi

# 检查前端服务
if ps aux | grep "${PROJECT_ROOT}/dist/apps/web/.next" | grep -v grep > /dev/null; then
    print_error "- 前端服务仍在运行"
fi

# 检查是否所有服务都已停止
if ! ps aux | grep -E "${PROJECT_ROOT}/(dist/services/.*\.jar|dist/agents/.*/(run|app)\.py|dist/penv/bin/python|dist/apps/web/.next|dist/nacos)" | grep -v grep > /dev/null; then
    print_info "所有服务已成功停止"
fi

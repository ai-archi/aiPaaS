#!/bin/bash

# 获取项目根目录
PROJECT_ROOT="$(cd "$(dirname "${BASH_SOURCE[0]}")/../.." && pwd)"
CONFIG_FILE="${PROJECT_ROOT}/tools/config/application.yaml"

# 设置颜色输出
GREEN='\033[0;32m'
RED='\033[0;31m'
NC='\033[0m' # No Color

# 初始化变量
FORCE_STOP=false
SPECIFIED_SERVICE=""

# 显示帮助信息
show_help() {
    echo "用法: $0 [-f|--force] [-s|--service <service_name>]"
    echo "选项:"
    echo "  -f, --force              强制停止服务（使用kill -9）"
    echo "  -s, --service SERVICE    指定要停止的服务"
    echo "可用的服务:"
    echo "  task-agent               Task Agent服务"
    echo "  api-gateway             API网关服务"
    echo "  auth-service            认证服务"
    echo "  user-service            用户服务"
    echo "  java-agent              Java Agent服务"
    echo "  web                     前端服务"
    echo "  nacos                   Nacos服务"
    echo "  all                     所有服务（默认）"
    exit 1
}

# 解析命令行参数
while [[ $# -gt 0 ]]; do
    case $1 in
        -f|--force)
            FORCE_STOP=true
            shift
            ;;
        -s|--service)
            if [ -z "$2" ]; then
                print_error "错误：--service 选项需要一个参数"
                show_help
            fi
            SPECIFIED_SERVICE="$2"
            shift 2
            ;;
        -h|--help)
            show_help
            ;;
        *)
            echo "未知参数: $1"
            show_help
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

# 停止Task Agent服务
stop_task_agent() {
    print_info "停止Task Agent服务..."
    local TASK_AGENT_PORT=$(get_config '.ports.task_agent' '8085')
    print_info "正在停止Task Agent (端口: ${TASK_AGENT_PORT})..."
    stop_port "${TASK_AGENT_PORT}" "$FORCE_STOP"
    if [ "$FORCE_STOP" = true ]; then
        pkill -9 -f "${PROJECT_ROOT}/dist/agents/task-agent/run.py" || true
    else
        pkill -f "${PROJECT_ROOT}/dist/agents/task-agent/run.py" || true
        sleep 2
        if pgrep -f "${PROJECT_ROOT}/dist/agents/task-agent/run.py" > /dev/null; then
            print_info "Task Agent未响应正常终止信号，使用强制终止..."
            pkill -9 -f "${PROJECT_ROOT}/dist/agents/task-agent/run.py" || true
        fi
    fi
}

# 停止Java服务
stop_java_service() {
    local service_name=$1
    print_info "停止 ${service_name}..."
    if [ "$FORCE_STOP" = true ]; then
        pkill -9 -f "${PROJECT_ROOT}/dist/services/${service_name}.*\.jar" || true
    else
        pkill -f "${PROJECT_ROOT}/dist/services/${service_name}.*\.jar" || true
        sleep 2
        if pgrep -f "${PROJECT_ROOT}/dist/services/${service_name}.*\.jar" > /dev/null; then
            print_info "${service_name} 未响应正常终止信号，使用强制终止..."
            pkill -9 -f "${PROJECT_ROOT}/dist/services/${service_name}.*\.jar" || true
        fi
    fi
}

# 停止前端服务
stop_web_service() {
    print_info "停止前端服务..."
    if [ "$FORCE_STOP" = true ]; then
        pkill -9 -f "${PROJECT_ROOT}/dist/apps/web/.next" || true
    else
        pkill -f "${PROJECT_ROOT}/dist/apps/web/.next" || true
        sleep 2
        if pgrep -f "${PROJECT_ROOT}/dist/apps/web/.next" > /dev/null; then
            print_info "前端服务未响应正常终止信号，使用强制终止..."
            pkill -9 -f "${PROJECT_ROOT}/dist/apps/web/.next" || true
        fi
    fi
}

# 停止Nacos服务
stop_nacos() {
    print_info "停止Nacos服务..."
    if [ -f "${PROJECT_ROOT}/dist/nacos/bin/shutdown.sh" ]; then
        bash "${PROJECT_ROOT}/dist/nacos/bin/shutdown.sh"
        if [ "$FORCE_STOP" = true ]; then
            sleep 2
            if pgrep -f "${PROJECT_ROOT}/dist/nacos" > /dev/null; then
                print_info "Nacos服务未响应正常终止信号，使用强制终止..."
                pkill -9 -f "${PROJECT_ROOT}/dist/nacos" || true
            fi
        fi
    else
        print_error "Nacos shutdown脚本不存在"
        if [ "$FORCE_STOP" = true ]; then
            print_info "强制终止所有Nacos进程..."
            pkill -9 -f "${PROJECT_ROOT}/dist/nacos" || true
        fi
    fi
}

stop_knowledge_rag_agent() {
    print_info "停止 knowledge_rag_agent..."
    lsof -ti:8002 | xargs kill -9 2>/dev/null || true
    pkill -f "uvicorn src.main:app" 2>/dev/null || true
}

stop_embed_serves() {
    print_info "停止 embed-serves..."
    lsof -ti:8003 | xargs kill -9 2>/dev/null || true
    pkill -f "uvicorn main:app" 2>/dev/null || true
}

# 根据指定的服务名称停止服务
stop_service() {
    case $1 in
        "knowledge_rag_agent")
            stop_knowledge_rag_agent
            ;;
        "embed-serves")
            stop_embed_serves
            ;;
        "api-gateway")
            stop_java_service "api-gateway"
            ;;
        "auth-service")
            stop_java_service "auth-service"
            ;;
        "user-service")
            stop_java_service "user-service"
            ;;
        "java-agent")
            stop_java_service "java-agent"
            ;;
        "web")
            stop_web_service
            ;;
        "nacos")
            stop_nacos
            ;;
        "all")
            stop_knowledge_rag_agent
            stop_embed_serves
            stop_java_service "api-gateway"
            stop_java_service "auth-service"
            stop_java_service "user-service"
            stop_java_service "java-agent"
            stop_web_service
            stop_nacos
            ;;
        *)
            print_error "未知的服务: $1"
            show_help
            ;;
    esac
}

# 主逻辑
if [ -z "$SPECIFIED_SERVICE" ]; then
    SPECIFIED_SERVICE="all"
fi

stop_service "$SPECIFIED_SERVICE"

# 验证服务状态
print_info "验证 $SPECIFIED_SERVICE 服务状态..."
sleep 2

case $SPECIFIED_SERVICE in
    "knowledge_rag_agent")
        if lsof -i:8002 >/dev/null 2>&1; then
            print_error "knowledge_rag_agent (端口 8002) 仍在运行"
        else
            print_info "knowledge_rag_agent 已成功停止"
        fi
        ;;
    "embed-serves")
        if lsof -i:8003 >/dev/null 2>&1; then
            print_error "embed-serves (端口 8003) 仍在运行"
        else
            print_info "embed-serves 已成功停止"
        fi
        ;;
    "all")
        # 检查所有服务状态
        echo "========================================"
        print_info "检查所有服务状态："
        if ! ps aux | grep -E "${PROJECT_ROOT}/(dist/services/.*\.jar|agents/knowledge_rag_agent/src/main.py|services/embed-serves/main.py|dist/apps/web/.next|dist/nacos)" | grep -v grep > /dev/null; then
            print_info "所有服务已成功停止"
        else
            print_error "以下服务仍在运行："
            ps aux | grep -E "${PROJECT_ROOT}/(dist/services/.*\.jar|agents/knowledge_rag_agent/src/main.py|services/embed-serves/main.py|dist/apps/web/.next|dist/nacos)" | grep -v grep
        fi
        ;;
    *)
        if ps aux | grep -E "${PROJECT_ROOT}.*${SPECIFIED_SERVICE}" | grep -v grep > /dev/null; then
            print_error "$SPECIFIED_SERVICE 仍在运行"
        else
            print_info "$SPECIFIED_SERVICE 已成功停止"
        fi
        ;;
esac

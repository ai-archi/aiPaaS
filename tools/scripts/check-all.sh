#!/bin/bash

# 设置颜色输出
GREEN='\033[0;32m'
YELLOW='\033[1;33m'
RED='\033[0;31m'
NC='\033[0m' # No Color

# 默认配置
DEBUG_MODE=false

# 解析命令行参数
while [[ $# -gt 0 ]]; do
    case $1 in
        --debug)
            DEBUG_MODE=true
            shift
            ;;
        *)
            echo -e "${RED}[ERROR]${NC} 未知参数: $1"
            echo "用法: $0 [--debug]"
            exit 1
            ;;
    esac
done

# 获取项目根目录
PROJECT_ROOT="$(cd "$(dirname "${BASH_SOURCE[0]}")/../.." && pwd)"
CONFIG_FILE="${PROJECT_ROOT}/tools/config/application.yaml"

# 调试信息输出函数
debug_info() {
    if [ "$DEBUG_MODE" = true ]; then
        echo -e "${GREEN}[DEBUG]${NC} $1"
    fi
}

# 打印带颜色的信息函数
print_info() {
    if [ "$DEBUG_MODE" = true ] || [ "$2" = "force" ]; then
        echo -e "${GREEN}[INFO]${NC} $1"
    fi
}

print_warn() {
    echo -e "${YELLOW}[WARN]${NC} $1"
}

print_error() {
    echo -e "${RED}[ERROR]${NC} $1"
}

print_status() {
    local service_name=$1
    local status=$2
    local port=$3
    local pid=$4
    
    if [ "$status" = "running" ]; then
        echo -e "${GREEN}[运行中]${NC} $service_name (端口: $port, PID: $pid)"
    else
        echo -e "${RED}[已停止]${NC} $service_name (端口: $port)"
    fi
}

# 检查并移动日志文件
check_and_move_logs() {
    # 创建日志目录
    mkdir -p "${PROJECT_ROOT}/dist/logs"
    debug_info "确保日志目录存在: ${PROJECT_ROOT}/dist/logs"
    
    # 检查根目录下的日志文件
    local log_files=($(find "${PROJECT_ROOT}/logs" -name "access_log.*.log" 2>/dev/null))
    
    if [ ${#log_files[@]} -gt 0 ]; then
        print_info "发现日志文件，开始处理..." "force"
        
        for log_file in "${log_files[@]}"; do
            local filename=$(basename "$log_file")
            local date_part=$(echo "$filename" | grep -o '[0-9]\{4\}-[0-9]\{2\}-[0-9]\{2\}-[0-9]\{2\}')
            
            if [ -n "$date_part" ]; then
                local new_filename="api-gateway-access_log.${date_part}.log"
                local new_path="${PROJECT_ROOT}/dist/logs/${new_filename}"
                
                debug_info "移动日志文件: $filename -> $new_filename"
                
                # 移动并重命名日志文件
                mv "$log_file" "$new_path"
                
                if [ $? -eq 0 ]; then
                    print_info "日志文件已移动: $new_filename" "force"
                else
                    print_error "移动日志文件失败: $filename"
                fi
            else
                print_warn "无法解析日志文件日期: $filename"
            fi
        done
    else
        debug_info "未发现需要处理的日志文件"
    fi
}

# 检查端口是否被占用
check_port() {
    local port=$1
    if lsof -i :$port > /dev/null 2>&1; then
        echo "running"
    else
        echo "stopped"
    fi
}

# 获取进程ID
get_pid() {
    local port=$1
    local pid=$(lsof -t -i :$port 2>/dev/null)
    echo $pid
}

# 检查进程是否为指定服务
check_process() {
    local pid=$1
    local service_pattern=$2
    if [ -n "$pid" ]; then
        if ps -p $pid -o command= | grep -q "$service_pattern"; then
            return 0
        fi
    fi
    return 1
}

# 读取配置文件函数
get_config() {
    local key=$1
    local default_value=$2
    if ! command -v yq &> /dev/null; then
        print_error "yq 未安装，无法读取配置文件"
        return
    fi
    value=$(yq eval "$key" "$CONFIG_FILE")
    if [ "$value" = "null" ]; then
        echo "$default_value"
    else
        echo "$value"
    fi
}

# 检查API网关状态
check_api_gateway() {
    local gateway_port=$(get_config '.services.api-gateway.port' '8080')
    local status=$(check_port $gateway_port)
    local pid=$(get_pid $gateway_port)
    
    debug_info "检查API网关状态 (端口: $gateway_port)"
    
    if [ "$status" = "running" ]; then
        if check_process $pid "api-gateway"; then
            print_status "API网关" "running" $gateway_port $pid
            
            # 检查日志文件
            local log_dir="${PROJECT_ROOT}/dist/logs"
            local today_date=$(date +"%Y-%m-%d-%H")
            local log_file="${log_dir}/api-gateway-access_log.${today_date}.log"
            
            if [ -f "$log_file" ]; then
                echo -e "  ${GREEN}[日志]${NC} 当前日志文件: $(basename "$log_file")"
                if [ "$DEBUG_MODE" = true ]; then
                    echo -e "  ${GREEN}[日志]${NC} 最后10行日志:"
                    tail -n 10 "$log_file"
                fi
            else
                echo -e "  ${YELLOW}[日志]${NC} 未找到当前日志文件"
            fi
        else
            print_warn "端口 $gateway_port 被其他进程占用"
        fi
    else
        print_status "API网关" "stopped" $gateway_port
    fi
}

# 检查Task Agent状态
check_task_agent() {
    local task_agent_port=$(get_config '.services.task-agent.port' '8085')
    local status=$(check_port $task_agent_port)
    local pid=$(get_pid $task_agent_port)
    
    debug_info "检查Task Agent状态 (端口: $task_agent_port)"
    
    if [ "$status" = "running" ]; then
        if check_process $pid "task-agent"; then
            print_status "Task Agent" "running" $task_agent_port $pid
            
            # 检查健康状态
            if curl -s "http://localhost:$task_agent_port/api/v1/tasks/health" > /dev/null 2>&1; then
                echo -e "  ${GREEN}[健康检查]${NC} 服务响应正常"
            else
                echo -e "  ${RED}[健康检查]${NC} 服务无响应"
            fi
        else
            print_warn "端口 $task_agent_port 被其他进程占用"
        fi
    else
        print_status "Task Agent" "stopped" $task_agent_port
    fi
}

# 检查前端服务状态
check_frontend() {
    local frontend_port=$(get_config '.services.frontend.port' '3000')
    local status=$(check_port $frontend_port)
    local pid=$(get_pid $frontend_port)
    
    debug_info "检查前端服务状态 (端口: $frontend_port)"
    
    if [ "$status" = "running" ]; then
        if check_process $pid "next"; then
            print_status "前端服务" "running" $frontend_port $pid
            
            # 检查健康状态
            if curl -s "http://localhost:$frontend_port" > /dev/null 2>&1; then
                echo -e "  ${GREEN}[健康检查]${NC} 服务响应正常"
            else
                echo -e "  ${RED}[健康检查]${NC} 服务无响应"
            fi
        else
            print_warn "端口 $frontend_port 被其他进程占用"
        fi
    else
        print_status "前端服务" "stopped" $frontend_port
    fi
}

# 检查Nacos服务状态
check_nacos() {
    local nacos_port=$(get_config '.services.nacos.port' '8848')
    local status=$(check_port $nacos_port)
    local pid=$(get_pid $nacos_port)
    
    debug_info "检查Nacos服务状态 (端口: $nacos_port)"
    
    if [ "$status" = "running" ]; then
        if check_process $pid "nacos"; then
            print_status "Nacos服务" "running" $nacos_port $pid
            
            # 检查健康状态
            if curl -s "http://localhost:$nacos_port/nacos/actuator/health" > /dev/null 2>&1; then
                echo -e "  ${GREEN}[健康检查]${NC} 服务响应正常"
            else
                echo -e "  ${RED}[健康检查]${NC} 服务无响应"
            fi
        else
            print_warn "端口 $nacos_port 被其他进程占用"
        fi
    else
        print_status "Nacos服务" "stopped" $nacos_port
    fi
}

# 主函数
main() {
    print_info "开始检查服务状态..." "force"
    echo "----------------------------------------"
    
    # 检查配置文件
    if [ ! -f "$CONFIG_FILE" ]; then
        print_warn "配置文件不存在: $CONFIG_FILE，将使用默认端口"
    fi
    
    # 检查并移动日志文件
    check_and_move_logs
    echo "----------------------------------------"
    
    # 检查各个服务
    check_nacos
    echo "----------------------------------------"
    
    check_api_gateway
    echo "----------------------------------------"
    
    check_task_agent
    echo "----------------------------------------"
    
    check_frontend
    echo "----------------------------------------"
    
    print_info "服务状态检查完成" "force"
}

# 执行主函数
main 
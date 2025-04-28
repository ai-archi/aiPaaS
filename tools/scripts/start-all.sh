#!/bin/bash

# 设置颜色输出
GREEN='\033[0;32m'
YELLOW='\033[1;33m'
RED='\033[0;31m'
NC='\033[0m' # No Color

# 获取项目根目录
PROJECT_ROOT="$(cd "$(dirname "${BASH_SOURCE[0]}")/../.." && pwd)"
CONFIG_FILE="${PROJECT_ROOT}/tools/config/application.yaml"

# 获取当前日期
get_date() {
    date '+%Y%m%d'
}

# 生成日志文件名
get_log_filename() {
    local service_name=$1
    local is_access_log=${2:-false}
    local current_date=$(get_date)
    
    if [ "$is_access_log" = true ]; then
        echo "${service_name}_access_log.${current_date}.log"
    else
        echo "${service_name}_log.${current_date}.log"
    fi
}

# 检查yq是否安装
if ! command -v yq &> /dev/null; then
    echo "正在安装yq..."
    if [[ "$OSTYPE" == "darwin"* ]]; then
        brew install yq
    else
        sudo wget https://github.com/mikefarah/yq/releases/latest/download/yq_linux_amd64 -O /usr/bin/yq && sudo chmod +x /usr/bin/yq
    fi
fi

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

# 打印带颜色的信息函数
print_info() {
    echo -e "${GREEN}[INFO]${NC} $1"
}

print_warn() {
    echo -e "${YELLOW}[WARN]${NC} $1"
}

print_error() {
    echo -e "${RED}[ERROR]${NC} $1"
}

# 检查Java版本
check_java_version() {
    local required_version=$1
    local java_version=$(java -version 2>&1 | awk -F '"' '/version/ {print $2}' | awk -F. '{print $1}')
    
    if [ -z "$java_version" ] || [ "$java_version" -lt "$required_version" ]; then
        print_error "需要JDK ${required_version}或更高版本"
        print_error "当前Java版本: $(java -version 2>&1 | head -n 1)"
        exit 1
    fi
}

# 检查Java环境
check_java() {
    if ! command -v java &> /dev/null; then
        print_error "Java未安装，请安装JDK 21或更高版本"
        exit 1
    fi
    
    check_java_version 21
    java_version=$(java -version 2>&1 | awk -F '"' '/version/ {print $2}')
    print_info "检测到Java版本: $java_version"
}


# 检查Python环境
check_python() {
    if ! command -v python3 &> /dev/null; then
        print_error "Python3未安装，请安装Python 3.10或更高版本"
        exit 1
    fi
    
    python_version=$(python3 --version 2>&1 | awk '{print $2}')
    print_info "检测到Python版本: $python_version"
}

# 检查Node.js环境
check_node() {
    if ! command -v node &> /dev/null; then
        print_error "Node.js未安装，请安装Node.js"
        exit 1
    fi
    
    node_version=$(node --version)
    print_info "检测到Node.js版本: $node_version"
}

# 通用下载函数
download_file() {
    local url=$1
    local output_file=$2
    local description=$3
    
    print_info "正在下载 ${description}..."
    if ! curl -L "${url}" -o "${output_file}"; then
        print_error "${description} 下载失败！"
        return 1
    fi
    return 0
}

# 通用服务等待函数
wait_for_service() {
    local service_name=$1
    local port=$2
    local pid=$3
    local max_attempts=${4:-30}
    
    print_info "等待 ${service_name} 启动..."
    local count=0
    while ! nc -z localhost ${port} && [ $count -lt $max_attempts ]; do
        sleep 2
        ((count++))
        echo -n "."
    done
    echo ""
    
    if nc -z localhost ${port}; then
        print_info "${service_name} 启动成功！(PID: ${pid})"
        return 0
    else
        print_error "${service_name} 启动失败！"
        return 1
    fi
}

# 启动服务
start_service() {
    local service_name=$1
    local start_cmd=$2
    local log_file=$3
    local port=$4
    local context_path=$5
    local process_pattern=$6

    # 确保日志目录存在
    local log_dir=$(dirname "$log_file")
    mkdir -p "$log_dir"

    print_info "启动${service_name}服务..."
    print_info "- 启动命令: ${start_cmd}"
    print_info "- 日志文件: ${log_file}"
    print_info "- 端口: ${port}"
    print_info "- 上下文路径: ${context_path}"

    # 检查端口占用情况
    if lsof -i:${port} > /dev/null 2>&1; then
        # 检查是否是相同的服务
        if [ ! -z "${process_pattern}" ] && ps aux | grep "${process_pattern}" | grep -v grep > /dev/null; then
            print_warn "${service_name}服务已在运行中（端口 ${port}）"
            print_info "- 访问地址: http://localhost:${port}${context_path}"
            print_info "- 日志文件: ${log_file}"
            return 0
        else
            print_error "端口 ${port} 被其他服务占用！"
            print_info "正在检查占用进程..."
            lsof -i:${port}
            return 1
        fi
    fi

    # 启动服务
    nohup ${start_cmd} > "${log_file}" 2>&1 &
    local pid=$!
    print_info "服务已启动，进程ID: ${pid}"

    # 等待服务启动
    local max_wait=30
    local wait_count=0
    while ! lsof -i:${port} > /dev/null 2>&1; do
        sleep 1
        wait_count=$((wait_count + 1))
        if [ ${wait_count} -ge ${max_wait} ]; then
            print_error "服务启动超时（${max_wait}秒）"
            return 1
        fi
        if ! ps -p ${pid} > /dev/null 2>&1; then
            print_error "服务进程已退出"
            return 1
        fi
    done

    print_info "服务启动成功！"
    print_info "- 访问地址: http://localhost:${port}${context_path}"
    print_info "- 日志文件: ${log_file}"
    return 0
}

# 启动Java服务
build_and_start_java_services() {
    print_info "正在启动Java服务..."
    cd "${PROJECT_ROOT}"
    
    # 读取服务配置
    local api_gateway_port=$(get_config '.ports.api_gateway' '8080')
    local auth_service_port=$(get_config '.ports.auth_service' '8081')
    local user_service_port=$(get_config '.ports.user_service' '8082')
    local java_agent_port=$(get_config '.ports.java_agent' '8083')
    
    local api_gateway_profile=$(get_config '.services.api_gateway.spring_profile' 'dev')
    local auth_service_profile=$(get_config '.services.auth_service.spring_profile' 'dev')
    local user_service_profile=$(get_config '.services.user_service.spring_profile' 'dev')
    local java_agent_profile=$(get_config '.services.java_agent.spring_profile' 'dev')
    
    local api_gateway_path=$(get_config '.services.api_gateway.context_path' '/api')
    local auth_service_path=$(get_config '.services.auth_service.context_path' '/auth')
    local user_service_path=$(get_config '.services.user_service.context_path' '/user')
    local java_agent_path=$(get_config '.services.java_agent.context_path' '/java-agent')
    
    # 创建日志目录
    local log_dir="${PROJECT_ROOT}/$(get_config '.logging.dir' 'dist/logs')"
    mkdir -p ${log_dir}
    echo "日志目录: ${log_dir}"

    local dist_dir="${PROJECT_ROOT}/dist/services"
    
    # 启动认证服务
    if [ -f "${dist_dir}/auth-service-1.0.0.jar" ]; then
        local auth_log_file="${log_dir}/$(get_log_filename "auth-service")"
        start_service "认证服务" \
            "java -jar ${dist_dir}/auth-service-1.0.0.jar \
            --spring.profiles.active=${auth_service_profile} \
            --server.port=${auth_service_port} \
            --logging.file.path=${log_dir} \
            --logging.file.name=${auth_log_file} \
            --logging.config= \
            --logging.file.max-size=100MB \
            --logging.file.max-history=30" \
            "${auth_log_file}" \
            ${auth_service_port} \
            ${auth_service_path} \
            "java -jar ${dist_dir}/auth-service-1.0.0.jar"
    else
        print_warn "认证服务jar包不存在，跳过启动"
    fi
    
    # 启动用户服务
    if [ -f "${dist_dir}/user-service-1.0.0.jar" ]; then
        local user_log_file="${log_dir}/$(get_log_filename "user-service")"
        start_service "用户服务" \
            "java -jar ${dist_dir}/user-service-1.0.0.jar \
            --spring.profiles.active=${user_service_profile} \
            --server.port=${user_service_port} \
            --logging.file.path=${log_dir} \
            --logging.file.name=${user_log_file} \
            --logging.config= \
            --logging.file.max-size=100MB \
            --logging.file.max-history=30" \
            "${user_log_file}" \
            ${user_service_port} \
            ${user_service_path} \
            "java -jar ${dist_dir}/user-service-1.0.0.jar"
    else
        print_warn "用户服务jar包不存在，跳过启动"
    fi
    
    # 启动API网关
    if [ -f "${dist_dir}/api-gateway/api-gateway-1.0.0.jar" ]; then
        local api_gateway_log_file="${log_dir}/$(get_log_filename "api-gateway")"
        start_service "API网关" \
            "java -jar ${dist_dir}/api-gateway/api-gateway-1.0.0.jar \
            --spring.profiles.active=${api_gateway_profile} \
            --server.port=${api_gateway_port} \
            --spring.config.location=${dist_dir}/api-gateway/ \
            --logging.file.path=${log_dir} \
            --logging.file.name=${api_gateway_log_file} \
            --logging.config= \
            --logging.file.max-size=100MB \
            --logging.file.max-history=30" \
            "${api_gateway_log_file}" \
            ${api_gateway_port} \
            ${api_gateway_path} \
            "java -jar ${dist_dir}/api-gateway/api-gateway-1.0.0.jar"
    else
        print_warn "API网关jar包不存在，跳过启动"
    fi
    
    cd "${PROJECT_ROOT}"
}

# 启动Nacos服务
start_nacos() {
    print_info "正在启动Nacos服务..."
    cd "${PROJECT_ROOT}"
    
    # 读取Nacos配置
    local nacos_version="2.5.1"
    local nacos_port=$(get_config '.ports.nacos' '8848')
    local nacos_mode=$(get_config '.nacos.mode' 'standalone')
    local nacos_auth_enabled=$(get_config '.nacos.auth.enabled' 'true')
    local nacos_auth_token=$(get_config '.nacos.auth.token' '')
    local nacos_auth_identity_key=$(get_config '.nacos.auth.identity_key' '')
    local nacos_auth_identity_value=$(get_config '.nacos.auth.identity_value' '')
    local log_dir="${PROJECT_ROOT}/$(get_config '.logging.dir' 'dist/logs')"
    local dist_dir="${PROJECT_ROOT}/dist"
    local nacos_home="${dist_dir}/nacos"
    
    # 创建日志目录
    mkdir -p "${log_dir}/nacos"
    
    # 处理已存在的 derby.log 文件
    if [ -f "${nacos_home}/derby.log" ]; then
        print_info "移动已存在的 derby.log 文件到日志目录..."
        mv "${nacos_home}/derby.log" "${log_dir}/nacos/derby.log"
    fi
    
    # 处理项目根目录下的 derby.log 文件
    if [ -f "${PROJECT_ROOT}/derby.log" ]; then
        print_info "移动项目根目录下的 derby.log 文件到日志目录..."
        mv "${PROJECT_ROOT}/derby.log" "${log_dir}/nacos/derby.log"
    fi
    
    # 配置JVM参数，添加内存限制和Derby日志路径
    local jvm_options=$(get_config '.nacos.jvm.options' '-Xms512m -Xmx512m -Xmn256m')
    echo "JAVA_OPT=\"${jvm_options} \
        -Dderby.stream.error.file=${log_dir}/nacos/derby.log \
        -Dnacos.logging.path=${log_dir}/nacos \
        --add-opens java.base/java.lang=ALL-UNNAMED \
        -Dnacos.remote.client.grpc.threadpool.max.size=20\"" > "${nacos_home}/bin/custom-jvm.properties"
    
    # 设置JAVA_HOME（如果配置文件中指定了）
    local java_home=$(get_config '.nacos.jvm.java_home' '')
    if [ -n "$java_home" ]; then
        export JAVA_HOME="$java_home"
    fi
    
    # 启动Nacos
    print_info "正在启动Nacos服务..."
    cd "${nacos_home}"
    nohup bash "bin/startup.sh" -m ${nacos_mode} -p ${nacos_port} > "${log_dir}/nacos/startup.log" 2>&1 &
    
    # 等待Nacos启动
    if wait_for_service "Nacos" "${nacos_port}" "$!"; then
        echo "控制台地址: http://127.0.0.1:${nacos_port}/nacos (仅限本地访问)"
        echo "用户名/密码: nacos/nacos"
        echo "日志位置: ${log_dir}/nacos"
        cd "${PROJECT_ROOT}"
    else
        cd "${PROJECT_ROOT}"
        exit 1
    fi
}

start_knowledge_rag_agent() {
    print_info "启动 knowledge_rag_agent..."
    local log_dir="${PROJECT_ROOT}/dist/logs"
    mkdir -p "$log_dir"
    local rag_port=$(get_config '.ports.knowledge_rag_agent' '8002')
    local main_path="${PROJECT_ROOT}/dist/agents/knowledge_rag_agent/src/main.py"
    if [ ! -f "$main_path" ]; then
        print_error "main.py 未找到: $main_path"
        return 1
    fi
    source "${PROJECT_ROOT}/dist/penv/bin/activate"
    local uvicorn_cmd="uvicorn dist.agents.knowledge_rag_agent.src.main:app --host 0.0.0.0 --port ${rag_port} --reload"
    print_info "实际启动命令: $uvicorn_cmd"
    nohup $uvicorn_cmd > "$log_dir/knowledge_rag_agent.log" 2>&1 &
    deactivate
    print_info "knowledge_rag_agent 启动完成，日志：$log_dir/knowledge_rag_agent.log"
    cd "${PROJECT_ROOT}"
}

start_embed_serves() {
    print_info "启动 embed_serves..."
    local log_dir="${PROJECT_ROOT}/dist/logs"
    mkdir -p "$log_dir"
    local embed_port=$(get_config '.ports.embed_serves' '8003')
    local main_path="${PROJECT_ROOT}/dist/services/embed_serves/main.py"
    if [ ! -f "$main_path" ]; then
        print_error "main.py 未找到: $main_path"
        return 1
    fi
    source "${PROJECT_ROOT}/dist/penv/bin/activate"
    local uvicorn_cmd="uvicorn dist.services.embed_serves.main:app --host 0.0.0.0 --port ${embed_port} --reload"
    print_info "实际启动命令: $uvicorn_cmd"
    nohup $uvicorn_cmd > "$log_dir/embed_serves.log" 2>&1 &
    deactivate
    print_info "embed_serves 启动完成，日志: $log_dir/embed_serves.log"
    cd "${PROJECT_ROOT}"
}

# 主函数
main() {
    cd "${PROJECT_ROOT}"
    
    # 检查配置文件是否存在
    if [ ! -f "$CONFIG_FILE" ]; then
        print_error "配置文件不存在: $CONFIG_FILE"
        exit 1
    fi
    
    print_info "开始环境检查..."
    check_java
    check_python
    check_node
    
    print_info "开始启动所有服务..."
    echo "========================================"
    
    local rag_port=$(get_config '.ports.knowledge_rag_agent' '8002')
    local embed_port=$(get_config '.ports.embed_serves' '8003')
    local nacos_port=$(get_config '.ports.nacos' '8848')
    local api_gateway_port=$(get_config '.ports.api_gateway' '8080')
    local log_dir="${PROJECT_ROOT}/$(get_config '.logging.dir' 'dist/logs')"
    start_nacos
    echo "========================================"
    build_and_start_java_services
    echo "========================================"
    start_knowledge_rag_agent
    echo "========================================"
    start_embed_serves
    echo "========================================"
    print_info "所有服务启动完成！"
    print_info "日志文件位置：${log_dir}/"
    echo "========================================"
    print_info "服务访问地址一览："
    echo "----------------------------------------"
    echo "Nacos:                http://localhost:${nacos_port}/nacos"
    echo "API网关:              http://localhost:${api_gateway_port}/api"
    echo "knowledge_rag_agent:  http://localhost:${rag_port}"
    echo "embed_serves:         http://localhost:${embed_port}"
    echo "----------------------------------------"
}


# 执行主函数
main

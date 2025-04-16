#!/bin/bash

# 设置颜色输出
GREEN='\033[0;32m'
YELLOW='\033[1;33m'
RED='\033[0;31m'
NC='\033[0m' # No Color

# 获取项目根目录
PROJECT_ROOT="$(cd "$(dirname "${BASH_SOURCE[0]}")/../.." && pwd)"
CONFIG_FILE="${PROJECT_ROOT}/tools/config/application.yaml"

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

# 检查Maven环境
check_maven() {
    if ! command -v mvn &> /dev/null; then
        print_error "Maven未安装，请安装Maven"
        exit 1
    fi
    
    mvn_version=$(mvn --version | awk 'NR==1{print $3}')
    print_info "检测到Maven版本: $mvn_version"
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

# 启动服务的通用函数
start_service() {
    local service_name=$1
    local command=$2
    local log_file=$3
    local port=$4
    local context_path=$5
    
    print_info "正在启动 ${service_name}..."
    eval "nohup ${command} > ${log_file} 2>&1 &"
    local pid=$!
    
    # 等待服务启动
    local count=0
    local max_attempts=30
    while ! nc -z localhost ${port} && [ $count -lt $max_attempts ]; do
        sleep 2
        ((count++))
        echo -n "."
    done
    echo ""
    
    if nc -z localhost ${port}; then
        print_info "${service_name} 启动成功！(PID: ${pid})"
        if [ -n "${context_path}" ]; then
            echo "访问地址: http://localhost:${port}${context_path}"
        else
            echo "访问地址: http://localhost:${port}"
        fi
        return 0
    else
        print_error "${service_name} 启动失败！"
        return 1
    fi
}

# 构建并启动Java服务
build_and_start_java_services() {
    print_info "正在构建Java服务..."
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
    
    # 构建父项目
    mvn clean install -DskipTests
    
    # 创建日志目录
    local log_dir=$(get_config '.logging.dir' 'logs')
    mkdir -p ${log_dir}
    echo "日志目录: ${log_dir}"
    # 启动认证服务
    cd services/auth-service
    start_service "认证服务" \
        "SPRING_PROFILES_ACTIVE=${auth_service_profile} SERVER_PORT=${auth_service_port} mvn spring-boot:run" \
        "${PROJECT_ROOT}/${log_dir}/auth-service.log" \
        ${auth_service_port} \
        ${auth_service_path}
    cd "${PROJECT_ROOT}"
    
    # 启动用户服务
    cd services/user-service
    start_service "用户服务" \
        "SPRING_PROFILES_ACTIVE=${user_service_profile} SERVER_PORT=${user_service_port} mvn spring-boot:run" \
        "${PROJECT_ROOT}/${log_dir}/user-service.log" \
        ${user_service_port} \
        ${user_service_path}
    cd "${PROJECT_ROOT}"
    
    # 启动Java智能体服务
    cd agents/java-agent
    start_service "Java智能体" \
        "SPRING_PROFILES_ACTIVE=${java_agent_profile} SERVER_PORT=${java_agent_port} mvn spring-boot:run" \
        "${PROJECT_ROOT}/${log_dir}/java-agent.log" \
        ${java_agent_port} \
        ${java_agent_path}
    cd "${PROJECT_ROOT}"
    
    # 最后启动API网关
    cd services/api-gateway
    start_service "API网关" \
        "SPRING_PROFILES_ACTIVE=${api_gateway_profile} SERVER_PORT=${api_gateway_port} mvn spring-boot:run" \
        "${PROJECT_ROOT}/${log_dir}/api-gateway.log" \
        ${api_gateway_port} \
        ${api_gateway_path}
    cd "${PROJECT_ROOT}"
}

# 构建并启动Python服务
build_and_start_python_services() {
    print_info "正在设置Python环境..."
    cd "${PROJECT_ROOT}"
    
    # 读取Python服务配置
    local python_agent_port=$(get_config '.ports.python_agent' '8084')
    local python_env=$(get_config '.services.python_agent.env' 'development')
    local python_agent_path=$(get_config '.services.python_agent.context_path' '/python-agent')
    local log_dir=$(get_config '.logging.dir' 'logs')
    
    # 创建并激活虚拟环境
    cd agents/python-agent
    python3 -m venv venv
    source venv/bin/activate
    
    # 安装依赖
    pip install -r requirements.txt
    
    # 启动Python服务
    start_service "Python智能体" \
        "PORT=${python_agent_port} PYTHON_ENV=${python_env} python app.py" \
        "${PROJECT_ROOT}/${log_dir}/python-agent.log" \
        ${python_agent_port} \
        ${python_agent_path}
    
    deactivate
    cd "${PROJECT_ROOT}"
}

# 构建并启动前端应用
build_and_start_frontend() {
    print_info "正在构建前端应用..."
    cd "${PROJECT_ROOT}"
    
    # 读取前端配置
    local frontend_port=$(get_config '.ports.frontend' '3000')
    local log_dir=$(get_config '.logging.dir' 'logs')
    
    # 安装依赖
    npm install
    
    # 构建并启动前端应用
    cd apps/web
    npm install
    npm run build
    
    start_service "前端应用" \
        "PORT=${frontend_port} npm run start" \
        "${PROJECT_ROOT}/${log_dir}/frontend.log" \
        ${frontend_port}
    
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
    local log_dir=$(get_config '.logging.dir' 'logs')
    local deploy_dir="${PROJECT_ROOT}/deploy"
    local nacos_home="${deploy_dir}/nacos"
    
    # 创建部署目录
    mkdir -p "${deploy_dir}"
    
    # 检查是否已下载
    if [ ! -d "${nacos_home}" ]; then
        print_info "正在下载 Nacos ${nacos_version}..."
        local download_url="https://github.com/alibaba/nacos/releases/download/${nacos_version}/nacos-server-${nacos_version}.tar.gz"
        local temp_file="${deploy_dir}/nacos.tar.gz"
        
        # 下载 Nacos
        if ! curl -L "${download_url}" -o "${temp_file}"; then
            print_error "Nacos 下载失败！"
            exit 1
        fi
        
        # 解压
        cd "${deploy_dir}"
        if ! tar -xzf nacos.tar.gz; then
            print_error "Nacos 解压失败！"
            exit 1
        fi
        rm -f nacos.tar.gz
        cd "${PROJECT_ROOT}"
    else
        print_info "使用已存在的 Nacos 安装..."
    fi
    
    # 配置Nacos
    local conf_file="${nacos_home}/conf/application.properties"
    print_info "正在配置 Nacos (${conf_file})"
    
    # 确保配置文件存在
    if [ ! -f "${conf_file}" ]; then
        print_info "配置文件不存在，创建新文件..."
        touch "${conf_file}"
    fi
    
    print_info "更新 Nacos 配置..."
    
    # 生成base64编码的密钥（至少32字节）
    local secret_key=$(echo "nacos-auth-key-super-long-security-key-2024" | base64)
    
    # 逐个更新配置项
    print_info "设置 server.ip..."
    sed -i '/^server.ip=/c server.ip=127.0.0.1' "${conf_file}"
    
    print_info "设置 auth.system.type..."
    sed -i '/^nacos.core.auth.system.type=/c nacos.core.auth.system.type=nacos' "${conf_file}"
    
    print_info "设置 auth.enabled..."
    sed -i '/^nacos.core.auth.enabled=/c nacos.core.auth.enabled='"${nacos_auth_enabled}" "${conf_file}"
    
    print_info "设置 auth.server.identity.key..."
    sed -i '/^nacos.core.auth.server.identity.key=/c nacos.core.auth.server.identity.key='"${nacos_auth_identity_key}" "${conf_file}"
    
    print_info "设置 auth.server.identity.value..."
    sed -i '/^nacos.core.auth.server.identity.value=/c nacos.core.auth.server.identity.value='"${nacos_auth_identity_value}" "${conf_file}"
    
    print_info "设置 auth.plugin.nacos.token..."
    sed -i '/^nacos.core.auth.plugin.nacos.token=/c nacos.core.auth.plugin.nacos.token='"${nacos_auth_token}" "${conf_file}"
    
    print_info "设置 JWT 密钥..."
    sed -i '/^nacos.core.auth.plugin.nacos.token.secret.key=/c nacos.core.auth.plugin.nacos.token.secret.key='"${secret_key}" "${conf_file}"
    
    # 检查配置项是否存在，如果不存在则添加
    print_info "检查并补充缺失的配置项..."
    for config in \
        "server.ip=127.0.0.1" \
        "nacos.core.auth.system.type=nacos" \
        "nacos.core.auth.enabled=${nacos_auth_enabled}" \
        "nacos.core.auth.server.identity.key=${nacos_auth_identity_key}" \
        "nacos.core.auth.server.identity.value=${nacos_auth_identity_value}" \
        "nacos.core.auth.plugin.nacos.token=${nacos_auth_token}" \
        "nacos.core.auth.plugin.nacos.token.secret.key=${secret_key}"
    do
        key=$(echo "${config}" | cut -d= -f1)
        if ! grep -q "^${key}=" "${conf_file}"; then
            echo "${config}" >> "${conf_file}"
            print_info "添加缺失配置: ${config}"
        fi
    done
    
    # 显示最终的配置文件内容
    print_info "当前配置文件内容："
    
    # 配置JVM参数，添加内存限制
    local jvm_options=$(get_config '.nacos.jvm.options' '-Xms512m -Xmx512m -Xmn256m')
    echo "JAVA_OPT=\"${jvm_options} --add-opens java.base/java.lang=ALL-UNNAMED -Dnacos.remote.client.grpc.threadpool.max.size=20\"" > "${nacos_home}/bin/custom-jvm.properties"
    
    # 设置JAVA_HOME（如果配置文件中指定了）
    local java_home=$(get_config '.nacos.jvm.java_home' '')
    if [ -n "$java_home" ]; then
        export JAVA_HOME="$java_home"
    fi
    
    # 启动Nacos
    print_info "正在启动Nacos服务..."
    nohup bash "${nacos_home}/bin/startup.sh" -m ${nacos_mode} -p ${nacos_port} > "${nacos_home}/logs/startup.log" 2>&1 &
    
    # 等待Nacos启动
    print_info "等待Nacos启动..."
    local count=0
    local max_attempts=30
    while ! nc -z localhost ${nacos_port} && [ $count -lt $max_attempts ]; do
        sleep 2
        ((count++))
        echo -n "."
    done
    echo ""
    
    if nc -z localhost ${nacos_port}; then
        print_info "Nacos启动成功！"
        echo "控制台地址: http://127.0.0.1:${nacos_port}/nacos (仅限本地访问)"
        echo "用户名/密码: nacos/nacos"
        echo "日志位置: ${nacos_home}/logs"
        cd "${PROJECT_ROOT}"
    else
        print_error "Nacos启动失败！"
        cd "${PROJECT_ROOT}"
        exit 1
    fi
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
    check_maven
    check_python
    check_node
    
    print_info "开始启动所有服务..."
    echo "========================================"
    
    # 启动Nacos
    start_nacos
    echo "========================================"
    
    # # 启动Java服务
    # build_and_start_java_services
    # echo "========================================"
    
    # # 启动Python服务
    # build_and_start_python_services
    # echo "========================================"
    
    # # 启动前端应用
    # build_and_start_frontend
    # echo "========================================"
    
    print_info "所有服务启动完成！"
    print_info "日志文件位置：${PROJECT_ROOT}/$(get_config '.logging.dir' 'logs')/"
    
    # 显示进程信息
    echo "========================================"
    print_info "当前运行的服务进程："
    ps aux | grep -E "spring-boot:run|python app.py|next start|nacos" | grep -v grep
    echo "========================================"
}

# 创建停止脚本
create_stop_script() {
    cat > "${PROJECT_ROOT}/tools/scripts/stop-all.sh" << 'EOF'
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
EOF

    chmod +x "${PROJECT_ROOT}/tools/scripts/stop-all.sh"
}

# 执行主函数
main
create_stop_script 
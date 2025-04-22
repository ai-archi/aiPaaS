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
            print_error "未知参数: $1"
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

# 检查yq是否安装
if ! command -v yq &> /dev/null; then
    print_info "正在安装yq..." "force"
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

# 构建Java服务
build_java_services() {
    print_info "正在构建Java服务..." "force"
    cd "${PROJECT_ROOT}"
    
    # 创建dist目录
    mkdir -p dist/services
    debug_info "创建目录: ${PROJECT_ROOT}/dist/services"
    
    # 构建父项目
    print_info "构建父项目..." "force"
    cd "${PROJECT_ROOT}/services"
    debug_info "切换到目录: ${PROJECT_ROOT}/services"
    
    if [ "$DEBUG_MODE" = true ]; then
        mvn clean install -DskipTests
    else
        mvn clean install -DskipTests -q
    fi
    
    if [ $? -ne 0 ]; then
        print_error "父项目构建失败"
        exit 1
    fi
    
    # 构建API网关
    print_info "构建API网关..." "force"
    cd "${PROJECT_ROOT}/services/api-gateway"
    debug_info "切换到目录: ${PROJECT_ROOT}/services/api-gateway"
    
    if [ "$DEBUG_MODE" = true ]; then
        mvn clean package -DskipTests
    else
        mvn clean package -DskipTests -q
    fi
    
    if [ $? -eq 0 ]; then
        cp target/api-gateway-1.0.0.jar "${PROJECT_ROOT}/dist/services/"
        debug_info "复制文件: target/api-gateway-1.0.0.jar -> ${PROJECT_ROOT}/dist/services/"
        print_info "API网关构建成功" "force"
    else
        print_error "API网关构建失败"
        exit 1
    fi
    cd "${PROJECT_ROOT}"
}

# 构建Python服务
build_python_services() {
    print_info "正在构建Python服务..." "force"
    cd "${PROJECT_ROOT}"
    
    # 清理并创建dist目录
    print_info "清理Python服务构建目录..." "force"
    rm -rf dist/agents/python-agent dist/agents/task-agent dist/penv
    debug_info "已删除旧的构建目录"
    
    mkdir -p dist/agents/python-agent
    mkdir -p dist/agents/task-agent
    mkdir -p dist/penv
    debug_info "创建目录结构:"
    debug_info "- ${PROJECT_ROOT}/dist/agents/python-agent"
    debug_info "- ${PROJECT_ROOT}/dist/agents/task-agent"
    debug_info "- ${PROJECT_ROOT}/dist/penv"
    
    # 创建全局Python虚拟环境
    print_info "创建全局Python虚拟环境..." "force"
    python3 -m venv dist/penv
    source dist/penv/bin/activate
    debug_info "Python虚拟环境已激活: $(which python)"
    
    # 升级pip
    print_info "升级pip..." "force"
    if [ "$DEBUG_MODE" = true ]; then
        python -m pip install --upgrade pip
    else
        python -m pip install --upgrade pip -q
    fi
    
    # 安装全局依赖
    print_info "安装全局Python依赖..." "force"
    if [ -f "agents/task-agent/requirements.txt" ]; then
        debug_info "安装依赖文件: agents/task-agent/requirements.txt"
        if [ "$DEBUG_MODE" = true ]; then
            pip install --no-cache-dir -r agents/task-agent/requirements.txt
        else
            pip install --no-cache-dir -r agents/task-agent/requirements.txt -q
        fi
        if [ $? -ne 0 ]; then
            print_error "全局Python依赖安装失败"
            deactivate
            exit 1
        fi
        debug_info "依赖安装成功，安装位置: ${VIRTUAL_ENV}"
    fi
    
    # 构建Python智能体
    print_info "构建Python智能体..." "force"
    cd agents/python-agent
    debug_info "切换到目录: ${PROJECT_ROOT}/agents/python-agent"
    
    # 安装Python智能体特定依赖
    if [ -f "requirements.txt" ]; then
        debug_info "安装依赖文件: requirements.txt"
        if [ "$DEBUG_MODE" = true ]; then
            pip install --no-cache-dir -r requirements.txt
        else
            pip install --no-cache-dir -r requirements.txt -q
        fi
        if [ $? -eq 0 ]; then
            print_info "Python智能体依赖安装成功" "force"
            # 复制必要文件到dist目录
            cp -r app.py requirements.txt "${PROJECT_ROOT}/dist/agents/python-agent/"
            debug_info "复制文件到: ${PROJECT_ROOT}/dist/agents/python-agent/"
        else
            print_error "Python智能体依赖安装失败"
            deactivate
            exit 1
        fi
    fi
    
    cd "${PROJECT_ROOT}"
    debug_info "切换回项目根目录"

    # 构建Task智能体
    print_info "构建Task智能体..." "force"
    cd agents/task-agent
    debug_info "切换到目录: ${PROJECT_ROOT}/agents/task-agent"
    
    # 复制必要文件到dist目录
    debug_info "复制Task智能体文件到dist目录"
    cp -r run.py requirements.txt .env "${PROJECT_ROOT}/dist/agents/task-agent/"
    debug_info "复制基础文件到: ${PROJECT_ROOT}/dist/agents/task-agent/"
    
    # 复制应用代码到dist目录
    if [ -d "app" ]; then
        cp -r app "${PROJECT_ROOT}/dist/agents/task-agent/"
        debug_info "复制app目录到: ${PROJECT_ROOT}/dist/agents/task-agent/"
    fi
    if [ -d "core" ]; then
        cp -r core "${PROJECT_ROOT}/dist/agents/task-agent/"
        debug_info "复制core目录到: ${PROJECT_ROOT}/dist/agents/task-agent/"
    fi
    if [ -d "utils" ]; then
        cp -r utils "${PROJECT_ROOT}/dist/agents/task-agent/"
        debug_info "复制utils目录到: ${PROJECT_ROOT}/dist/agents/task-agent/"
    fi
    
    cd "${PROJECT_ROOT}"
    debug_info "切换回项目根目录"
    
    # 验证虚拟环境中的依赖安装
    print_info "验证Python依赖安装..." "force"
    if [ "$DEBUG_MODE" = true ]; then
        pip list
    fi
    
    deactivate
    debug_info "Python虚拟环境已退出"
}

# 构建前端应用
build_frontend() {
    print_info "正在构建前端应用..."
    cd "${PROJECT_ROOT}"
    
    # 创建dist目录
    mkdir -p dist/apps/web
    
    # 安装根目录依赖
    npm install
    if [ $? -ne 0 ]; then
        print_error "前端根目录依赖安装失败"
        exit 1
    fi
    
    # 构建前端应用
    cd apps/web
    npm install
    if [ $? -ne 0 ]; then
        print_error "前端应用依赖安装失败"
        exit 1
    fi
    
    npm run build
    if [ $? -eq 0 ]; then
        # 复制构建产物到dist目录
        cp -r .next package.json package-lock.json "${PROJECT_ROOT}/dist/apps/web/"
        print_info "前端应用构建成功"
    else
        print_error "前端应用构建失败"
        exit 1
    fi
    
    cd "${PROJECT_ROOT}"
}

# 通用下载函数
download_file() {
    local url=$1
    local output_file=$2
    local description=$3
    
    print_info "正在下载 ${description}..." "force"
    if ! curl -L "${url}" -o "${output_file}"; then
        print_error "${description} 下载失败！"
        return 1
    fi
    return 0
}

# 构建Nacos服务
build_nacos() {
    print_info "正在构建Nacos服务..." "force"
    cd "${PROJECT_ROOT}"
    
    # 读取Nacos配置
    local nacos_version=$(get_config '.nacos.version' '2.5.1')
    local dist_dir="${PROJECT_ROOT}/dist"
    local nacos_home="${dist_dir}/nacos"
    
    # 创建部署目录
    mkdir -p "${dist_dir}"
    debug_info "创建目录: ${dist_dir}"
    
    # 检查是否已下载
    if [ ! -d "${nacos_home}" ]; then
        print_info "下载并安装Nacos ${nacos_version}..." "force"
        local download_url="https://github.com/alibaba/nacos/releases/download/${nacos_version}/nacos-server-${nacos_version}.tar.gz"
        local temp_file="${dist_dir}/nacos.tar.gz"
        
        # 下载 Nacos
        if ! download_file "${download_url}" "${temp_file}" "Nacos ${nacos_version}"; then
            print_error "Nacos下载失败"
            exit 1
        fi
        
        # 解压
        print_info "解压Nacos..." "force"
        cd "${dist_dir}"
        if ! tar -xzf nacos.tar.gz; then
            print_error "Nacos解压失败"
            rm -f nacos.tar.gz
            exit 1
        fi
        rm -f nacos.tar.gz
        debug_info "Nacos解压完成，清理临时文件"
        
        print_info "Nacos安装完成" "force"
    else
        print_info "使用已存在的Nacos安装..." "force"
        debug_info "Nacos目录: ${nacos_home}"
    fi
    
    # 创建日志目录
    local log_dir="${PROJECT_ROOT}/$(get_config '.logging.dir' 'dist/logs')"
    mkdir -p "${log_dir}/nacos"
    debug_info "创建Nacos日志目录: ${log_dir}/nacos"
    
    cd "${PROJECT_ROOT}"
}

# 主函数
main() {
    cd "${PROJECT_ROOT}"
    
    if [ "$DEBUG_MODE" = true ]; then
        print_info "调试模式已启用" "force"
        print_info "项目根目录: ${PROJECT_ROOT}" "force"
        print_info "配置文件: ${CONFIG_FILE}" "force"
    fi
    
    # 检查配置文件是否存在
    if [ ! -f "$CONFIG_FILE" ]; then
        print_error "配置文件不存在: $CONFIG_FILE"
        exit 1
    fi
    
    print_info "开始环境检查..." "force"
    check_java
    check_maven
    check_python
    check_node
    
    print_info "开始构建所有服务..." "force"
    echo "========================================"
    
    # 构建Nacos服务
    build_nacos
    echo "========================================"
    
    # 构建Java服务
    build_java_services
    echo "========================================"
    
    # 构建Python服务
    build_python_services
    echo "========================================"
    
    # # 构建前端应用
    # build_frontend
    # echo "========================================"
    
    print_info "所有服务构建完成！" "force"
    print_info "构建产物位置：${PROJECT_ROOT}/dist/" "force"
    
    # 显示构建产物
    if [ "$DEBUG_MODE" = true ]; then
        echo "========================================"
        print_info "构建产物列表：" "force"
        ls -R "${PROJECT_ROOT}/dist/"
        echo "========================================"
    fi
}

# 执行主函数
main 
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


# 构建Java服务
build_java_services() {
    print_info "正在构建Java服务..."
    cd "${PROJECT_ROOT}"
    
    # 创建dist目录
    mkdir -p dist/services
    
    # 构建父项目
    print_info "构建父项目..."
    cd "${PROJECT_ROOT}/services"
    mvn clean install -DskipTests
    if [ $? -ne 0 ]; then
        print_error "父项目构建失败"
        exit 1
    fi
    
    # 构建API网关
    print_info "构建API网关..."
    cd "${PROJECT_ROOT}/services/api-gateway"
    mvn clean package -DskipTests
    if [ $? -eq 0 ]; then
        cp target/api-gateway-1.0.0.jar "${PROJECT_ROOT}/dist/services/"
        print_info "API网关构建成功"
    else
        print_error "API网关构建失败"
        exit 1
    fi
    cd "${PROJECT_ROOT}"
    
    # # 构建认证服务
    # print_info "构建认证服务..."
    # cd "${PROJECT_ROOT}/services/auth-service"
    # mvn clean package -DskipTests
    # if [ $? -eq 0 ]; then
    #     cp target/auth-service-1.0.0.jar "${PROJECT_ROOT}/dist/services/"
    #     print_info "认证服务构建成功"
    # else
    #     print_error "认证服务构建失败"
    #     exit 1
    # fi
    # cd "${PROJECT_ROOT}"
    
    # # 构建用户服务
    # print_info "构建用户服务..."
    # cd "${PROJECT_ROOT}/services/user-service"
    # mvn clean package -DskipTests
    # if [ $? -eq 0 ]; then
    #     cp target/user-service-1.0.0.jar "${PROJECT_ROOT}/dist/services/"
    #     print_info "用户服务构建成功"
    # else
    #     print_error "用户服务构建失败"
    #     exit 1
    # fi
    cd "${PROJECT_ROOT}"
}

# 构建Python服务
build_python_services() {
    print_info "正在构建Python服务..."
    cd "${PROJECT_ROOT}"
    
    # 创建dist目录
    mkdir -p dist/agents
    
    # 构建Python智能体
    cd agents/python-agent
    
    # 创建并激活虚拟环境
    python3 -m venv venv
    source venv/bin/activate
    
    # 安装依赖
    pip install -r requirements.txt
    if [ $? -eq 0 ]; then
        print_info "Python智能体依赖安装成功"
        # 复制必要文件到dist目录
        cp -r app.py requirements.txt "${PROJECT_ROOT}/dist/agents/python-agent/"
    else
        print_error "Python智能体依赖安装失败"
        exit 1
    fi
    
    deactivate
    cd "${PROJECT_ROOT}"
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
    
    
    print_info "开始构建所有服务..."
    echo "========================================"
    
    # 构建Java服务
    build_java_services
    echo "========================================"
    
    # # 构建Python服务
    # build_python_services
    # echo "========================================"
    
    # # 构建前端应用
    # build_frontend
    # echo "========================================"
    
    print_info "所有服务构建完成！"
    print_info "构建产物位置：${PROJECT_ROOT}/dist/"
    
    # 显示构建产物
    echo "========================================"
    print_info "构建产物列表："
    ls -R "${PROJECT_ROOT}/dist/"
    echo "========================================"
}

# 执行主函数
main 
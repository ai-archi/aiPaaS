#!/bin/bash

# 设置颜色输出
GREEN='\033[0;32m'
YELLOW='\033[1;33m'
RED='\033[0;31m'
NC='\033[0m' # No Color

# 默认配置
DEBUG_MODE=false
CLEAN_MODE=false

# 设置环境变量
export PATH="/usr/local/bin:/usr/bin:/bin:/usr/sbin:/sbin:$PATH"
export LANG=en_US.UTF-8
export LC_ALL=en_US.UTF-8

# 检查并安装yq
install_yq() {
    if ! command -v yq &> /dev/null; then
        print_info "正在安装yq..." "force"
        if [[ "$OSTYPE" == "darwin"* ]]; then
            # 对于 macOS，尝试直接下载二进制文件
            local tmp_dir=$(mktemp -d)
            if curl -L "https://github.com/mikefarah/yq/releases/latest/download/yq_darwin_amd64" -o "${tmp_dir}/yq"; then
                chmod +x "${tmp_dir}/yq"
                if [ -d "/usr/local/bin" ]; then
                    sudo mv "${tmp_dir}/yq" /usr/local/bin/yq
                else
                    mkdir -p ~/bin
                    mv "${tmp_dir}/yq" ~/bin/yq
                    export PATH="$HOME/bin:$PATH"
                fi
                rm -rf "${tmp_dir}"
                print_info "yq 安装成功"
            else
                print_warn "无法下载 yq，将使用默认配置继续"
            fi
        else
            # 对于 Linux，尝试直接下载二进制文件
            local tmp_dir=$(mktemp -d)
            if curl -L "https://github.com/mikefarah/yq/releases/latest/download/yq_linux_amd64" -o "${tmp_dir}/yq"; then
                chmod +x "${tmp_dir}/yq"
                if [ -d "/usr/local/bin" ]; then
                    sudo mv "${tmp_dir}/yq" /usr/local/bin/yq
                else
                    mkdir -p ~/bin
                    mv "${tmp_dir}/yq" ~/bin/yq
                    export PATH="$HOME/bin:$PATH"
                fi
                rm -rf "${tmp_dir}"
                print_info "yq 安装成功"
            else
                print_warn "无法下载 yq，将使用默认配置继续"
            fi
        fi
    fi
}

# 检查必要的命令
check_required_commands() {
    local missing_commands=()
    
    # 检查基本命令
    for cmd in curl mkdir rm cp mv chmod python3; do
        if ! command -v $cmd &> /dev/null; then
            missing_commands+=($cmd)
        fi
    done
    
    # 如果有缺失的命令，输出错误信息并退出
    if [ ${#missing_commands[@]} -ne 0 ]; then
        print_error "以下必要命令未找到: ${missing_commands[*]}"
        print_error "请安装这些命令后重试"
        exit 1
    fi
}

# 解析命令行参数
while [[ $# -gt 0 ]]; do
    case $1 in
        --debug)
            DEBUG_MODE=true
            shift
            ;;
        --clean)
            CLEAN_MODE=true
            shift
            ;;
        *)
            print_error "未知参数: $1"
            echo "用法: $0 [--debug] [--clean]"
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

# 读取配置文件函数
get_config() {
    local key=$1
    local default_value=$2
    
    if [ ! -f "$CONFIG_FILE" ]; then
        echo "$default_value"
        return
    fi
    
    if ! command -v yq &> /dev/null; then
        echo "$default_value"
        return
    fi
    
    value=$(yq eval "$key" "$CONFIG_FILE" 2>/dev/null || echo "$default_value")
    if [ "$value" = "null" ] || [ -z "$value" ]; then
        echo "$default_value"
    else
        echo "$value"
    fi
}

# 检查Java版本
check_java_version() {
    local required_version=$1
    local java_version
    
    # 获取完整的Java版本信息
    if java_version=$(java -version 2>&1 | awk -F '"' '/version/ {print $2}'); then
        # 提取主版本号
        local major_version=$(echo "$java_version" | cut -d'.' -f1)
        
        if [ -z "$major_version" ] || [ "$major_version" -lt "$required_version" ]; then
            print_error "需要JDK ${required_version}或更高版本"
            print_error "当前Java版本: $java_version"
            return 1
        fi
        
        # 检查是否为预览版本
        if java -version 2>&1 | grep -q "preview"; then
            print_warn "检测到Java预览版本，可能会影响某些功能的使用"
        fi
        
        print_info "检测到Java版本: $java_version"
        return 0
    else
        print_error "无法获取Java版本信息"
        return 1
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

# 检查Node.js环境（可选）
check_node() {
    if ! command -v node &> /dev/null; then
        print_warn "Node.js未安装，跳过前端构建"
        return 1
    fi
    
    node_version=$(node --version)
    print_info "检测到Node.js版本: $node_version"
    return 0
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
    
    # 设置Maven编译选项
    export MAVEN_OPTS="-Xmx1024m"
    
    # 构建命令，禁用预览特性
    local mvn_cmd="mvn clean install -DskipTests"
    if [ "$DEBUG_MODE" = true ]; then
        mvn_cmd="$mvn_cmd"
    else
        mvn_cmd="$mvn_cmd -q"
    fi
    
    # 添加编译器参数
    mvn_cmd="$mvn_cmd -Dmaven.compiler.source=21 -Dmaven.compiler.target=21 -Dmaven.compiler.release=21"
    
    if ! eval "$mvn_cmd"; then
        print_error "父项目构建失败"
        return 1
    fi
    
    # 构建API网关
    print_info "构建API网关..." "force"
    cd "${PROJECT_ROOT}/services/api-gateway"
    debug_info "切换到目录: ${PROJECT_ROOT}/services/api-gateway"
    
    # 清理并创建 dist/services/api-gateway 目录
    rm -rf "${PROJECT_ROOT}/dist/services/api-gateway"
    mkdir -p "${PROJECT_ROOT}/dist/services/api-gateway"
    
    # 构建命令
    mvn_cmd="mvn clean package -DskipTests"
    if [ "$DEBUG_MODE" = true ]; then
        mvn_cmd="$mvn_cmd"
    else
        mvn_cmd="$mvn_cmd -q"
    fi
    
    # 添加编译器参数
    mvn_cmd="$mvn_cmd -Dmaven.compiler.source=21 -Dmaven.compiler.target=21 -Dmaven.compiler.release=21"
    
    if eval "$mvn_cmd"; then
        cp target/api-gateway-1.0.0.jar "${PROJECT_ROOT}/dist/services/api-gateway/api-gateway-1.0.0.jar"
        # 复制所有配置文件到 dist/services/api-gateway
        cp -r src/main/resources/application*.yml src/main/resources/application*.yaml src/main/resources/application.properties "${PROJECT_ROOT}/dist/services/api-gateway/" 2>/dev/null || true
        debug_info "复制文件: target/api-gateway-1.0.0.jar 及配置文件 -> ${PROJECT_ROOT}/dist/services/api-gateway/"
        print_info "API网关构建成功" "force"
    else
        print_error "API网关构建失败"
        return 1
    fi
    
    cd "${PROJECT_ROOT}"
    return 0
}

# 构建Python服务
build_python_services() {
    print_info "正在构建Python服务..." "force"
    cd "${PROJECT_ROOT}"
    
    # 创建虚拟环境目录（如果不存在）
    if [ ! -d "dist/penv" ] || [ "$CLEAN_MODE" = true ]; then
        if [ "$CLEAN_MODE" = true ]; then
            print_info "清理模式：删除旧的Python虚拟环境..." "force"
            rm -rf dist/penv
        fi
        
        print_info "创建新的Python虚拟环境..." "force"
        mkdir -p dist/penv
        python3 -m venv dist/penv
        
        # 激活虚拟环境并更新pip
        source dist/penv/bin/activate
        pip install --upgrade pip
        pip install wheel setuptools
        deactivate
    fi
    
    # 激活虚拟环境
    source dist/penv/bin/activate
    
    # 首先构建并安装 mf-nacos-service-registrar
    print_info "构建并安装 mf-nacos-service-registrar..." "force"
    build_mf_nacos_service_registrar || { deactivate; return 1; }
    
    # 然后构建其他Python服务
    build_knowledge_rag_agent || { deactivate; return 1; }
    build_embed_serve || { deactivate; return 1; }
    
    deactivate
    debug_info "Python虚拟环境已退出"
    cd "${PROJECT_ROOT}"
}

# 构建前端应用
build_frontend() {
    print_info "正在构建前端应用..."
    cd "${PROJECT_ROOT}"
    if [ ! -d "apps/web" ] || [ ! -f "apps/web/package.json" ]; then
        print_warn "apps/web 或 package.json 不存在，跳过前端构建"
        return 0
    fi
    mkdir -p dist/apps/web
    npm install
    cd apps/web
    npm install
    if grep -q '"build"' package.json; then
        npm run build
        if [ $? -eq 0 ]; then
            cp -r .next package.json package-lock.json "${PROJECT_ROOT}/dist/apps/web/"
            print_info "前端应用构建成功"
        else
            print_error "前端应用构建失败"
            exit 1
        fi
    else
        print_warn "package.json 未定义 build 脚本，跳过前端构建"
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

# 构建 mf-nacos-service-registrar
build_mf_nacos_service_registrar() {
    print_info "构建并本地发布 mf-nacos-service-registrar..." "force"
    cd "${PROJECT_ROOT}/libs/python/mf-nacos-service-registrar"
    
    # 虚拟环境已在父函数中激活
    python -m build
    pip install -e .
    
    cd "${PROJECT_ROOT}"
    return 0
}

# 构建 knowledge_rag_agent
build_knowledge_rag_agent() {
    print_info "构建 knowledge_rag_agent..." "force"
    local target_dir="${PROJECT_ROOT}/dist/agents/knowledge_rag_agent"
    local source_dir="${PROJECT_ROOT}/agents/knowledge_rag_agent"
    
    # 如果是清理模式，删除目标目录
    if [ "$CLEAN_MODE" = true ]; then
        print_info "清理模式：删除旧的构建目录..." "force"
        rm -rf "${target_dir}"
    fi
    
    # 创建目标目录
    mkdir -p "${target_dir}"
    
    cd "${source_dir}"
    
    # 检查并安装依赖（虚拟环境已在父函数中激活）
    if [ -f "requirements.txt" ]; then
        print_info "安装 knowledge_rag_agent 依赖..." "force"
        
        # 安装依赖（使用虚拟环境中的pip）
        if ! pip install -v -r requirements.txt; then
            print_error "依赖安装失败，请检查 requirements.txt 和上述错误信息"
            return 1
        fi
        
        # 验证关键依赖是否安装成功
        if ! python -c "import httpx" 2>/dev/null; then
            print_error "httpx 导入测试失败，依赖可能未正确安装"
            return 1
        fi
        
        print_info "依赖安装成功" "force"
    else
        print_error "requirements.txt 文件不存在"
        return 1
    fi
    
    # 同步源码和配置文件
    print_info "同步 knowledge_rag_agent 源码和配置..." "force"
    
    # 使用 rsync 进行增量同步，保持目录结构
    if command -v rsync &> /dev/null; then
        rsync -av --delete \
            --exclude '*.pyc' \
            --exclude '__pycache__' \
            --exclude '*.egg-info' \
            --exclude '.pytest_cache' \
            --exclude '.coverage' \
            --exclude 'htmlcov' \
            --exclude '*.log' \
            src/ "${target_dir}/src/"
    else
        # 如果没有 rsync，使用 cp 命令
        rm -rf "${target_dir}/src"
        cp -r src "${target_dir}/"
    fi
    
    # 复制其他必要文件
    for file in README.md pyproject.toml requirements.txt config.yaml; do
        if [ -f "$file" ]; then
            cp -f "$file" "${target_dir}/" 2>/dev/null || true
        fi
    done
    
    # 创建必要的数据目录
    mkdir -p "${target_dir}/data/"{vector_db,storage,temp}
    mkdir -p "${target_dir}/logs"
    
    cd "${PROJECT_ROOT}"
    return 0
}

# 构建 embed_serve
build_embed_serve() {
    print_info "构建 embed_serve..." "force"
    local target_dir="${PROJECT_ROOT}/dist/services/embed_serve"
    local source_dir="${PROJECT_ROOT}/services/embed_serve"
    
    # 如果是清理模式，删除目标目录
    if [ "$CLEAN_MODE" = true ]; then
        print_info "清理模式：删除旧的构建目录..." "force"
        rm -rf "${target_dir}"
    fi
    
    # 创建目标目录
    mkdir -p "${target_dir}"
    
    cd "${source_dir}"
    
    # 检查并安装依赖（虚拟环境已在父函数中激活）
    if [ -f "requirements.txt" ]; then
        print_info "安装 embed_serve 依赖..." "force"
        if ! pip install -v -r requirements.txt; then
            print_error "依赖安装失败"
            return 1
        fi
        print_info "依赖安装成功" "force"
    fi
    
    # 同步源码和配置文件
    print_info "同步 embed_serve 源码和配置..." "force"
    
    # 使用 rsync 进行增量同步，保持目录结构
    if command -v rsync &> /dev/null; then
        rsync -av --delete \
            --exclude '*.pyc' \
            --exclude '__pycache__' \
            --exclude '*.egg-info' \
            --exclude '.pytest_cache' \
            --exclude '.coverage' \
            --exclude 'htmlcov' \
            --exclude '*.log' \
            ./ "${target_dir}/"
    else
        # 如果没有 rsync，使用 cp 命令
        find "${target_dir}" -type f -delete
        find . -type f \( -name "*.py" -o -name "*.yaml" -o -name "*.yml" -o -name "*.json" \) -exec cp --parents {} "${target_dir}/" \;
    fi
    
    # 复制其他必要文件
    for file in README.md pyproject.toml requirements.txt; do
        if [ -f "$file" ]; then
            cp -f "$file" "${target_dir}/" 2>/dev/null || true
        fi
    done
    
    cd "${PROJECT_ROOT}"
    return 0
}

# 主函数
main() {
    cd "${PROJECT_ROOT}"
    
    # 检查必要的命令
    check_required_commands
    
    # 安装 yq（如果需要）
    install_yq
    
    # 检查配置文件是否存在
    if [ ! -f "$CONFIG_FILE" ]; then
        print_warn "配置文件不存在: $CONFIG_FILE，将使用默认配置"
    fi
    
    print_info "开始环境检查..." "force"
    check_java || { print_error "Java环境检查失败"; exit 1; }
    check_python || { print_error "Python环境检查失败"; exit 1; }
    
    # Node.js检查改为可选
    check_node
    
    print_info "开始构建所有服务..." "force"
    echo "========================================"
    
    # 构建Nacos服务
    build_nacos || { print_error "Nacos构建失败"; exit 1; }
    echo "========================================"
    
    # 构建Java服务
    build_java_services || { print_error "Java服务构建失败"; exit 1; }
    echo "========================================"
    
    # 构建Python服务（包含了mf-nacos-service-registrar的构建）
    build_python_services || { print_error "Python服务构建失败"; exit 1; }
    echo "========================================"
    
    # 构建前端（如果Node.js可用）
    if command -v node &> /dev/null; then
        build_frontend
        echo "========================================"
    fi
    
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
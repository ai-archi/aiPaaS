#!/bin/bash

# Workbench 服务启动脚本
# 使用 java -cp 方式启动，参考格式：java -cp "path/to/libs/*:src/main/java" com.example.DemoApplication

# 获取项目根目录
PROJECT_DIR="$(cd "$(dirname "${BASH_SOURCE[0]}")" && pwd)"
cd "$PROJECT_DIR"

# 编译项目（如果需要）
echo "检查并编译项目..."
mvn clean compile -DskipTests > /dev/null 2>&1

# 生成依赖 classpath
echo "生成依赖 classpath..."
mvn dependency:build-classpath -Dmdep.outputFile=/tmp/workbench-classpath.txt -q

# 读取 classpath
DEPENDENCY_CLASSPATH=$(cat /tmp/workbench-classpath.txt)

# 构建完整的 classpath（包含编译后的类文件和依赖）
CLASSPATH="${PROJECT_DIR}/target/classes:${DEPENDENCY_CLASSPATH}"

# 添加资源文件路径（如果需要）
CLASSPATH="${CLASSPATH}:${PROJECT_DIR}/src/main/resources"

# 启动命令
MAIN_CLASS="com.aixone.workbench.WorkbenchApplication"

# 默认启动参数
SPRING_PROFILES_ACTIVE="${SPRING_PROFILES_ACTIVE:-dev}"

echo "启动 Workbench 服务..."
echo "主类: ${MAIN_CLASS}"
echo "Profile: ${SPRING_PROFILES_ACTIVE}"
echo ""

# 执行启动命令
java -cp "${CLASSPATH}" \
    -Dspring.profiles.active="${SPRING_PROFILES_ACTIVE}" \
    ${MAIN_CLASS} \
    "$@"


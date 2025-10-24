#!/bin/bash

# aixone-event-sdk 测试运行脚本
# 确保Kafka正常运行并执行所有测试

echo "=========================================="
echo "aixone-event-sdk 测试运行脚本"
echo "=========================================="

# 检查Kafka是否运行
echo "检查Kafka服务状态..."
kafka-topics --bootstrap-server localhost:9092 --list > /dev/null 2>&1
if [ $? -eq 0 ]; then
    echo "✅ Kafka服务正常运行"
else
    echo "❌ Kafka服务未运行，请先启动Kafka"
    echo "启动命令: brew services start kafka"
    exit 1
fi

# 创建测试Topic
echo "创建测试Topic..."
kafka-topics --bootstrap-server localhost:9092 --create --topic real-test-user-events --partitions 1 --replication-factor 1 --if-not-exists
kafka-topics --bootstrap-server localhost:9092 --create --topic real-test-order-events --partitions 1 --replication-factor 1 --if-not-exists
kafka-topics --bootstrap-server localhost:9092 --create --topic real-test-payment-events --partitions 1 --replication-factor 1 --if-not-exists

echo "✅ 测试Topic创建完成"

# 运行单元测试
echo "运行单元测试..."
mvn test -Dtest="*Test" -Dspring.profiles.active=test

if [ $? -eq 0 ]; then
    echo "✅ 单元测试通过"
else
    echo "❌ 单元测试失败"
    exit 1
fi

# 运行集成测试（真实Kafka）
echo "运行真实Kafka集成测试..."
mvn test -Dtest="RealKafkaEventListenerTest" -Dspring.profiles.active=test

if [ $? -eq 0 ]; then
    echo "✅ 真实Kafka集成测试通过"
else
    echo "❌ 真实Kafka集成测试失败"
    exit 1
fi

# 运行TestContainers测试
echo "运行TestContainers集成测试..."
mvn test -Dtest="TestContainersKafkaEventListenerTest" -Dspring.profiles.active=test

if [ $? -eq 0 ]; then
    echo "✅ TestContainers集成测试通过"
else
    echo "❌ TestContainers集成测试失败"
    exit 1
fi

# 清理测试Topic
echo "清理测试Topic..."
kafka-topics --bootstrap-server localhost:9092 --delete --topic real-test-user-events
kafka-topics --bootstrap-server localhost:9092 --delete --topic real-test-order-events
kafka-topics --bootstrap-server localhost:9092 --delete --topic real-test-payment-events

echo "✅ 测试Topic清理完成"

echo "=========================================="
echo "🎉 所有测试完成！"
echo "=========================================="

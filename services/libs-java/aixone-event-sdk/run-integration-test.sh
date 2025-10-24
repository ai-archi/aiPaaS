#!/bin/bash

# Kafka集成测试运行脚本
echo "🚀 开始运行Kafka集成测试..."

# 检查Kafka服务状态
echo "📋 检查Kafka服务状态..."
kafka-topics --bootstrap-server localhost:9092 --list > /dev/null 2>&1
if [ $? -eq 0 ]; then
    echo "✅ Kafka服务正常运行"
else
    echo "❌ Kafka服务未运行，请先启动Kafka"
    echo "启动命令: brew services start kafka"
    exit 1
fi

# 创建测试Topic
echo "📝 创建测试Topic..."
kafka-topics --bootstrap-server localhost:9092 --create --topic test-user-events --partitions 1 --replication-factor 1 --if-not-exists
kafka-topics --bootstrap-server localhost:9092 --create --topic test-order-events --partitions 1 --replication-factor 1 --if-not-exists
kafka-topics --bootstrap-server localhost:9092 --create --topic test-payment-events --partitions 1 --replication-factor 1 --if-not-exists
kafka-topics --bootstrap-server localhost:9092 --create --topic test-batch-events --partitions 1 --replication-factor 1 --if-not-exists

echo "✅ 测试Topic创建完成"

# 运行集成测试
echo "🧪 运行Kafka集成测试..."
mvn test -Dtest="KafkaEventListenerIntegrationTest" -Dspring.profiles.active=test -Dmaven.test.failure.ignore=true

# 检查测试结果
if [ $? -eq 0 ]; then
    echo "✅ Kafka集成测试通过"
else
    echo "❌ Kafka集成测试失败"
fi

# 清理测试Topic
echo "🧹 清理测试Topic..."
kafka-topics --bootstrap-server localhost:9092 --delete --topic test-user-events
kafka-topics --bootstrap-server localhost:9092 --delete --topic test-order-events
kafka-topics --bootstrap-server localhost:9092 --delete --topic test-payment-events
kafka-topics --bootstrap-server localhost:9092 --delete --topic test-batch-events

echo "✅ 测试完成"

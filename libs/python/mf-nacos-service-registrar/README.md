# nacos-client-python

用于 Python 微服务在启动时自动注册/注销到 Nacos 注册中心，并支持服务发现。

## 主要功能
- 支持服务注册、注销到 Nacos
- 支持服务发现（获取健康实例地址）
- 支持容器云环境下自动获取本地 IP
- 适配 gRPC、HTTP 等服务场景

## 快速开始

```python
from nacos_client import NacosServiceRegistrar

registrar = NacosServiceRegistrar(
    server="localhost:8848",
    namespace="public",
    username="nacos",
    password="nacos"
)

# 注册服务
registrar.register(
    service_name="my-grpc-service",
    ip="127.0.0.1",
    port=50051,
    group="DEFAULT_GROUP"
)

# 获取服务实例
address = registrar.get_one_healthy_instance("my-grpc-service")
print("gRPC address:", address)

# 程序退出时注销
registrar.deregister("my-grpc-service", "127.0.0.1", 50051)
```

## 依赖
- nacos-sdk-python 
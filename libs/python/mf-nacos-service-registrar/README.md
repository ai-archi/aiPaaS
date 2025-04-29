# nacos-client-python

用于 Python 微服务在启动时自动注册/注销到 Nacos 注册中心，并支持服务发现。

## 主要功能
- 支持服务注册、注销到 Nacos
- 支持服务发现（获取健康实例地址）
- 支持容器云环境下自动获取本地 IP
- 适配 gRPC、HTTP 等服务场景
- 支持配置中心功能
- 支持服务健康检查

## 安装

### 从 PyPI 安装
```bash
pip install mf-nacos-service-registrar
```

### 从源码安装
```bash
git clone https://github.com/your-org/mf-nacos-service-registrar.git
cd mf-nacos-service-registrar
pip install -e .
```

## 构建和发布

### 构建包
```bash
# 安装构建工具
pip install build twine

# 构建包
python -m build
```

### 发布到 PyPI
```bash
# 发布到测试 PyPI
python -m twine upload --repository testpypi dist/*

# 发布到正式 PyPI
python -m twine upload dist/*
```

## 使用示例

### 1. FastAPI 服务注册示例
```python
from fastapi import FastAPI
from contextlib import asynccontextmanager
from mf_nacos_service_registrar import NacosServiceRegistrar

@asynccontextmanager
async def lifespan(app: FastAPI):
    # 创建注册器
    registrar = NacosServiceRegistrar(
        server="localhost:8848",
        namespace="public",
        username="nacos",
        password="nacos"
    )
    
    # 设置服务信息
    registrar.set_service(
        service_name="fastapi-service",
        service_ip="127.0.0.1",
        service_port=8000,
        service_group="DEFAULT_GROUP"
    )
    
    try:
        # 注册服务
        registrar.register()
        yield
    finally:
        # 注销服务
        registrar.unregister()

app = FastAPI(lifespan=lifespan)
```

### 2. gRPC 服务注册示例
```python
from mf_nacos_service_registrar import NacosServiceRegistrar
import grpc
from concurrent import futures

class MyGrpcService(my_service_pb2_grpc.MyServiceServicer):
    pass

def serve():
    # 创建 gRPC 服务器
    server = grpc.server(futures.ThreadPoolExecutor(max_workers=10))
    my_service_pb2_grpc.add_MyServiceServicer_to_server(MyGrpcService(), server)
    
    # 创建 Nacos 注册器
    registrar = NacosServiceRegistrar(
        server="localhost:8848",
        namespace="public",
        username="nacos",
        password="nacos"
    )
    
    try:
        # 启动 gRPC 服务
        port = 50051
        server.add_insecure_port(f'[::]:{port}')
        server.start()
        
        # 注册服务到 Nacos
        registrar.set_service(
            service_name="grpc-service",
            service_ip="127.0.0.1",
            service_port=port,
            service_group="DEFAULT_GROUP"
        )
        registrar.register()
        
        server.wait_for_termination()
    finally:
        registrar.unregister()
```

### 3. 服务发现示例
```python
from mf_nacos_service_registrar import NacosServiceRegistrar

# 创建注册器
registrar = NacosServiceRegistrar(
    server="localhost:8848",
    namespace="public",
    username="nacos",
    password="nacos"
)

# 获取健康的服务实例
try:
    address = registrar.get_one_healthy_instance("my-service", group="DEFAULT_GROUP")
    print(f"获取到服务地址: {address}")
except RuntimeError as e:
    print(f"未找到健康的服务实例: {e}")
```

### 4. 配置中心示例
```python
from mf_nacos_service_registrar import NacosServiceRegistrar

# 创建注册器
registrar = NacosServiceRegistrar(
    server="localhost:8848",
    namespace="public",
    username="nacos",
    password="nacos"
)

# 获取配置
config = registrar.get_config("my-service.yaml", "DEFAULT_GROUP")

# 监听配置变更
def config_changed(config):
    print(f"配置已更新: {config}")

registrar.add_config_watcher("my-service.yaml", "DEFAULT_GROUP", config_changed)
```

## 环境变量配置
支持通过环境变量配置以下参数：

```bash
# Nacos 服务器地址
export NACOS_SERVER_ADDR=localhost:8848

# 命名空间
export NACOS_NAMESPACE=public

# 认证信息
export NACOS_USERNAME=nacos
export NACOS_PASSWORD=nacos

# 服务配置
export NACOS_SERVICE_NAME=my-service
export NACOS_GROUP=DEFAULT_GROUP
export NACOS_PORT=8000

# 其他配置
export NACOS_WEIGHT=1.0
export NACOS_ENABLE=true
export NACOS_HEALTHY=true
export NACOS_EPHEMERAL=true
```

## 开发说明

### 目录结构
```
mf-nacos-service-registrar/
├── src/
│   └── mf_nacos_service_registrar/
│       ├── __init__.py
│       ├── registrar.py
│       └── utils.py
├── tests/
│   └── test_registrar.py
├── setup.py
├── pyproject.toml
└── README.md
```

### 运行测试
```bash
# 安装测试依赖
pip install pytest pytest-asyncio

# 运行测试
pytest tests/
```

## 贡献指南
1. Fork 本仓库
2. 创建功能分支 (`git checkout -b feature/AmazingFeature`)
3. 提交更改 (`git commit -m 'Add some AmazingFeature'`)
4. 推送到分支 (`git push origin feature/AmazingFeature`)
5. 开启 Pull Request

## 许可证
MIT License 
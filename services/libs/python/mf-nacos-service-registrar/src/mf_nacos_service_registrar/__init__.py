from .registrar import (
    get_local_ip,
    get_nacos_client,
    register_instance,
    deregister_instance,
    NacosServiceRegistrar
)

__version__ = "0.1.1"

__all__ = [
    "get_local_ip",
    "get_nacos_client",
    "register_instance",
    "deregister_instance",
    "NacosServiceRegistrar",
]

# 导出 NacosServiceRegistrar 的实例方法为独立函数

def get_one_healthy_instance_url(
    client,
    service_name: str,
    group: str = "DEFAULT_GROUP",
    schema: str = "http"
) -> str:
    # 兼容 NacosServiceRegistrar 和 NacosClient
    if hasattr(client, "get_one_healthy_instance_url"):
        return client.get_one_healthy_instance_url(service_name, group, schema)
    # 直接用 NacosClient 实现
    instances = client.list_naming_instance(service_name, group_name=group)
    for inst in instances.get('hosts', []):
        if inst.get('healthy'):
            return f"{schema}://{inst['ip']}:{inst['port']}"
    raise RuntimeError(f"No healthy instance for {service_name}")

import os
import nacos
import socket
import logging
from config import settings

_nacos_client = None


def get_local_ip() -> str:
    """获取本机 IP 地址。"""
    try:
        s = socket.socket(socket.AF_INET, socket.SOCK_DGRAM)
        s.connect(("8.8.8.8", 80))
        ip = s.getsockname()[0]
        s.close()
        return ip
    except Exception as e:
        logging.error(f"获取本机 IP 失败: {e}")
        return "127.0.0.1"


def get_nacos_client():
    global _nacos_client
    if _nacos_client is not None:
        return _nacos_client
    server_addr = settings.nacos_server_addr  # 只允许 host:port，不拼 context-path
    if settings.nacos_access_key and settings.nacos_secret_key:
        _nacos_client = nacos.NacosClient(
            server_addr,
            namespace=settings.nacos_namespace,
            ak=settings.nacos_access_key,
            sk=settings.nacos_secret_key
        )
    else:
        _nacos_client = nacos.NacosClient(
            server_addr,
            namespace=settings.nacos_namespace
        )
    return _nacos_client

async def register_instance():
    client = get_nacos_client()
    ip = get_local_ip()
    try:
        client.add_naming_instance(
            settings.nacos_service_name,
            ip,
            settings.port,
            cluster_name=settings.nacos_cluster,
            weight=settings.nacos_weight,
            metadata={"env": settings.environment},
            enable=settings.nacos_enable,
            healthy=settings.nacos_healthy,
            ephemeral=settings.nacos_ephemeral,
            group_name=settings.nacos_group,
            heartbeat_interval=5
        )
        logging.info(f"已注册到 Nacos: {settings.nacos_service_name}@{ip}:{settings.port}")
    except Exception as e:
        logging.error(f"Nacos 注册失败: {e}")

async def deregister_instance():
    client = get_nacos_client()
    ip = get_local_ip()
    try:
        client.remove_naming_instance(
            settings.nacos_service_name,
            ip,
            settings.port,
            cluster_name=settings.nacos_cluster,
            ephemeral=settings.nacos_ephemeral,
            group_name=settings.nacos_group
        )
        logging.info(f"已从 Nacos 注销: {settings.nacos_service_name}@{ip}:{settings.port}")
    except Exception as e:
        logging.error(f"Nacos 注销失败: {e}") 
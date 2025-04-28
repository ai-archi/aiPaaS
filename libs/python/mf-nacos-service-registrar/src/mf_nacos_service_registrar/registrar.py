import nacos
import socket
import logging
from typing import Optional, Dict, Any

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


def get_nacos_client(
    server_addr: str,
    namespace: Optional[str] = None,
    ak: Optional[str] = None,
    sk: Optional[str] = None
):
    """
    获取 NacosClient 实例。
    :param server_addr: 形如 127.0.0.1:8848
    :param namespace: 命名空间 id
    :param ak: access key
    :param sk: secret key
    :return: nacos.NacosClient 实例
    """
    if ak and sk:
        return nacos.NacosClient(server_addr, namespace=namespace, ak=ak, sk=sk)
    return nacos.NacosClient(server_addr, namespace=namespace)


def register_instance(
    client: nacos.NacosClient,
    service_name: str,
    port: int,
    ip: Optional[str] = None,
    cluster_name: str = "DEFAULT",
    weight: float = 1.0,
    metadata: Optional[Dict[str, Any]] = None,
    enable: bool = True,
    healthy: bool = True,
    ephemeral: bool = True,
    group_name: str = "DEFAULT_GROUP",
    heartbeat_interval: int = 5
):
    """
    注册服务实例。
    :param client: nacos.NacosClient 实例
    :param service_name: 服务名
    :param port: 端口
    :param ip: IP，默认自动获取本机IP
    :param cluster_name: 集群名
    :param weight: 权重
    :param metadata: 元数据
    :param enable: 是否启用
    :param healthy: 是否健康
    :param ephemeral: 是否临时实例
    :param group_name: 分组名
    :param heartbeat_interval: 心跳间隔
    """
    if ip is None:
        ip = get_local_ip()
    try:
        client.add_naming_instance(
            service_name,
            ip,
            port,
            cluster_name=cluster_name,
            weight=weight,
            metadata=metadata or {},
            enable=enable,
            healthy=healthy,
            ephemeral=ephemeral,
            group_name=group_name,
            heartbeat_interval=heartbeat_interval
        )
        logging.info(f"已注册到 Nacos: {service_name}@{ip}:{port}")
    except Exception as e:
        logging.error(f"Nacos 注册失败: {e}")


def deregister_instance(
    client: nacos.NacosClient,
    service_name: str,
    port: int,
    ip: Optional[str] = None,
    cluster_name: str = "DEFAULT",
    ephemeral: bool = True,
    group_name: str = "DEFAULT_GROUP"
):
    """
    注销服务实例。
    :param client: nacos.NacosClient 实例
    :param service_name: 服务名
    :param port: 端口
    :param ip: IP，默认自动获取本机IP
    :param cluster_name: 集群名
    :param ephemeral: 是否临时实例
    :param group_name: 分组名
    """
    if ip is None:
        ip = get_local_ip()
    try:
        client.remove_naming_instance(
            service_name,
            ip,
            port,
            cluster_name=cluster_name,
            ephemeral=ephemeral,
            group_name=group_name
        )
        logging.info(f"已从 Nacos 注销: {service_name}@{ip}:{port}")
    except Exception as e:
        logging.error(f"Nacos 注销失败: {e}")


class NacosServiceRegistrar:
    def __init__(
        self,
        server: str,
        namespace: str = "public",
        username: str = None,
        password: str = None
    ):
        self.client = nacos.NacosClient(
            server_addresses=server,
            namespace=namespace,
            username=username,
            password=password
        )
        self.service_name: str = None
        self.service_ip: str = get_local_ip()
        self.service_port: int = None
        self.service_group: str = "DEFAULT_GROUP"

    def set_service(self, service_name: str, service_ip: str, service_port: int, service_group: str):
        self.service_name = service_name
        self.service_ip = service_ip
        self.service_port = service_port
        self.service_group = service_group

    def register(self):
        if not all([self.service_name, self.service_ip, self.service_port, self.service_group]):
            raise ValueError("service_name, service_ip, service_port, service_group 必须先 set_service")
        self.client.add_naming_instance(
            self.service_name,
            self.service_ip,
            self.service_port,
            group_name=self.service_group
        )
        logging.info(f"注册服务: {self.service_name}@{self.service_ip}:{self.service_port} group={self.service_group}")

    def unregister(self):
        if not all([self.service_name, self.service_ip, self.service_port, self.service_group]):
            raise ValueError("service_name, service_ip, service_port, service_group 必须先 set_service")
        self.client.remove_naming_instance(
            self.service_name,
            self.service_ip,
            self.service_port,
            group_name=self.service_group
        )
        logging.info(f"注销服务: {self.service_name}@{self.service_ip}:{self.service_port} group={self.service_group}")

    def modify(self, service_name: str, service_ip: str = None, service_port: int = None):
        self.client.modify_naming_instance(
            service_name,
            service_ip if service_ip else self.service_ip,
            service_port if service_port else self.service_port
        )
        logging.info(f"修改服务实例: {service_name}@{service_ip or self.service_ip}:{service_port or self.service_port}")

    def send_heartbeat(self):
        if not all([self.service_name, self.service_ip, self.service_port]):
            raise ValueError("service_name, service_ip, service_port 必须先 set_service")
        self.client.send_heartbeat(
            self.service_name,
            self.service_ip,
            self.service_port
        )
        logging.info(f"发送心跳: {self.service_name}@{self.service_ip}:{self.service_port}")

    def get_config(self, data_id: str, group: str) -> str:
        return self.client.get_config(data_id=data_id, group=group, no_snapshot=True)

    def add_config_watcher(self, data_id: str, group: str, callback):
        self.client.add_config_watcher(data_id=data_id, group=group, cb=callback)
        logging.info(f"添加配置监听: {data_id}@{group}")

    def get_one_healthy_instance(self, service_name: str, group: str = "DEFAULT_GROUP") -> str:
        instances = self.client.list_naming_instance(service_name, group_name=group)
        for inst in instances['hosts']:
            if inst['healthy']:
                return f"{inst['ip']}:{inst['port']}"
        raise RuntimeError(f"No healthy instance for {service_name}")

    def get_one_healthy_instance_url(
        self,
        service_name: str,
        group: str = "DEFAULT_GROUP",
        schema: str = "http"
    ) -> str:
        """
        返回可用服务的完整 URL（如 http://ip:port）
        """
        instances = self.client.list_naming_instance(service_name, group_name=group)
        for inst in instances.get('hosts', []):
            if inst.get('healthy'):
                return f"{schema}://{inst['ip']}:{inst['port']}"
        raise RuntimeError(f"No healthy instance for {service_name}") 
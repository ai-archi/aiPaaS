import nacos
import socket
from typing import Optional

class NacosServiceRegistrar:
    def __init__(self, server: str, namespace: str = "public", username: str = "nacos", password: str = "nacos"):
        self.client = nacos.NacosClient(
            server_addresses=server,
            namespace=namespace,
            username=username,
            password=password
        )

    @staticmethod
    def get_local_ip() -> str:
        s = socket.socket(socket.AF_INET, socket.SOCK_DGRAM)
        try:
            s.connect(('10.255.255.255', 1))
            IP = s.getsockname()[0]
        except Exception:
            IP = '127.0.0.1'
        finally:
            s.close()
        return IP

    def register(self, service_name: str, ip: Optional[str] = None, port: int = 50051, group: str = "DEFAULT_GROUP"):
        ip = ip or self.get_local_ip()
        self.client.add_naming_instance(
            service_name,
            ip,
            port,
            group_name=group,
            ephemeral=True
        )

    def deregister(self, service_name: str, ip: Optional[str] = None, port: int = 50051, group: str = "DEFAULT_GROUP"):
        ip = ip or self.get_local_ip()
        self.client.remove_naming_instance(
            service_name,
            ip,
            port,
            group_name=group
        )

    def get_one_healthy_instance(self, service_name: str, group: str = "DEFAULT_GROUP") -> str:
        instances = self.client.list_naming_instance(service_name, group_name=group)
        for inst in instances['hosts']:
            if inst['healthy']:
                return f"{inst['ip']}:{inst['port']}"
        raise RuntimeError(f"No healthy instance for {service_name}") 
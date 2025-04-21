from typing import List
from ..entities.base import Entity
from ..events.base import DomainEvent

class AggregateRoot(Entity):
    """基础聚合根类"""
    version: int = 1
    _domain_events: List[DomainEvent] = []
    
    def add_domain_event(self, event: DomainEvent) -> None:
        """添加领域事件"""
        self._domain_events.append(event)
    
    def clear_domain_events(self) -> None:
        """清除领域事件"""
        self._domain_events = []
    
    @property
    def domain_events(self) -> List[DomainEvent]:
        """获取领域事件列表"""
        return self._domain_events 
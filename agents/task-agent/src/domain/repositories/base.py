"""
Base repository interface.
"""
from abc import ABC, abstractmethod
from typing import Generic, TypeVar, Optional, List, Any

T = TypeVar('T')

class BaseRepository(Generic[T], ABC):
    """Base repository interface for domain entities."""
    
    @abstractmethod
    async def get(self, id: Any) -> Optional[T]:
        """Get entity by id."""
        pass
    
    @abstractmethod
    async def list(self, **filters) -> List[T]:
        """List entities with filters."""
        pass
    
    @abstractmethod
    async def add(self, entity: T) -> T:
        """Add new entity."""
        pass
    
    @abstractmethod
    async def update(self, entity: T) -> T:
        """Update existing entity."""
        pass
    
    @abstractmethod
    async def delete(self, id: Any) -> bool:
        """Delete entity by id."""
        pass 
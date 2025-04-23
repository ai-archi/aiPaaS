"""
Base application service.
"""
from abc import ABC
from typing import Any, Dict

class BaseApplicationService(ABC):
    """Base class for application services."""
    
    def __init__(self, uow: Any = None):
        """Initialize application service with unit of work."""
        self.uow = uow
    
    async def _begin_transaction(self) -> None:
        """Begin a new transaction if unit of work is available."""
        if self.uow:
            await self.uow.begin()
    
    async def _commit_transaction(self) -> None:
        """Commit current transaction if unit of work is available."""
        if self.uow:
            await self.uow.commit()
    
    async def _rollback_transaction(self) -> None:
        """Rollback current transaction if unit of work is available."""
        if self.uow:
            await self.uow.rollback() 
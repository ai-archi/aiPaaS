"""
Base command handler.
"""
from abc import ABC, abstractmethod
from typing import Any, Generic, TypeVar

Input = TypeVar('Input')
Output = TypeVar('Output')

class BaseCommand(Generic[Input, Output], ABC):
    """Base class for command handlers."""
    
    @abstractmethod
    async def execute(self, command: Input) -> Output:
        """Execute the command."""
        pass 
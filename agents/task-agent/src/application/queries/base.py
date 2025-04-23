"""
Base query handler.
"""
from abc import ABC, abstractmethod
from typing import Any, Generic, TypeVar

Input = TypeVar('Input')
Output = TypeVar('Output')

class BaseQuery(Generic[Input, Output], ABC):
    """Base class for query handlers."""
    
    @abstractmethod
    async def execute(self, query: Input) -> Output:
        """Execute the query."""
        pass 
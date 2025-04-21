from typing import Dict, List, Optional
from metagpt.roles import Role
from metagpt.schema import Message
from metagpt.actions import Action

class CreateTask(Action):
    """创建新任务的动作"""
    def __init__(self, name: str = "create_task"):
        super().__init__(name)
    
    async def run(self, task_info: Dict) -> Dict:
        """创建新任务"""
        # 实现任务创建逻辑
        return {"status": "success", "task_id": "123", "message": "Task created successfully"}

class UpdateTask(Action):
    """更新任务状态的动作"""
    def __init__(self, name: str = "update_task"):
        super().__init__(name)
    
    async def run(self, task_id: str, updates: Dict) -> Dict:
        """更新任务状态"""
        # 实现任务更新逻辑
        return {"status": "success", "task_id": task_id, "message": "Task updated successfully"}

class TaskManagerAgent(Role):
    """任务管理 Agent"""
    def __init__(
        self,
        name: str = "TaskManager",
        profile: str = "I am a task manager agent, responsible for managing and coordinating tasks.",
        goal: str = "Efficiently manage and track tasks",
        constraints: List[str] = None
    ):
        super().__init__(name, profile, goal, constraints or [])
        self.actions = [CreateTask(), UpdateTask()]
        self._init_actions([CreateTask, UpdateTask])
    
    async def handle_message(self, message: Message) -> Optional[Message]:
        """处理接收到的消息"""
        # 根据消息类型选择相应的动作
        if message.content.get("action") == "create_task":
            action = self.get_action(CreateTask)
            result = await action.run(message.content.get("task_info", {}))
        elif message.content.get("action") == "update_task":
            action = self.get_action(UpdateTask)
            result = await action.run(
                message.content.get("task_id"),
                message.content.get("updates", {})
            )
        else:
            result = {"status": "error", "message": "Unknown action"}
        
        return Message(content=result, role=self.name) 
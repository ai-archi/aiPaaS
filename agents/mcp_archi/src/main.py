from fastmcp import FastMCP
import asyncio
import logging
from pydantic import BaseModel
from typing import List, Optional, Dict, Any
from src.domain.todo_model import TodoItem
from src.domain.workflow_model import Workflow
from src.infrastructure.todo_repository import load_todos, save_todos
from src.infrastructure.workflow_repository import load_workflows, save_workflows
import uuid

logging.basicConfig(
    level=logging.INFO,
    format='%(asctime)s [%(levelname)s] %(filename)s:%(lineno)d %(message)s'
)

mcp = FastMCP("FastMCP Demo Server")

# ==== Pydantic 模型定义 ====
class TodoItem(BaseModel):
    id: str
    title: str
    description: Optional[str] = None
    parent_id: Optional[str] = None  # 支持两层结构
    workflow_id: Optional[str] = None

class Workflow(BaseModel):
    id: str
    name: str
    steps: List[Dict[str, Any]]

# ==== Tool 实现 ====
@mcp.tool()
def todo_list(parent_id: Optional[str] = None) -> List[TodoItem]:
    """查询 TODO 列表 """
    # TODO: 实现真实查询逻辑
    return [
        TodoItem(id="1", title="示例任务", parent_id=None, workflow_id="wf1"),
        TodoItem(id="2", title="子任务", parent_id="1", workflow_id=None)
    ]
@mcp.tool()
def todo_add(todo: TodoItem, workflow: Optional[Workflow] = None) -> Dict[str, Any]:
    """添加 TODO，可指定已有 workflow 或直接生成新的临时 workflow 并绑定"""
    todos = load_todos()
    workflows = load_workflows()
    # 生成 TODO id
    todo_id = str(uuid.uuid4())
    todo_dict = todo.model_dump()
    todo_dict['id'] = todo_id

    # 如果传入 workflow，则新建 workflow 并绑定
    if workflow is not None:
        workflow_id = str(uuid.uuid4())
        workflow_dict = workflow.model_dump()
        workflow_dict['id'] = workflow_id
        workflows.append(Workflow(**workflow_dict))
        save_workflows(workflows)
        todo_dict['workflow_id'] = workflow_id

    # 构造新的 TodoItem
    new_todo = TodoItem(**todo_dict)
    todos.append(new_todo)
    save_todos(todos)
    return {"todo": new_todo.model_dump(), "result": "success"}


@mcp.tool()
def todo_run(todo_id: str) -> Dict[str, Any]:
    """运行指定 TODO。如果绑定 workflow，则自动调用 workflow_run。"""
    # TODO: 查询 todo 详情
    # 示例：假设 id=1 绑定了 workflow
    if todo_id == "1":
        workflow_id = "wf1"
        # 自动调用 workflow_run
        workflow_result = workflow_run(workflow_id)
        return {"todo_id": todo_id, "status": "workflow_executed", "workflow_result": workflow_result}
    # 未绑定 workflow，直接执行
    return {"todo_id": todo_id, "status": "executed"}

@mcp.tool()
def workflow_list() -> List[Workflow]:
    """查询 workflow 列表"""
    return load_workflows()

@mcp.tool()
def workflow_add(workflow: Workflow) -> Dict[str, Any]:
    """添加 workflow"""
    workflows = load_workflows()
    workflow_id = str(uuid.uuid4())
    workflow_dict = workflow.model_dump()
    workflow_dict['id'] = workflow_id
    new_workflow = Workflow(**workflow_dict)
    workflows.append(new_workflow)
    save_workflows(workflows)
    return {"workflow": new_workflow.model_dump(), "result": "success"}

@mcp.tool()
def workflow_update(workflow: Dict[str, Any]) -> Dict[str, Any]:
    """更新 workflow"""
    # TODO: 实现增删改逻辑
    return {"workflow": workflow, "result": "success"}
@mcp.tool()
def workflow_delete(workflow: Dict[str, Any]) -> Dict[str, Any]:
    """删除 workflow"""
    # TODO: 实现增删改逻辑
    return {"workflow": workflow, "result": "success"}

@mcp.tool()
def workflow_run(workflow_id: str, params: Optional[Dict[str, Any]] = None) -> Dict[str, Any]:
    """运行指定 workflow"""
    # TODO: 实现 workflow 执行逻辑
    return {"workflow_id": workflow_id, "status": "executed", "params": params}

if __name__ == "__main__":
    asyncio.run(mcp.run_sse_async(host="127.0.0.1", port=8000))
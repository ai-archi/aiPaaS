import yaml
from pathlib import Path
from typing import List
from src.domain.workflow_model import Workflow

WORKFLOW_FILE = Path(__file__).parent.parent / "data" / "workflows.yaml"

def load_workflows() -> List[Workflow]:
    if not WORKFLOW_FILE.exists():
        return []
    try:
        with open(WORKFLOW_FILE, "r", encoding="utf-8") as f:
            data = yaml.safe_load(f) or []
        return [Workflow(**item) for item in data]
    except Exception:
        return []

def save_workflows(workflows: List[Workflow]) -> None:
    WORKFLOW_FILE.parent.mkdir(parents=True, exist_ok=True)
    with open(WORKFLOW_FILE, "w", encoding="utf-8") as f:
        yaml.safe_dump([wf.model_dump() for wf in workflows], f, allow_unicode=True) 
from typing import Generator
from app.db.session import get_db

# Re-export database dependency
get_db = get_db 
#!/usr/bin/env python3
import os
import shutil
from pathlib import Path

def clean_pycache():
    """清理所有 __pycache__ 目录和 .pyc 文件"""
    root_dir = Path(__file__).parent.parent
    
    # 清理 __pycache__ 目录
    pycache_dirs = list(root_dir.glob("**/__pycache__"))
    for dir_path in pycache_dirs:
        print(f"删除目录: {dir_path}")
        shutil.rmtree(dir_path)
    
    # 清理 .pyc 文件
    pyc_files = list(root_dir.glob("**/*.pyc"))
    for file_path in pyc_files:
        print(f"删除文件: {file_path}")
        file_path.unlink()

if __name__ == "__main__":
    clean_pycache() 
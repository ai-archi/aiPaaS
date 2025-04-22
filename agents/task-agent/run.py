import os
import uvicorn
from dotenv import load_dotenv

# 加载环境变量
load_dotenv()

if __name__ == "__main__":
    # 从环境变量获取配置
    port = int(os.getenv("PORT", "8001"))
    env = os.getenv("PYTHON_ENV", "development")
    debug = env in ["development", "local"]
    
    print(f"Starting server on port {port} in {env} environment")
    print(f"Debug mode: {debug}")
    
    uvicorn.run(
        "app.main:app",
        host="0.0.0.0",  # 允许外部访问
        port=port,
        reload=debug,
        log_level="debug" if debug else "info"
    ) 
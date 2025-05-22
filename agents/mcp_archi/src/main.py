from fastmcp import FastMCP
import asyncio
import logging

logging.basicConfig(
    level=logging.INFO,
    format='%(asctime)s [%(levelname)s] %(filename)s:%(lineno)d %(message)s'
)

mcp = FastMCP("FastMCP Demo Server")


@mcp.tool()
def echo(text: str) -> str:
    """回显输入文本"""
    return text

if __name__ == "__main__":
    asyncio.run(mcp.run_sse_async(host="127.0.0.1", port=8000))
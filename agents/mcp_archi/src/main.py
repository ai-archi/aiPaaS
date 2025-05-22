from fastmcp import FastMCP
import asyncio

mcp = FastMCP("FastMCP Demo Server")

@mcp.tool()
def add(a: int, b: int) -> int:
    """Add two numbers"""
    return a + b

if __name__ == "__main__":
    asyncio.run(mcp.run_sse_async(host="127.0.0.1", port=8000))
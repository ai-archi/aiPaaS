## 日志配置说明

本项目已集成标准库 `logging` 作为日志系统。

- 启动日志：服务启动时会自动输出关键信息（如端口、模式等）。
- 运行日志：各工具、主流程等均可通过 `import logging` 获取 logger 并输出日志。
- 日志格式：`%(asctime)s [%(levelname)s] %(name)s: %(message)s`
- 日志级别：INFO（可根据需要调整）

如需在自定义模块中输出日志：
```python
import logging
logging.info("你的日志内容")
``` 
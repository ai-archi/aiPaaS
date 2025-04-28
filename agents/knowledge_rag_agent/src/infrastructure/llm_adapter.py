from domain.ports import LLMServicePort

class LLMServiceClient(LLMServicePort):
    async def generate(self, context: str, question: str) -> str:
        # TODO: 调用实际 LLM 服务
        ... 
请实现一个简单 RAG Engine。

功能：

1 文本 chunk
2 embedding
3 vector search
4 context retrieval

核心类：

EmbeddingService
VectorStore
RAGEngine

函数：

retrieveContext(question: String): List<String>

要求：

Kotlin 实现
结构清晰
可扩展到真实向量数据库。
你是一名资深 Android 架构师和 AI 工程师。

请帮我从 0 到 1 设计并实现一个完整项目：

项目名称：
AI Browser Assistant

项目目标：
在 Android 浏览器中集成 AI 能力，实现：

1 AI 总结网页
2 AI 问网页
3 AI 自动操作网页（Agent）

技术要求：

Android：
- Kotlin
- Jetpack Compose
- MVVM
- Coroutines + Flow
- WebView

AI 技术：
- LLM API
- RAG
- Embedding
- Vector Search
- Tool Calling / Function Calling
- AI Agent

功能模块：

1 浏览器模块
- WebView 浏览网页
- Tab 管理

2 网页解析模块
- DOM 提取
- 正文抽取
- 文本清洗

3 RAG 模块
- 文本 chunk
- embedding
- 向量检索

4 AI SDK
- 统一 AI 调用接口
- streaming 输出
- prompt 管理
- token 控制

5 AI Agent
- Tool Registry
- Planner
- Tool Executor
- Memory

6 Web Tools
- open_url
- click
- type
- extract_text

输出要求：

1 给出完整系统架构
2 给出模块划分
3 给出 Android 项目目录结构
4 说明每个模块职责
5 不要直接生成全部代码，先设计架构
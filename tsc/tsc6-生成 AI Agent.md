现在实现 AI Agent 模块。

功能：

根据用户任务自动调用工具。

核心组件：

Planner
ToolRegistry
ToolExecutor
Memory

定义：

interface Tool {
val name: String
suspend fun execute(params: Map<String, Any>): String
}

实现 Web 工具：

open_url
click
type
extract_text

代码必须：

Kotlin
结构清晰
易扩展。
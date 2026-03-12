package com.icon.aibrowserasistor.agent

import com.icon.aibrowserasistor.tools.web.ToolResult
import com.icon.aibrowserasistor.tools.web.WebToolExecutor
import com.icon.aibrowserasistor.tools.web.WebToolRequest
import com.icon.aibrowserasistor.tools.web.WebToolType

class AgentOpenUrlTool(private val executor: WebToolExecutor) : Tool {
    override val name: String = "open_url"

    override suspend fun execute(params: Map<String, Any>): String {
        val url = params["url"]?.toString().orEmpty()
        if (url.isBlank()) return "open_url: missing url"
        return executor.execute(WebToolRequest(WebToolType.OPEN_URL, mapOf("url" to url))).toMessage()
    }
}

class AgentClickTool(private val executor: WebToolExecutor) : Tool {
    override val name: String = "click"

    override suspend fun execute(params: Map<String, Any>): String {
        val selector = params["selector"]?.toString().orEmpty()
        if (selector.isBlank()) return "click: missing selector"
        return executor.execute(WebToolRequest(WebToolType.CLICK, mapOf("selector" to selector))).toMessage()
    }
}

class AgentTypeTool(private val executor: WebToolExecutor) : Tool {
    override val name: String = "type"

    override suspend fun execute(params: Map<String, Any>): String {
        val selector = params["selector"]?.toString().orEmpty()
        val text = params["text"]?.toString().orEmpty()
        if (selector.isBlank()) return "type: missing selector"
        return executor.execute(WebToolRequest(WebToolType.TYPE, mapOf("selector" to selector, "text" to text))).toMessage()
    }
}

class AgentExtractTextTool(private val executor: WebToolExecutor) : Tool {
    override val name: String = "extract_text"

    override suspend fun execute(params: Map<String, Any>): String {
        val selector = params["selector"]?.toString()
        val args = if (selector.isNullOrBlank()) emptyMap() else mapOf("selector" to selector)
        return executor.execute(WebToolRequest(WebToolType.EXTRACT_TEXT, args)).toMessage()
    }
}

private fun ToolResult.toMessage(): String = when (this) {
    is ToolResult.Success -> result.output
    is ToolResult.Failure -> "error: ${this.reason}"
}

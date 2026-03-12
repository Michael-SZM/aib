package com.icon.aibrowserasistor.common

data class TabState(
    val id: String,
    val title: String? = null,
    val url: String? = null
)

data class BrowserState(
    val tabs: List<TabState> = emptyList(),
    val activeTabId: String? = null
)

data class WebPageContent(
    val url: String,
    val html: String,
    val text: String
)

data class ContentChunk(
    val id: String,
    val text: String,
    val sourceUrl: String
)

data class EmbeddingVector(
    val id: String,
    val values: List<Float>,
    val metadata: Map<String, String> = emptyMap()
)

data class RagContext(
    val chunks: List<ContentChunk>
)

enum class AiRole {
    System,
    User,
    Assistant
}

data class AiMessage(
    val role: AiRole,
    val content: String
)

data class AiResponseChunk(
    val text: String,
    val isFinal: Boolean
)

data class AgentTask(
    val goal: String,
    val contextUrl: String? = null
)

data class AgentPlanStep(
    val description: String,
    val toolHint: String? = null
)

data class ToolCallResult(
    val output: String,
    val success: Boolean = true
)

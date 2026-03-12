package com.icon.aibrowserasistor.ai.sdk

import com.google.gson.annotations.SerializedName

data class ChatMessage(
    val role: String,
    val content: String
)

data class ChatRequest(
    val model: String,
    val messages: List<ChatMessage>,
    @SerializedName("max_tokens") val maxTokens: Int? = null,
    val temperature: Double? = null,
    val stream: Boolean = false
)

data class ChatChoice(
    val index: Int,
    val message: ChatMessage?,
    val delta: ChatMessage?,
    val finishReason: String? = null
)

data class ChatResponse(
    val id: String,
    val created: Long,
    val model: String,
    val choices: List<ChatChoice>
)

data class ChatStreamChunk(
    val id: String,
    val created: Long,
    val model: String,
    val choices: List<ChatChoice>
)

data class AIProvider(
    val baseUrl: String,
    val apiKey: String,
    val defaultModel: String
)

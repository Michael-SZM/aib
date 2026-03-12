package com.icon.aibrowserasistor.ai.sdk

import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.delay
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.flow
import kotlinx.coroutines.flow.flowOn

class FakeAIClient : AIClient(
    provider = AIProvider(
        baseUrl = "https://example.com/",
        apiKey = "fake-key",
        defaultModel = "fake-model"
    ),
    promptManager = PromptManager()
) {
    override suspend fun chat(messages: List<ChatMessage>, model: String?): ChatResponse {
        val content = "Fake response: " + messages.lastOrNull()?.content.orEmpty()
        val message = ChatMessage(role = "assistant", content = content)
        val choice = ChatChoice(index = 0, message = message, delta = null, finishReason = "stop")
        return ChatResponse(id = "fake", created = System.currentTimeMillis(), model = model ?: "fake-model", choices = listOf(choice))
    }

    override fun streamChat(messages: List<ChatMessage>, model: String?): Flow<ChatStreamChunk> {
        return flow {
            val content = "Fake streaming: " + messages.lastOrNull()?.content.orEmpty()
            val delta = ChatMessage(role = "assistant", content = content)
            emit(ChatStreamChunk(id = "fake", created = System.currentTimeMillis(), model = model ?: "fake-model", choices = listOf(ChatChoice(0, null, delta, null))))
            delay(50)
            emit(ChatStreamChunk(id = "fake", created = System.currentTimeMillis(), model = model ?: "fake-model", choices = listOf(ChatChoice(0, ChatMessage("assistant", "[done]"), null, "stop"))))
        }.flowOn(Dispatchers.IO)
    }

    override fun prompt(name: String): String? = super.prompt(name)
}

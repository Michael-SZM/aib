package com.icon.aibrowserasistor.ai.service

import android.webkit.WebView
import com.icon.aibrowserasistor.ai.sdk.AIClient
import com.icon.aibrowserasistor.ai.sdk.ChatMessage
import com.icon.aibrowserasistor.parser.WebParser
import com.icon.aibrowserasistor.rag.RAGEngine
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.withContext

class AskPageService(
    private val webView: WebView,
    private val ragEngine: RAGEngine,
    private val aiClient: AIClient
) {
    suspend fun askPage(question: String): String = withContext(Dispatchers.IO) {
        val pageText = WebParser.extractPageText(webView)
        ragEngine.index(pageText)
        val context = ragEngine.retrieveContext(question)

        val messages = buildList {
            add(ChatMessage(role = "system", content = "You are a helpful assistant that answers based on page content."))
            add(ChatMessage(role = "user", content = "Question: $question\nContext: ${context.joinToString("\n")}"))
        }

        val response = aiClient.chat(messages)
        val content = response.choices.firstOrNull()?.message?.content
        content ?: "No answer"
    }
}

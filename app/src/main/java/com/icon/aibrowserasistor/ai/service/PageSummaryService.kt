package com.icon.aibrowserasistor.ai.service

import android.webkit.WebView
import com.icon.aibrowserasistor.ai.sdk.AIClient
import com.icon.aibrowserasistor.ai.sdk.ChatMessage
import com.icon.aibrowserasistor.parser.WebParser
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.withContext

class PageSummaryService(
    private val webView: WebView,
    private val aiClient: AIClient,
    private val chunkSize: Int = 800
) {

    suspend fun summarizePage(): String = withContext(Dispatchers.IO) {
        val rawText = WebParser.extractPageText(webView)
        val chunks = WebParser.chunkText(rawText, chunkSize).ifEmpty { listOf(rawText) }

        val partialSummaries = chunks.map { chunk -> summarizeChunk(chunk) }.filter { it.isNotBlank() }
        if (partialSummaries.isEmpty()) return@withContext "No content to summarize"
        if (partialSummaries.size == 1) return@withContext partialSummaries.first()

        mergeSummaries(partialSummaries)
    }

    private suspend fun summarizeChunk(chunk: String): String {
        val messages = listOf(
            ChatMessage(role = "system", content = "Summarize the following text concisely."),
            ChatMessage(role = "user", content = chunk.take(4000))
        )
        val response = aiClient.chat(messages)
        return response.choices.firstOrNull()?.message?.content.orEmpty()
    }

    private suspend fun mergeSummaries(summaries: List<String>): String {
        val messages = listOf(
            ChatMessage(role = "system", content = "Merge the provided partial summaries into a concise overall summary."),
            ChatMessage(role = "user", content = summaries.joinToString(separator = "\n"))
        )
        val response = aiClient.chat(messages)
        return response.choices.firstOrNull()?.message?.content ?: summaries.joinToString(" ")
    }
}

package com.icon.aibrowserasistor.parser

import com.icon.aibrowserasistor.common.ContentChunk
import com.icon.aibrowserasistor.common.WebPageContent

data class ParsedContent(
    val cleanedText: String,
    val chunks: List<ContentChunk>
)

interface ContentParser {
    suspend fun parse(content: WebPageContent): ParsedContent
}

class DefaultContentParser : ContentParser {
    override suspend fun parse(content: WebPageContent): ParsedContent {
        val normalized = content.text.trim()
        val chunk = ContentChunk(
            id = content.url,
            text = normalized,
            sourceUrl = content.url
        )
        return ParsedContent(cleanedText = normalized, chunks = listOf(chunk))
    }
}

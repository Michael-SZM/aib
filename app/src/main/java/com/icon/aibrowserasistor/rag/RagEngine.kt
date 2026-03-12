package com.icon.aibrowserasistor.rag

import com.icon.aibrowserasistor.common.EmbeddingVector
import com.icon.aibrowserasistor.data.vector.VectorStore
import com.icon.aibrowserasistor.parser.WebParser

interface EmbeddingService {
    suspend fun embed(texts: List<String>): List<EmbeddingVector>
}

class FakeEmbeddingService : EmbeddingService {
    override suspend fun embed(texts: List<String>): List<EmbeddingVector> {
        return texts.map { text ->
            val values = List(16) { index -> (text.hashCode() + index).toFloat() }
            EmbeddingVector(id = text.hashCode().toString(), values = values, metadata = mapOf("text" to text))
        }
    }
}

class RAGEngine(
    private val embeddingService: EmbeddingService,
    private val vectorStore: VectorStore,
    private val chunkSize: Int = 400,
    private val topK: Int = 3
) {
    suspend fun index(documentText: String, source: String? = null) {
        val chunks = WebParser.chunkText(documentText, chunkSize)
        if (chunks.isEmpty()) return
        val vectors = embeddingService.embed(chunks)
        vectors.forEachIndexed { idx, vec ->
            val text = chunks[idx]
            val enriched = vec.copy(metadata = vec.metadata + mapOf("text" to text, "source" to (source ?: "")))
            vectorStore.upsert(enriched)
        }
    }

    suspend fun retrieveContext(question: String): List<String> {
        val queryVec = embeddingService.embed(listOf(question)).firstOrNull() ?: return emptyList()
        val matches = vectorStore.search(queryVec, topK)
        return matches.map { it.metadata["text"].orEmpty() }.filter { it.isNotBlank() }
    }
}

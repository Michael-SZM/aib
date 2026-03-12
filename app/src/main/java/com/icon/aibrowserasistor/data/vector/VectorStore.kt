package com.icon.aibrowserasistor.data.vector

import com.icon.aibrowserasistor.common.EmbeddingVector
import kotlin.math.sqrt

interface VectorStore {
    suspend fun upsert(vector: EmbeddingVector)

    suspend fun search(query: EmbeddingVector, topK: Int = 3): List<EmbeddingVector>
}

class InMemoryVectorStore : VectorStore {
    private val vectors = mutableListOf<EmbeddingVector>()

    override suspend fun upsert(vector: EmbeddingVector) {
        vectors.removeAll { it.id == vector.id }
        vectors.add(vector)
    }

    override suspend fun search(query: EmbeddingVector, topK: Int): List<EmbeddingVector> {
        if (vectors.isEmpty()) return emptyList()
        return vectors
            .sortedByDescending { cosineSimilarity(query.values, it.values) }
            .take(topK)
    }

    private fun cosineSimilarity(a: List<Float>, b: List<Float>): Double {
        val minSize = minOf(a.size, b.size)
        if (minSize == 0) return 0.0
        var dot = 0.0
        var normA = 0.0
        var normB = 0.0
        for (i in 0 until minSize) {
            dot += a[i] * b[i]
            normA += a[i] * a[i]
            normB += b[i] * b[i]
        }
        if (normA == 0.0 || normB == 0.0) return 0.0
        return dot / (sqrt(normA) * sqrt(normB))
    }
}

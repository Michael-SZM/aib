package com.icon.aibrowserasistor.common

fun ContentChunk.embeddingId(): EmbeddingVector {
    val values = List(8) { index -> (text.length + index).toFloat() }
    return EmbeddingVector(
        id = id,
        values = values,
        metadata = mapOf("text" to text, "url" to sourceUrl)
    )
}

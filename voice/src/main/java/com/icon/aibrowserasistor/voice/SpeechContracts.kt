package com.icon.aibrowserasistor.voice

data class SpeechResult(
    val text: String,
    val isFinal: Boolean
)

interface SpeechAdapter {
    fun startListening(onResult: (SpeechResult) -> Unit)
    fun stopListening()
    fun release()
}

class SpeechFacade(
    private var adapter: SpeechAdapter
) {
    fun setAdapter(adapter: SpeechAdapter) {
        this.adapter = adapter
    }

    fun start(onResult: (SpeechResult) -> Unit) {
        adapter.startListening(onResult)
    }

    fun stop() {
        adapter.stopListening()
    }

    fun release() {
        adapter.release()
    }
}

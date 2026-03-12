package com.icon.aibrowserasistor.data.session

import com.icon.aibrowserasistor.common.AiMessage

class SessionRepository {
    private val history = mutableListOf<AiMessage>()

    fun add(message: AiMessage) {
        history.add(message)
    }

    fun all(): List<AiMessage> = history.toList()
}

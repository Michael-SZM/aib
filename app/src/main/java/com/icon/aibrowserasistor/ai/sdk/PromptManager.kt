package com.icon.aibrowserasistor.ai.sdk

class PromptManager {
    private val templates = mutableMapOf<String, String>()

    fun register(name: String, template: String) {
        templates[name] = template
    }

    fun resolve(name: String): String? = templates[name]
}

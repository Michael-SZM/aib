package com.icon.aibrowserasistor.voice

object SpeechServiceLocator {
    @Volatile
    private var facade: SpeechFacade? = null

    fun init(defaultFacade: SpeechFacade) {
        facade = defaultFacade
    }

    fun facade(): SpeechFacade {
        return facade ?: throw IllegalStateException("SpeechFacade not initialized")
    }
}

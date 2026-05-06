package com.icon.aibrowserasistor.hybrid.bridge

/**
 * JSBridge 回调管理器
 */
class CallbackManager {
    private val callbacks = mutableMapOf<String, (String) -> Unit>()

    fun addCallback(id: String, callback: (String) -> Unit) {
        callbacks[id] = callback
    }

    fun invokeCallback(id: String, data: String) {
        callbacks[id]?.invoke(data)
        callbacks.remove(id)
    }
}

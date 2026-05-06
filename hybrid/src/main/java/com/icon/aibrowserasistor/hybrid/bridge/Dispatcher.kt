package com.icon.aibrowserasistor.hybrid.bridge

import com.google.gson.Gson
import com.icon.aibrowserasistor.hybrid.plugins.PluginManager

/**
 * JSBridge 消息分发器
 */
class Dispatcher(private val pluginManager: PluginManager) {
    private val gson = Gson()

    fun dispatch(message: String, callbackManager: CallbackManager) {
        try {
            val request = gson.fromJson(message, JsRequest::class.java)
            pluginManager.execute(request) { result ->
                val response = JsResponse(id = request.id, result = result)
                callbackManager.invokeCallback(request.id, gson.toJson(response))
            }
        } catch (e: Exception) {
            // 解析失败处理
        }
    }
}

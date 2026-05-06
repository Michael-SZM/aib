package com.icon.aibrowserasistor.hybrid.bridge

import android.net.Uri
import android.webkit.WebView
import com.icon.aibrowserasistor.hybrid.plugins.PluginManager
import java.net.URLDecoder

/**
 * JSBridge 核心类 - 使用 URL Scheme 拦截方式实现，提高安全性
 */
class BridgeCore(private val webView: WebView) {

    private val pluginManager = PluginManager()
    private val dispatcher = Dispatcher(pluginManager)
    private val callbackManager = CallbackManager()

    companion object {
        const val SCHEME = "jsbridge"
        const val HOST_POST_MESSAGE = "postMessage"
    }

    /**
     * 处理拦截到的 URL
     * 协议格式示例: jsbridge://postMessage?data=URL_ENCODED_JSON
     * @return 如果是 JSBridge 协议并已处理则返回 true，否则返回 false
     */
    fun handleUrl(url: String): Boolean {
        val uri = Uri.parse(url)
        if (uri.scheme != SCHEME) return false

        when (uri.host) {
            HOST_POST_MESSAGE -> {
                val data = uri.getQueryParameter("data")
                if (data != null) {
                    try {
                        val decodedData = URLDecoder.decode(data, "UTF-8")
                        postMessage(decodedData)
                    } catch (e: Exception) {
                        e.printStackTrace()
                    }
                }
            }
        }
        return true
    }

    private fun postMessage(message: String) {
        // 在后台线程处理分发逻辑
        dispatcher.dispatch(message, callbackManager)
    }

    /**
     * 执行 JS 代码
     */
    fun executeJs(script: String) {
        webView.post {
            webView.evaluateJavascript(script, null)
        }
    }

    /**
     * 向 JS 发送回调响应
     * JS 侧需定义 window.JSBridge.receiveMessage 方法
     */
    fun sendResponse(responseJson: String) {
        // 对 JSON 字符串进行处理，防止注入问题，这里简单包裹
        val script = "javascript:if(window.JSBridge && window.JSBridge.receiveMessage){window.JSBridge.receiveMessage('$responseJson')}"
        executeJs(script)
    }
}

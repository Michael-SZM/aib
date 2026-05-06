package com.icon.aibrowserasistor.hybrid.plugins

import com.icon.aibrowserasistor.hybrid.bridge.JsRequest

/**
 * 插件管理器，负责注册和分发插件调用
 */
class PluginManager {
    private val plugins = mutableMapOf<String, IPlugin>()

    init {
        registerDefaultPlugins()
    }

    private fun registerDefaultPlugins() {
        registerPlugin(CameraPlugin())
        registerPlugin(LoginPlugin())
        registerPlugin(PaymentPlugin())
    }

    fun registerPlugin(plugin: IPlugin) {
        plugins[plugin.name] = plugin
    }

    fun execute(request: JsRequest, callback: (String) -> Unit) {
        val plugin = plugins[request.method.split(".").firstOrNull()] 
        if (plugin != null) {
            plugin.execute(request, callback)
        } else {
            // 返回方法未找到错误
        }
    }
}

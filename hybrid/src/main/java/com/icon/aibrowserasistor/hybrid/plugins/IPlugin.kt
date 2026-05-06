package com.icon.aibrowserasistor.hybrid.plugins

import com.icon.aibrowserasistor.hybrid.bridge.JsRequest

/**
 * 插件接口定义
 */
interface IPlugin {
    val name: String
    fun execute(request: JsRequest, callback: (String) -> Unit)
}

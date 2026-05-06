package com.icon.aibrowserasistor.hybrid.plugins

import com.icon.aibrowserasistor.hybrid.bridge.JsRequest

class LoginPlugin : IPlugin {
    override val name: String = "login"

    override fun execute(request: JsRequest, callback: (String) -> Unit) {
        // 实现登录逻辑，可能涉及打开原生登录页
    }
}

package com.icon.aibrowserasistor.hybrid.plugins

import com.icon.aibrowserasistor.hybrid.bridge.JsRequest

class PaymentPlugin : IPlugin {
    override val name: String = "payment"

    override fun execute(request: JsRequest, callback: (String) -> Unit) {
        // 实现支付逻辑 (如 支付宝、微信支付等)
    }
}

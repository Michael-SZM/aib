package com.icon.aibrowserasistor.hybrid.bridge

/**
 * JSBridge 通信协议定义 (JSON-RPC 2.0 风格)
 */
data class JsRequest(
    val id: String,
    val method: String,
    val params: Map<String, Any>?
)

data class JsResponse(
    val id: String,
    val result: Any? = null,
    val error: JsError? = null
)

data class JsError(
    val code: Int,
    val message: String
)

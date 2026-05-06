package com.icon.aibrowserasistor.hybrid.plugins

import com.icon.aibrowserasistor.hybrid.bridge.JsRequest

class CameraPlugin : IPlugin {
    override val name: String = "camera"

    override fun execute(request: JsRequest, callback: (String) -> Unit) {
        // 实现拍照或选择相册逻辑
    }
}

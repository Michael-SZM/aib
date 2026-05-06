package com.icon.aibrowserasistor.hybrid.resource

/**
 * 离线包配置信息
 */
data class OfflineConfig(
    val hasUpdate: Boolean,
    val version: String,
    val url: String
)

/**
 * 离线包版本管理
 */
class VersionManager {
    fun check(callback: (OfflineConfig) -> Unit) {
        // 模拟检查更新，实际应请求后端接口
        val mockConfig = OfflineConfig(
            hasUpdate = true,
            version = "1.0.1",
            url = "https://example.com/offline_v101.zip"
        )
        callback(mockConfig)
    }
}

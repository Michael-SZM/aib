package com.icon.aibrowserasistor.hybrid.security

import android.net.Uri

/**
 * 安全管理：域名白名单及权限控制
 */
class SecurityManager {
    private val whiteList = mutableSetOf(
        "example.com",
        "static.example.com",
        "api.example.com"
    )

    fun isDomainAllowed(url: String): Boolean {
        val host = Uri.parse(url).host ?: return false
        return whiteList.any { host == it || host.endsWith(".$it") }
    }

    fun checkApiPermission(pluginName: String, methodName: String): Boolean {
        // 简单示例：所有注册插件默认允许，实际项目中可根据域名配置权限表
        return true
    }
}

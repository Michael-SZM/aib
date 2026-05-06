package com.icon.aibrowserasistor.hybrid.resource

import android.webkit.WebResourceResponse
import java.io.File
import java.io.FileInputStream
import java.net.URLConnection

/**
 * 本地资源映射缓存
 */
class ResourceCache {
    
    var localResourceDir: File? = null

    fun get(url: String): WebResourceResponse? {
        val path = mapUrlToLocalPath(url) ?: return null
        val file = File(localResourceDir, path)
        
        if (file.exists()) {
            val inputStream = FileInputStream(file)
            val mimeType = getMimeType(path)
            return WebResourceResponse(mimeType, "UTF-8", inputStream)
        }
        return null
    }

    private fun mapUrlToLocalPath(url: String): String? {
        // 示例：将 https://static.example.com/js/main.js 映射到本地 js/main.js
        if (url.startsWith("https://static.example.com/")) {
            return url.substring("https://static.example.com/".length)
        }
        return null
    }

    private fun getMimeType(path: String): String {
        return when {
            path.endsWith(".html") -> "text/html"
            path.endsWith(".css") -> "text/css"
            path.endsWith(".js") -> "application/javascript"
            path.endsWith(".png") -> "image/png"
            path.endsWith(".jpg") || path.endsWith(".jpeg") -> "image/jpeg"
            else -> URLConnection.guessContentTypeFromName(path) ?: "text/plain"
        }
    }
}

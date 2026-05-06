package com.icon.aibrowserasistor.hybrid.resource

import android.content.Context
import android.webkit.WebResourceResponse
import com.icon.aibrowserasistor.hybrid.monitor.Monitor
import com.icon.aibrowserasistor.hybrid.utils.ZipUtils
import java.io.File

/**
 * 离线包资源管理器
 */
class ResourceManager(private val context: Context) {
    private val versionManager = VersionManager()
    private val downloader = Downloader(context)
    private val cache = ResourceCache()

    init {
        cache.localResourceDir = File(context.filesDir, "hybrid_offline")
        if (!cache.localResourceDir!!.exists()) {
            cache.localResourceDir!!.mkdirs()
        }
    }

    fun shouldInterceptRequest(url: String): WebResourceResponse? {
        val response = cache.get(url)
        if (response != null) {
            Monitor.logInfo("Intercepted: $url")
        }
        return response
    }
    
    fun checkUpdate() {
        versionManager.check { config ->
            if (config.hasUpdate) {
                Monitor.logInfo("Found update: ${config.version}")
                downloader.download(config.url) { zipFile ->
                    if (zipFile != null && zipFile.exists()) {
                        val success = ZipUtils.unzip(zipFile, cache.localResourceDir!!)
                        if (success) {
                            Monitor.logInfo("Offline package update success: ${config.version}")
                            // 实际项目中这里还需要保存版本号到 SP/Database
                        } else {
                            Monitor.logError("OfflineUpdate", "Unzip failed")
                        }
                        zipFile.delete() // 删除临时文件
                    } else {
                        Monitor.logError("OfflineUpdate", "Download failed")
                    }
                }
            }
        }
    }
}

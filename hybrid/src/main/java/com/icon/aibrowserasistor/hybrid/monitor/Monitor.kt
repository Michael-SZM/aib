package com.icon.aibrowserasistor.hybrid.monitor

import android.util.Log

/**
 * 监控模块：性能监控、异常上报
 */
object Monitor {
    private const val TAG = "HybridMonitor"
    
    fun logPerformance(event: String, duration: Long) {
        Log.d(TAG, "Performance: $event took ${duration}ms")
        // 可以在此处接入数据统计 SDK (如 Firebase, 友盟等)
    }

    fun logError(type: String, message: String, stackTrace: String? = null) {
        Log.e(TAG, "Error [$type]: $message")
        stackTrace?.let { Log.e(TAG, it) }
        // 上报错误到后台
    }

    fun logInfo(message: String) {
        Log.i(TAG, message)
    }
}

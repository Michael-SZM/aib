package com.icon.aibrowserasistor.hybrid.pool

import android.annotation.SuppressLint
import android.content.Context
import android.view.ViewGroup
import android.webkit.WebView
import java.util.Stack

/**
 * WebView 复用池，支持预加载
 */
object WebViewPool {
    private val pool = Stack<WebView>()
    private const val MAX_POOL_SIZE = 3

    /**
     * 初始化预加载
     */
    fun init(context: Context) {
        prepareWebView(context)
    }

    private fun prepareWebView(context: Context) {
        if (pool.size < MAX_POOL_SIZE) {
            val webView = createWebView(context.applicationContext)
            pool.push(webView)
        }
    }

    @SuppressLint("SetJavaScriptEnabled")
    private fun createWebView(context: Context): WebView {
        return WebView(context).apply {
            settings.javaScriptEnabled = true
            settings.domStorageEnabled = true
            settings.databaseEnabled = true
            // 其他通用配置
        }
    }

    /**
     * 获取 WebView
     */
    fun getWebView(context: Context): WebView {
        val webView = if (pool.isNotEmpty()) {
            pool.pop()
        } else {
            createWebView(context.applicationContext)
        }
        // 预创建下一个，保持池子活跃
        prepareWebView(context)
        return webView
    }

    /**
     * 回收 WebView
     */
    fun recycle(webView: WebView) {
        // 释放资源
        webView.stopLoading()
        webView.loadUrl("about:blank")
        webView.clearHistory()
        webView.removeAllViews()
        
        val parent = webView.parent
        if (parent is ViewGroup) {
            parent.removeView(webView)
        }

        if (pool.size < MAX_POOL_SIZE) {
            pool.push(webView)
        } else {
            webView.destroy()
        }
    }
}

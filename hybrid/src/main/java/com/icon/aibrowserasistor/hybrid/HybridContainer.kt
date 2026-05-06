package com.icon.aibrowserasistor.hybrid

import android.content.Context
import android.util.AttributeSet
import android.webkit.WebResourceRequest
import android.webkit.WebResourceResponse
import android.webkit.WebView
import android.webkit.WebViewClient
import android.widget.FrameLayout
import com.icon.aibrowserasistor.hybrid.bridge.BridgeCore
import com.icon.aibrowserasistor.hybrid.pool.WebViewPool
import com.icon.aibrowserasistor.hybrid.resource.ResourceManager

/**
 * Hybrid 容器入口
 */
class HybridContainer @JvmOverloads constructor(
    context: Context,
    attrs: AttributeSet? = null,
    defStyleAttr: Int = 0
) : FrameLayout(context, attrs, defStyleAttr) {

    private val webView: WebView = WebViewPool.getWebView(context)
    private val bridge: BridgeCore = BridgeCore(webView)
    private val resourceManager = ResourceManager(context)

    init {
        setupWebView()
        addView(webView, LayoutParams(LayoutParams.MATCH_PARENT, LayoutParams.MATCH_PARENT))
    }

    private fun setupWebView() {
        webView.webViewClient = object : WebViewClient() {
            override fun shouldOverrideUrlLoading(
                view: WebView?,
                request: WebResourceRequest?
            ): Boolean {
                val url = request?.url?.toString() ?: return false
                if (bridge.handleUrl(url)) {
                    return true
                }
                return super.shouldOverrideUrlLoading(view, request)
            }

            override fun shouldInterceptRequest(
                view: WebView?,
                request: WebResourceRequest?
            ): WebResourceResponse? {
                val url = request?.url?.toString() ?: return null
                return resourceManager.shouldInterceptRequest(url)
            }
        }
    }

    fun loadUrl(url: String) {
        webView.loadUrl(url)
    }

    fun onDestroy() {
        WebViewPool.recycle(webView)
    }
}

package com.icon.aibrowserasistor.browser

import android.annotation.SuppressLint
import android.view.ViewGroup
import android.webkit.WebChromeClient
import android.webkit.WebSettings
import android.webkit.WebView
import android.webkit.WebViewClient
import androidx.compose.runtime.Composable
import androidx.compose.runtime.DisposableEffect
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.remember
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.viewinterop.AndroidView
import com.icon.aibrowserasistor.common.ToolCallResult
import kotlin.coroutines.resume
import kotlinx.coroutines.suspendCancellableCoroutine
import org.jsoup.Jsoup

class BrowserWebViewController(private val webView: WebView) {
    fun loadUrl(url: String) {
        webView.loadUrl(url)
    }

    fun goBack() {
        if (webView.canGoBack()) webView.goBack()
    }

    fun goForward() {
        if (webView.canGoForward()) webView.goForward()
    }
}

@SuppressLint("SetJavaScriptEnabled")
@Composable
fun BrowserWebView(
    modifier: Modifier = Modifier,
    url: String?,
    onCreated: (BrowserWebViewController) -> Unit,
    onPageStarted: (String?) -> Unit,
    onPageFinished: (String?, String?) -> Unit,
    onNavigationState: (Boolean, Boolean) -> Unit,
    onContent: (String) -> Unit
) {
    val context = LocalContext.current
    val webView = remember {
        WebView(context).apply {
            layoutParams = ViewGroup.LayoutParams(
                ViewGroup.LayoutParams.MATCH_PARENT,
                ViewGroup.LayoutParams.MATCH_PARENT
            )
            settings.javaScriptEnabled = true
            settings.domStorageEnabled = true
            settings.cacheMode = WebSettings.LOAD_DEFAULT
            settings.useWideViewPort = true
            settings.loadWithOverviewMode = true
            webChromeClient = WebChromeClient()
        }
    }

    DisposableEffect(Unit) {
        onCreated(BrowserWebViewController(webView))
        onDispose {
            webView.destroy()
        }
    }

    LaunchedEffect(url) {
        if (!url.isNullOrBlank()) {
            webView.loadUrl(url)
        }
    }

    AndroidView(
        modifier = modifier,
        factory = { webView },
        update = { view ->
            view.webViewClient = object : WebViewClient() {
                override fun onPageStarted(view: WebView?, url: String?, favicon: android.graphics.Bitmap?) {
                    super.onPageStarted(view, url, favicon)
                    onPageStarted(url)
                    view?.let { onNavigationState(it.canGoBack(), it.canGoForward()) }
                }

                override fun onPageFinished(view: WebView?, url: String?) {
                    super.onPageFinished(view, url)
                    onPageFinished(url, view?.title)
                    view?.let {
                        onNavigationState(it.canGoBack(), it.canGoForward())
                        captureContent(it, onContent)
                    }
                }
            }
        }
    )
}

private fun captureContent(webView: WebView, onContent: (String) -> Unit) {
    webView.evaluateJavascript(
        """(function() { return document.documentElement.outerHTML; })();"""
    ) { raw ->
        onContent(raw?.let { decodeJsString(it) } ?: "")
    }
}

private fun decodeJsString(raw: String): String {
    if (raw.length < 2) return raw
    val unquoted = raw.removePrefix("\"").removeSuffix("\"")
    val decoded = unquoted
        .replace("\\n", "\n")
        .replace("\\t", "\t")
        .replace("\\u003C", "<")
        .replace("\\\"", "\"")
    val docText = Jsoup.parse(decoded).text()
    return docText.replace(Regex("\\s+"), " ").trim()
}

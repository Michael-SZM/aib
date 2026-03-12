package com.icon.aibrowserasistor.tools.web

import android.webkit.WebView
import com.icon.aibrowserasistor.common.ToolCallResult
import kotlin.coroutines.resume
import kotlinx.coroutines.suspendCancellableCoroutine

enum class WebToolType {
    OPEN_URL,
    CLICK,
    TYPE,
    EXTRACT_TEXT
}

data class WebToolRequest(
    val type: WebToolType,
    val args: Map<String, String> = emptyMap(),
    val tabId: String? = null,
    val webView: WebView? = null
)

sealed class ToolResult {
    data class Success(val result: ToolCallResult) : ToolResult()
    data class Failure(val reason: String) : ToolResult()
}

interface WebTool {
    val type: WebToolType
    suspend fun execute(request: WebToolRequest): ToolResult
}

private suspend fun WebView.evaluateJsSuspend(script: String): String = suspendCancellableCoroutine { cont ->
    post {
        evaluateJavascript(script) { value ->
            cont.resume(value ?: "")
        }
    }
}

private fun decodeJs(raw: String): String {
    if (raw.length < 2) return raw
    return raw.removePrefix("\"").removeSuffix("\"")
        .replace("\\n", "\n")
        .replace("\\t", "\t")
        .replace("\\u003C", "<")
        .replace("\\\"", "\"")
}

class OpenUrlTool : WebTool {
    override val type: WebToolType = WebToolType.OPEN_URL

    override suspend fun execute(request: WebToolRequest): ToolResult {
        val webView = request.webView ?: return ToolResult.Failure("WebView not provided")
        val url = request.args["url"].orEmpty()
        if (url.isBlank()) return ToolResult.Failure("Missing url")
        webView.post { webView.loadUrl(url) }
        return ToolResult.Success(ToolCallResult(output = "Opened $url"))
    }
}

class ClickTool : WebTool {
    override val type: WebToolType = WebToolType.CLICK

    override suspend fun execute(request: WebToolRequest): ToolResult {
        val webView = request.webView ?: return ToolResult.Failure("WebView not provided")
        val selector = request.args["selector"].orEmpty()
        if (selector.isBlank()) return ToolResult.Failure("Missing selector")
        val js = """
            (function() {
                var el = document.querySelector(${selector.wrapForJs()});
                if (!el) return 'selector not found';
                el.click();
                return 'clicked';
            })();
        """.trimIndent()
        val result = decodeJs(webView.evaluateJsSuspend(js))
        return ToolResult.Success(ToolCallResult(output = result))
    }
}

class TypeTool : WebTool {
    override val type: WebToolType = WebToolType.TYPE

    override suspend fun execute(request: WebToolRequest): ToolResult {
        val webView = request.webView ?: return ToolResult.Failure("WebView not provided")
        val selector = request.args["selector"].orEmpty()
        val text = request.args["text"].orEmpty()
        if (selector.isBlank()) return ToolResult.Failure("Missing selector")
        val js = """
            (function() {
                var el = document.querySelector(${selector.wrapForJs()});
                if (!el) return 'selector not found';
                el.value = ${text.wrapForJs()};
                el.dispatchEvent(new Event('input', { bubbles: true }));
                el.dispatchEvent(new Event('change', { bubbles: true }));
                return 'typed';
            })();
        """.trimIndent()
        val result = decodeJs(webView.evaluateJsSuspend(js))
        return ToolResult.Success(ToolCallResult(output = result))
    }
}

class ExtractTextTool : WebTool {
    override val type: WebToolType = WebToolType.EXTRACT_TEXT

    override suspend fun execute(request: WebToolRequest): ToolResult {
        val webView = request.webView ?: return ToolResult.Failure("WebView not provided")
        val selector = request.args["selector"]
        val js = if (selector.isNullOrBlank()) {
            """
                (function() {
                    var body = document.body || document.documentElement;
                    return body ? body.innerText : '';
                })();
            """.trimIndent()
        } else {
            """
                (function() {
                    var el = document.querySelector(${selector.wrapForJs()});
                    if (!el) return '';
                    return el.innerText || el.textContent || '';
                })();
            """.trimIndent()
        }
        val raw = webView.evaluateJsSuspend(js)
        return ToolResult.Success(ToolCallResult(output = decodeJs(raw)))
    }
}

class WebToolRegistry(tools: List<WebTool>) {
    private val registry = tools.associateBy { it.type }

    fun get(type: WebToolType): WebTool? = registry[type]
}

class WebToolExecutor(private val registry: WebToolRegistry) {
    @Volatile
    var currentWebView: WebView? = null

    fun attach(webView: WebView) {
        currentWebView = webView
    }

    suspend fun execute(request: WebToolRequest): ToolResult {
        val tool = registry.get(request.type) ?: return ToolResult.Failure("Tool not registered")
        val enriched = if (request.webView != null) request else request.copy(webView = currentWebView)
        return tool.execute(enriched)
    }

    suspend fun dryRun(): String {
        return "Executed 0 real tools (stub)"
    }
}

private fun String.wrapForJs(): String = "'" + this.replace("'", "\\'") + "'"

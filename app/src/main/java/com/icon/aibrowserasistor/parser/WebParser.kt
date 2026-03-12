package com.icon.aibrowserasistor.parser

import android.os.Looper
import android.webkit.WebView
import java.util.concurrent.CountDownLatch
import kotlin.math.min

object WebParser {

    fun extractPageText(webView: WebView): String {
        if (Looper.myLooper() == Looper.getMainLooper()) {
            throw IllegalStateException("extractPageText must be called off the main thread")
        }
        val js = """
            (function() {
                function removeAll(selectors) {
                    selectors.forEach(function(sel) {
                        document.querySelectorAll(sel).forEach(function(el) { el.remove(); });
                    });
                }
                removeAll(['script','style','nav','header','footer','aside','noscript','iframe','form','button','input','select','option','textarea','svg','canvas','video','audio','picture','figure','img','.ads','.ad','.banner']);
                var text = document.body ? document.body.innerText : document.documentElement.innerText;
                return text || '';
            })();
        """.trimIndent()

        return blockingEvaluate(webView, js).let { cleanText(it) }
    }

    private fun blockingEvaluate(webView: WebView, script: String): String {
        var result = ""
        val latch = CountDownLatch(1)
        webView.post {
            webView.evaluateJavascript(script) { value ->
                result = decodeJsString(value)
                latch.countDown()
            }
        }
        latch.await()
        return result
    }

    private fun decodeJsString(raw: String?): String {
        if (raw.isNullOrEmpty()) return ""
        if (raw.length < 2) return raw
        val unquoted = raw.removePrefix("\"").removeSuffix("\"")
        return unquoted
            .replace("\\n", "\n")
            .replace("\\t", "\t")
            .replace("\\u003C", "<")
            .replace("\\\"", "\"")
    }

    private fun cleanText(text: String): String {
        val noHtmlEntities = text.replace("\r", "\n")
        val collapsed = noHtmlEntities.replace(Regex("\\n+"), "\n")
        return collapsed.replace(Regex("\\s+"), " ").trim()
    }

    fun chunkText(text: String, size: Int): List<String> {
        if (size <= 0) return emptyList()
        val normalized = text.replace(Regex("\\s+"), " ").trim()
        if (normalized.isBlank()) return emptyList()
        val chunks = mutableListOf<String>()
        var index = 0
        while (index < normalized.length) {
            val end = min(index + size, normalized.length)
            chunks.add(normalized.substring(index, end))
            index = end
        }
        return chunks
    }
}

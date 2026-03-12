现在实现 Web Parser 模块。

目标：

从 WebView 页面中提取网页正文内容。

实现：

1 执行 JavaScript 获取 DOM 文本
2 过滤导航栏、广告、脚本
3 输出 clean text

需要实现：

WebParser.kt

函数：

extractPageText(webView: WebView): String

另外实现：

chunkText(text: String, size: Int)

用于 RAG 切分文本。

代码要求：

Kotlin
可直接在 Android 使用。
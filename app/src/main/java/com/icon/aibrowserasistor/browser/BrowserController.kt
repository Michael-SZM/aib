package com.icon.aibrowserasistor.browser

import com.icon.aibrowserasistor.common.BrowserState
import com.icon.aibrowserasistor.tools.web.WebToolRequest
import com.icon.aibrowserasistor.tools.web.ToolResult
import kotlinx.coroutines.flow.StateFlow

interface BrowserController {
    val browserState: StateFlow<BrowserState>

    suspend fun newTab(initialUrl: String? = null)

    suspend fun openUrl(url: String, tabId: String? = null)

    suspend fun closeTab(tabId: String)

    suspend fun executeTool(request: WebToolRequest): ToolResult
}

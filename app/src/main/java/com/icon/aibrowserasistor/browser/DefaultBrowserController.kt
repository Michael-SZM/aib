package com.icon.aibrowserasistor.browser

import com.icon.aibrowserasistor.common.BrowserState
import com.icon.aibrowserasistor.common.TabState
import com.icon.aibrowserasistor.tools.web.WebToolExecutor
import com.icon.aibrowserasistor.tools.web.WebToolRequest
import com.icon.aibrowserasistor.tools.web.ToolResult
import java.util.UUID
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.update

class DefaultBrowserController(
    private val toolExecutor: WebToolExecutor
) : BrowserController {

    private val mutableState = MutableStateFlow(initialState())
    override val browserState: StateFlow<BrowserState> = mutableState

    override suspend fun newTab(initialUrl: String?) {
        val tabId = UUID.randomUUID().toString()
        mutableState.update { current ->
            val newTab = TabState(id = tabId, url = initialUrl, title = initialUrl)
            val tabs = current.tabs + newTab
            current.copy(tabs = tabs, activeTabId = tabId)
        }
        if (!initialUrl.isNullOrBlank()) {
            openUrl(initialUrl, tabId)
        }
    }

    override suspend fun openUrl(url: String, tabId: String?) {
        val activeId = tabId ?: mutableState.value.activeTabId
        if (activeId == null) {
            newTab(url)
            return
        }
        mutableState.update { current ->
            val updatedTabs = current.tabs.map { tab ->
                if (tab.id == activeId) {
                    tab.copy(url = url, title = url)
                } else {
                    tab
                }
            }
            current.copy(tabs = updatedTabs, activeTabId = activeId)
        }
    }

    override suspend fun closeTab(tabId: String) {
        mutableState.update { current ->
            val remaining = current.tabs.filterNot { it.id == tabId }
            val activeId = if (current.activeTabId == tabId) {
                remaining.lastOrNull()?.id
            } else {
                current.activeTabId
            }
            current.copy(tabs = remaining, activeTabId = activeId)
        }
    }

    override suspend fun executeTool(request: WebToolRequest): ToolResult {
        return toolExecutor.execute(request)
    }

    private fun initialState(): BrowserState {
        val firstTabId = UUID.randomUUID().toString()
        val tabs = listOf(TabState(id = firstTabId, title = "New Tab"))
        return BrowserState(tabs = tabs, activeTabId = firstTabId)
    }
}

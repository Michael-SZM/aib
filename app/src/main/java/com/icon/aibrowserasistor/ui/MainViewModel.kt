package com.icon.aibrowserasistor.ui

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.icon.aibrowserasistor.AppDependencies
import com.icon.aibrowserasistor.common.AgentTask
import com.icon.aibrowserasistor.common.BrowserState
import com.icon.aibrowserasistor.common.ToolCallResult
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.SharingStarted
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.collect
import kotlinx.coroutines.flow.combine
import kotlinx.coroutines.flow.stateIn
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.launch

data class AiPanelState(
    val generatedText: String = "",
    val isRunning: Boolean = false
)

data class MainUiState(
    val browserState: BrowserState = BrowserState(),
    val aiPanelState: AiPanelState = AiPanelState(),
    val lastToolResult: ToolCallResult? = null
)

class MainViewModel(
    private val dependencies: AppDependencies = AppDependencies()
) : ViewModel() {

    private val aiPanel = MutableStateFlow(AiPanelState())
    private val toolResult = MutableStateFlow<ToolCallResult?>(null)

    private val browserFlow = dependencies.browserController.browserState

    val uiState: StateFlow<MainUiState> = combine(browserFlow, aiPanel, toolResult) { browser, ai, tool ->
        MainUiState(browserState = browser, aiPanelState = ai, lastToolResult = tool)
    }.stateIn(
        scope = viewModelScope,
        started = SharingStarted.WhileSubscribed(5_000),
        initialValue = MainUiState(browserState = browserFlow.value)
    )

    fun newTab(url: String?) {
        viewModelScope.launch {
            dependencies.browserController.newTab(url.takeIf { !it.isNullOrBlank() })
        }
    }

    fun openUrl(url: String) {
        if (url.isBlank()) return
        viewModelScope.launch {
            dependencies.browserController.openUrl(url)
        }
    }

    fun askQuestion(question: String) {
        if (question.isBlank()) return
        val task = AgentTask(goal = question, contextUrl = activeTabUrl())
        viewModelScope.launch {
            aiPanel.update { it.copy(isRunning = true, generatedText = "") }
            dependencies.agentExecutor.run(task).collect { chunk ->
                aiPanel.update { current ->
                    val combined = listOf(current.generatedText, chunk.text).filter { it.isNotBlank() }.joinToString("\n")
                    current.copy(generatedText = combined, isRunning = !chunk.isFinal)
                }
            }
        }
    }

    fun runWebTool(description: String) {
        val result = ToolCallResult(output = description)
        toolResult.value = result
    }

    private fun activeTabUrl(): String? {
        val activeId = browserFlow.value.activeTabId ?: return null
        return browserFlow.value.tabs.firstOrNull { it.id == activeId }?.url
    }
}

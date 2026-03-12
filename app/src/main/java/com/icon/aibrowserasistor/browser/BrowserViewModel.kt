package com.icon.aibrowserasistor.browser

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.launch

data class BrowserUiState(
    val urlInput: String = "https://www.example.com",
    val currentUrl: String? = null,
    val pageTitle: String? = null,
    val canGoBack: Boolean = false,
    val canGoForward: Boolean = false,
    val isLoading: Boolean = false,
    val pageContent: String = ""
)

class BrowserViewModel : ViewModel() {

    private val _uiState = MutableStateFlow(BrowserUiState())
    val uiState: StateFlow<BrowserUiState> = _uiState.asStateFlow()

    fun onUrlInputChange(input: String) {
        _uiState.update { it.copy(urlInput = input) }
    }

    fun loadRequestedUrl() {
        val url = normalizeUrl(_uiState.value.urlInput)
        _uiState.update { it.copy(currentUrl = url, isLoading = true) }
    }

    fun onPageStarted(url: String?) {
        _uiState.update { current ->
            current.copy(
                isLoading = true,
                currentUrl = url ?: current.currentUrl,
                urlInput = url ?: current.urlInput
            )
        }
    }

    fun onPageFinished(url: String?, title: String?) {
        _uiState.update { current ->
            current.copy(
                isLoading = false,
                currentUrl = url ?: current.currentUrl,
                urlInput = url ?: current.urlInput,
                pageTitle = title ?: current.pageTitle
            )
        }
    }

    fun onNavigationState(canGoBack: Boolean, canGoForward: Boolean) {
        _uiState.update { it.copy(canGoBack = canGoBack, canGoForward = canGoForward) }
    }

    fun onContentCaptured(html: String) {
        _uiState.update { it.copy(pageContent = html) }
    }

    fun goBack() {
        viewModelScope.launch {
            // The actual navigation is handled in the WebView controller; we only flip loading flag for UI.
            _uiState.update { it.copy(isLoading = true) }
        }
    }

    fun goForward() {
        viewModelScope.launch {
            _uiState.update { it.copy(isLoading = true) }
        }
    }

    private fun normalizeUrl(input: String): String {
        val trimmed = input.trim()
        if (trimmed.startsWith("http://") || trimmed.startsWith("https://")) return trimmed
        return "https://$trimmed"
    }
}

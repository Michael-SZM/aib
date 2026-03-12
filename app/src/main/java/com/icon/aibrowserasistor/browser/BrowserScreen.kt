package com.icon.aibrowserasistor.browser

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.ArrowBack
import androidx.compose.material.icons.filled.ArrowForward
import androidx.compose.material3.Button
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.Text
import androidx.compose.material3.TopAppBar
import androidx.compose.material3.Scaffold
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.lifecycle.viewmodel.compose.viewModel

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun BrowserScreen(viewModel: BrowserViewModel = viewModel()) {
    val state by viewModel.uiState.collectAsState()
    val controllerState = remember { mutableStateOf<BrowserWebViewController?>(null) }

    LaunchedEffect(Unit) {
        if (state.currentUrl == null) {
            viewModel.loadRequestedUrl()
        }
    }

    Scaffold(
        topBar = {
            TopAppBar(
                title = {
                    Text(text = state.pageTitle ?: state.currentUrl ?: "AI Browser Assistant")
                },
                actions = {
                    if (state.isLoading) {
                        CircularProgressIndicator(
                            modifier = Modifier
                                .padding(end = 16.dp)
                                .size(20.dp),
                            strokeWidth = 2.dp
                        )
                    }
                }
            )
        }
    ) { padding ->
        Column(
            modifier = Modifier
                .fillMaxSize()
                .padding(padding)
                .padding(12.dp),
            verticalArrangement = Arrangement.spacedBy(12.dp)
        ) {
            UrlControls(
                url = state.urlInput,
                canGoBack = state.canGoBack,
                canGoForward = state.canGoForward,
                onUrlChange = viewModel::onUrlInputChange,
                onGo = {
                    viewModel.loadRequestedUrl()
                },
                onBack = {
                    controllerState.value?.goBack()
                    viewModel.goBack()
                },
                onForward = {
                    controllerState.value?.goForward()
                    viewModel.goForward()
                }
            )

            LazyColumn(
                modifier = Modifier
                    .weight(1f)
                    .fillMaxWidth(),
                verticalArrangement = Arrangement.spacedBy(12.dp)
            ) {
                item {
                    BrowserWebView(
                        modifier = Modifier
                            .fillMaxWidth()
                            .height(420.dp),
                        url = state.currentUrl,
                        onCreated = { controllerState.value = it },
                        onPageStarted = viewModel::onPageStarted,
                        onPageFinished = viewModel::onPageFinished,
                        onNavigationState = viewModel::onNavigationState,
                        onContent = viewModel::onContentCaptured
                    )
                }
                item {
                    PageContentPreview(content = state.pageContent)
                }
            }
        }
    }
}

@Composable
private fun UrlControls(
    url: String,
    canGoBack: Boolean,
    canGoForward: Boolean,
    onUrlChange: (String) -> Unit,
    onGo: () -> Unit,
    onBack: () -> Unit,
    onForward: () -> Unit
) {
    Column(verticalArrangement = Arrangement.spacedBy(8.dp)) {
        OutlinedTextField(
            modifier = Modifier.fillMaxWidth(),
            value = url,
            onValueChange = onUrlChange,
            label = { Text("输入或粘贴 URL") },
            maxLines = 1,
            singleLine = true
        )
        Row(verticalAlignment = Alignment.CenterVertically, horizontalArrangement = Arrangement.spacedBy(8.dp)) {
            IconButton(onClick = onBack, enabled = canGoBack) {
                Icon(Icons.Default.ArrowBack, contentDescription = "后退")
            }
            IconButton(onClick = onForward, enabled = canGoForward) {
                Icon(Icons.Default.ArrowForward, contentDescription = "前进")
            }
            Button(onClick = onGo) {
                Text(text = "前往")
            }
        }
    }
}

@Composable
private fun PageContentPreview(content: String) {
    Column(verticalArrangement = Arrangement.spacedBy(4.dp)) {
        Text(text = "网页内容 (提供给 AI)", fontWeight = FontWeight.Bold)
        Text(text = if (content.isBlank()) "内容抓取中..." else content)
    }
}

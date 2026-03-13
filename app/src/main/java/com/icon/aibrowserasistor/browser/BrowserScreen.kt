package com.icon.aibrowserasistor.browser

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Add
import androidx.compose.material.icons.filled.ArrowBack
import androidx.compose.material.icons.filled.ArrowForward
import androidx.compose.material.icons.filled.Close
import androidx.compose.material3.Button
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.material3.HorizontalDivider
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.LinearProgressIndicator
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.Text
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.Surface
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateListOf
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.rememberCoroutineScope
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.Dp
import androidx.compose.ui.unit.dp
import androidx.lifecycle.viewmodel.compose.viewModel
import androidx.compose.foundation.layout.BoxScope
import androidx.compose.foundation.layout.offset
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.material.icons.filled.Edit
import androidx.compose.material.icons.filled.Mic
import androidx.compose.material.icons.filled.QuestionAnswer
import androidx.compose.material.icons.filled.Summarize
import androidx.compose.material3.FloatingActionButton
import androidx.compose.foundation.text.KeyboardActions
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.ui.text.input.ImeAction
import androidx.compose.ui.text.input.KeyboardType
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.verticalScroll
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.ui.window.Dialog
import android.webkit.WebView
import com.icon.aibrowserasistor.ai.service.PageSummaryService
import com.icon.aibrowserasistor.ai.service.AskPageService
import com.icon.aibrowserasistor.AppDependencies
import kotlinx.coroutines.launch
import kotlinx.coroutines.delay
import androidx.compose.animation.core.tween
import androidx.compose.foundation.gestures.animateScrollBy
import kotlin.math.PI
import kotlin.math.cos
import kotlin.math.sin

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun BrowserScreen(viewModel: BrowserViewModel = viewModel()) {
    val state by viewModel.uiState.collectAsState()
    val controllerState = remember { mutableStateOf<BrowserWebViewController?>(null) }
    val fabExpanded = remember { mutableStateOf(false) }
    val webViewRef = remember { mutableStateOf<WebView?>(null) }
    val summaryText = remember { mutableStateOf("") }
    val showSummaryDialog = remember { mutableStateOf(false) }
    val showQADialog = remember { mutableStateOf(false) }
    val qaInput = remember { mutableStateOf("") }
    val qaMessages = remember { mutableStateListOf<Pair<String, String>>() }
    val qaLoading = remember { mutableStateOf(false) }
    val qaScrollState = rememberScrollState()
    val deps = remember { AppDependencies() }
    val scope = rememberCoroutineScope()

    LaunchedEffect(Unit) {
        if (state.currentUrl == null) {
            viewModel.loadRequestedUrl()
        }
    }

    Box(modifier = Modifier.fillMaxSize()) {
        Column(
            modifier = Modifier
                .fillMaxSize()
                .padding(12.dp),
            verticalArrangement = Arrangement.spacedBy(12.dp)
        ) {
            UrlControls(
                url = state.urlInput,
                onUrlChange = viewModel::onUrlInputChange,
                onGo = { viewModel.loadRequestedUrl() },
                isLoading = state.isLoading
            )

            HorizontalDivider()

            Box(modifier = Modifier.weight(1f)) {
                BrowserWebView(
                    modifier = Modifier.fillMaxSize(),
                    url = state.currentUrl,
                    onCreated = { controllerState.value = it },
                    onWebViewReady = { webViewRef.value = it },
                    onPageStarted = viewModel::onPageStarted,
                    onPageFinished = viewModel::onPageFinished,
                    onNavigationState = viewModel::onNavigationState,
                    onContent = viewModel::onContentCaptured
                )
            }

            NavigationBar(
                canGoBack = state.canGoBack,
                canGoForward = state.canGoForward,
                onBack = {
                    controllerState.value?.goBack()
                    viewModel.goBack()
                },
                onForward = {
                    controllerState.value?.goForward()
                    viewModel.goForward()
                },
                onMore = { fabExpanded.value = !fabExpanded.value }
            )
        }

        RadialFabMenu(
            expanded = fabExpanded.value,
            onToggle = { fabExpanded.value = !fabExpanded.value },
            onAction1 = {
                val view = webViewRef.value ?: return@RadialFabMenu
                scope.launch {
                    summaryText.value = "总结中..."
                    val service = PageSummaryService(view, deps.ai())
                    summaryText.value = service.summarizePage(state.pageContent)
                    showSummaryDialog.value = true
                }
            },
            onAction2 = {
                if (qaMessages.isEmpty()) {
                    qaMessages.add("system" to "欢迎来到问答模块，请输入您的问题。")
                }
                showQADialog.value = true
            },
            onAction3 = { /* TODO: action 3 */ }
        )

        QADialog(
            visible = showQADialog.value,
            messages = qaMessages,
            input = qaInput.value,
            onInputChange = { qaInput.value = it },
            loading = qaLoading.value,
            scrollState = qaScrollState,
            onSend = {
                val view = webViewRef.value ?: return@QADialog
                val question = qaInput.value.trim()
                if (question.isBlank()) return@QADialog
                qaMessages.add("user" to question)
                qaInput.value = ""
                scope.launch {
                    qaLoading.value = true
                    val service = AskPageService(view, deps.rag(), deps.ai())
                    val answer = service.askPage(state.pageContent,question)
                    qaMessages.add("assistant" to answer)
                    qaScrollState.scrollTo(qaScrollState.maxValue)
                    qaLoading.value = false
                }
            },
            onDismiss = { showQADialog.value = false }
        )

        if (showSummaryDialog.value) {
            Dialog(onDismissRequest = { showSummaryDialog.value = false }) {
                Surface(
                    modifier = Modifier
                        .padding(horizontal = 16.dp, vertical = 50.dp)
                        .fillMaxWidth(),
                    shape = RoundedCornerShape(12.dp),
                    color = MaterialTheme.colorScheme.surface
                ) {
                    Column(
                        modifier = Modifier
                            .padding(16.dp)
                            .verticalScroll(rememberScrollState()),
                        verticalArrangement = Arrangement.spacedBy(12.dp)
                    ) {
                        Text(text = "AI 总结", fontWeight = FontWeight.Bold)
                        Text(text = summaryText.value)
                        Row(horizontalArrangement = Arrangement.End, modifier = Modifier.fillMaxWidth()) {
                            Button(onClick = { showSummaryDialog.value = false }) {
                                Text("关闭")
                            }
                        }
                    }
                }
            }
        }
    }
}

@Composable
private fun UrlControls(
    url: String,
    onUrlChange: (String) -> Unit,
    onGo: () -> Unit,
    isLoading: Boolean
) {
    Column(verticalArrangement = Arrangement.spacedBy(8.dp)) {
        Row(verticalAlignment = Alignment.CenterVertically, horizontalArrangement = Arrangement.spacedBy(8.dp)) {
            OutlinedTextField(
                modifier = Modifier.weight(1f),
                value = url,
                onValueChange = onUrlChange,
                label = { Text("输入或粘贴 URL") },
                maxLines = 1,
                singleLine = true
            )
            Button(onClick = onGo) {
                Text(text = "搜索")
            }
        }
        if (isLoading) {
            LinearProgressIndicator(
                modifier = Modifier.fillMaxWidth(),
                progress = 0.4f
            )
        }
    }
}

@Composable
private fun PageContentPreview(content: String, summary: String) {
    Column(verticalArrangement = Arrangement.spacedBy(8.dp)) {
        Text(text = if (content.isBlank()) "内容抓取中..." else content)
        if (summary.isNotBlank()) {
            HorizontalDivider()
            Text(text = "AI 总结：")
            Text(text = summary)
        }
    }
}

@Composable
private fun NavigationBar(
    canGoBack: Boolean,
    canGoForward: Boolean,
    onBack: () -> Unit,
    onForward: () -> Unit,
    onMore: () -> Unit
) {
    Row(
        modifier = Modifier.fillMaxWidth(),
        horizontalArrangement = Arrangement.SpaceBetween,
        verticalAlignment = Alignment.CenterVertically
    ) {
        Row(horizontalArrangement = Arrangement.spacedBy(12.dp), verticalAlignment = Alignment.CenterVertically) {
            IconButton(onClick = onBack, enabled = canGoBack) {
                Icon(Icons.Default.ArrowBack, contentDescription = "后退")
            }
            IconButton(onClick = onForward, enabled = canGoForward) {
                Icon(Icons.Default.ArrowForward, contentDescription = "前进")
            }
        }
        Row(horizontalArrangement = Arrangement.spacedBy(8.dp), verticalAlignment = Alignment.CenterVertically) {
            Spacer(modifier = Modifier.width(12.dp))
            IconButton(onClick = onMore) {
                Icon(Icons.Default.Add, contentDescription = "展开工具面板")
            }
        }
    }
}

@Composable
private fun RadialFabMenu(
    expanded: Boolean,
    onToggle: () -> Unit,
    onAction1: () -> Unit,
    onAction2: () -> Unit,
    onAction3: () -> Unit
) {
    val angles = listOf(20.0, 50.0, 80.0)
    val radius = 120.dp
    Box(modifier = Modifier.fillMaxSize()) {
        if (expanded) {
            angles.forEachIndexed { index, angleDeg ->
                val angleRad = angleDeg * PI / 180.0
                val offsetX = -(radius.value * cos(angleRad)).dp
                val offsetY = -(radius.value * sin(angleRad)).dp
                val action = when (index) {
                    0 -> onAction1
                    1 -> onAction2
                    else -> onAction3
                }
                val icon = when (index) {
                    0 -> Icons.Default.Mic
                    1 -> Icons.Default.QuestionAnswer
                    else -> Icons.Default.Summarize
                }
                RadialActionButton(offsetX = offsetX, offsetY = offsetY, icon = icon, onClick = action)
            }
        }
        FloatingActionButton(
            modifier = Modifier
                .align(Alignment.BottomEnd)
                .padding(end = 16.dp, bottom = 20.dp),
            onClick = onToggle,
            shape = CircleShape
        ) {
            Icon(if (expanded) Icons.Default.Close else Icons.Default.Add, contentDescription = "工具菜单")
        }
    }
}

@Composable
private fun QADialog(
    visible: Boolean,
    messages: List<Pair<String, String>>,
    input: String,
    onInputChange: (String) -> Unit,
    onSend: () -> Unit,
    onDismiss: () -> Unit,
    loading: Boolean,
    scrollState: androidx.compose.foundation.ScrollState
) {
    if (!visible) return
    Dialog(onDismissRequest = onDismiss) {
        Surface(
            modifier = Modifier
                .padding(horizontal = 16.dp, vertical = 50.dp)
                .fillMaxWidth(),
            shape = RoundedCornerShape(12.dp),
            color = MaterialTheme.colorScheme.surface
        ) {
            Column(
                modifier = Modifier
                    .padding(16.dp),
                verticalArrangement = Arrangement.spacedBy(12.dp)
            ) {
                LaunchedEffect(messages.size) {
                    val delta = scrollState.maxValue - scrollState.value
                    if (delta > 0) {
                        scrollState.animateScrollBy(delta.toFloat())
                    }
                }
                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.SpaceBetween,
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    Text(text = "网页问答", fontWeight = FontWeight.Bold)
                    IconButton(onClick = onDismiss) {
                        Icon(Icons.Default.Close, contentDescription = "关闭")
                    }
                }
                Column(
                    modifier = Modifier
                        .weight(1f, fill = false)
                        .verticalScroll(scrollState),
                    verticalArrangement = Arrangement.spacedBy(8.dp)
                ) {
                    messages.forEach { (role, text) ->
                        val alignment = if (role == "user") Alignment.End else Alignment.Start
                        Row(modifier = Modifier.fillMaxWidth(), horizontalArrangement = if (role == "user") Arrangement.End else Arrangement.Start) {
                            Surface(
                                color = if (role == "user") MaterialTheme.colorScheme.primary.copy(alpha = 0.1f) else MaterialTheme.colorScheme.surfaceVariant,
                                shape = RoundedCornerShape(12.dp)
                            ) {
                                Text(
                                    text = text,
                                    modifier = Modifier.padding(12.dp)
                                )
                            }
                        }
                    }
                }
                Row(
                    verticalAlignment = Alignment.CenterVertically,
                    horizontalArrangement = Arrangement.spacedBy(8.dp),
                    modifier = Modifier.fillMaxWidth()
                ) {
                    OutlinedTextField(
                        modifier = Modifier.weight(1f),
                        value = input,
                        onValueChange = onInputChange,
                        singleLine = true,
                        label = { Text("请输入问题") },
                        keyboardOptions = KeyboardOptions(
                            imeAction = ImeAction.Send,
                            keyboardType = KeyboardType.Text
                        ),
                        keyboardActions = KeyboardActions(
                            onSend = { onSend() }
                        )
                    )
                    Button(onClick = onSend, enabled = input.isNotBlank() && !loading) {
                        Text(if (loading) "发送中..." else "发送")
                    }
                }
            }
        }
    }
}

@Composable
private fun BoxScope.RadialActionButton(offsetX: Dp, offsetY: Dp, icon: ImageVector, onClick: () -> Unit) {
    FloatingActionButton(
        modifier = Modifier
            .align(Alignment.BottomEnd)
            .offset(x = offsetX, y = offsetY)
            .size(48.dp),
        onClick = onClick,
        shape = CircleShape
    ) {
        Icon(icon, contentDescription = null)
    }
}

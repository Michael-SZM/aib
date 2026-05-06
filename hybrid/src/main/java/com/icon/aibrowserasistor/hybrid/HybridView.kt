package com.icon.aibrowserasistor.hybrid

import androidx.compose.runtime.Composable
import androidx.compose.runtime.DisposableEffect
import androidx.compose.runtime.remember
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.viewinterop.AndroidView

/**
 * Compose 版本的 Hybrid 容器
 */
@Composable
fun HybridView(
    url: String,
    modifier: Modifier = Modifier,
    onCreated: (HybridContainer) -> Unit = {}
) {
    val context = LocalContext.current
    val hybridContainer = remember { HybridContainer(context) }

    DisposableEffect(hybridContainer) {
        onCreated(hybridContainer)
        onDispose {
            hybridContainer.onDestroy()
        }
    }

    AndroidView(
        factory = { hybridContainer.apply { loadUrl(url) } },
        modifier = modifier
    )
}

package com.icon.aibrowserasistor

import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import com.icon.aibrowserasistor.ui.theme.AIBrowserAsistorTheme
import com.icon.aibrowserasistor.ui.AiBrowserApp
import com.icon.aibrowserasistor.ui.SplashScreen
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.getValue
import androidx.compose.runtime.setValue
import androidx.compose.runtime.LaunchedEffect
import kotlinx.coroutines.delay

class MainActivity : ComponentActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContent {
            AIBrowserAsistorTheme {
                var showSplash by remember { mutableStateOf(true) }
                LaunchedEffect(Unit) {
                    delay(1500)
                    showSplash = false
                }
                if (showSplash) {
                    SplashScreen()
                } else {
                    AiBrowserApp()
                }
            }
        }
    }
}

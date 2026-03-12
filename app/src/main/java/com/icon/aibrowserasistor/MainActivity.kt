package com.icon.aibrowserasistor

import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import com.icon.aibrowserasistor.ui.theme.AIBrowserAsistorTheme
import com.icon.aibrowserasistor.ui.AiBrowserApp

class MainActivity : ComponentActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContent {
            AIBrowserAsistorTheme {
                AiBrowserApp()
            }
        }
    }
}

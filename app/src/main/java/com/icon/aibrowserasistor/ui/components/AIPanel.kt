package com.icon.aibrowserasistor.ui.components

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.material3.Button
import androidx.compose.material3.Card
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.ui.Modifier
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp

data class ChatMessageUi(
    val role: String,
    val text: String
)

@Composable
fun AISidePanel(
    modifier: Modifier = Modifier,
    messages: List<ChatMessageUi>,
    isRunning: Boolean,
    onSummarize: () -> Unit,
    onAsk: (String) -> Unit
) {
    Column(
        modifier = modifier,
        verticalArrangement = Arrangement.spacedBy(12.dp)
    ) {
        Row(horizontalArrangement = Arrangement.spacedBy(8.dp)) {
            Button(onClick = onSummarize) {
                Text(text = if (isRunning) "Summarizing..." else "总结网页")
            }
        }

        AskInput(onAsk = onAsk, isRunning = isRunning)

        Card(modifier = Modifier.fillMaxWidth()) {
            Column(modifier = Modifier.padding(12.dp), verticalArrangement = Arrangement.spacedBy(8.dp)) {
                Text(text = "AI 输出", style = MaterialTheme.typography.titleMedium)
                ChatMessageList(messages = messages)
            }
        }
    }
}

@Composable
fun ChatMessageList(messages: List<ChatMessageUi>) {
    LazyColumn(verticalArrangement = Arrangement.spacedBy(6.dp)) {
        items(messages) { msg ->
            Column(verticalArrangement = Arrangement.spacedBy(2.dp)) {
                Text(text = msg.role.uppercase(), fontWeight = FontWeight.Bold, style = MaterialTheme.typography.labelMedium)
                Text(text = msg.text, style = MaterialTheme.typography.bodyMedium)
            }
        }
    }
}

@Composable
fun AskInput(onAsk: (String) -> Unit, isRunning: Boolean) {
    val textState = remember { mutableStateOf("") }
    Column(verticalArrangement = Arrangement.spacedBy(8.dp)) {
        OutlinedTextField(
            modifier = Modifier.fillMaxWidth(),
            value = textState.value,
            onValueChange = { textState.value = it },
            label = { Text("问网页") },
            enabled = !isRunning
        )
        Button(onClick = {
            val input = textState.value.trim()
            if (input.isNotEmpty()) {
                onAsk(input)
            }
        }, enabled = !isRunning) {
            Text(text = if (isRunning) "运行中..." else "提问")
        }
    }
}

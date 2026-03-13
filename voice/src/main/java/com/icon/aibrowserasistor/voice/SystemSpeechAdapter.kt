package com.icon.aibrowserasistor.voice

import android.content.Context
import android.content.Intent
import android.os.Bundle
import android.speech.RecognitionListener
import android.speech.RecognizerIntent
import android.speech.SpeechRecognizer
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.Job
import kotlinx.coroutines.cancel
import kotlinx.coroutines.launch

/**
 * Default speech adapter using Android SpeechRecognizer (no external deps).
 * Host app must handle RECORD_AUDIO permission before startListening.
 */
class SystemSpeechAdapter(
    private val context: Context,
    private val scope: CoroutineScope = CoroutineScope(Dispatchers.Main)
) : SpeechAdapter {

    private var recognizer: SpeechRecognizer? = null
    private var listenJob: Job? = null

    override fun startListening(onResult: (SpeechResult) -> Unit) {
        stopListening()
        recognizer = SpeechRecognizer.createSpeechRecognizer(context).apply {
            setRecognitionListener(object : RecognitionListener {
                override fun onReadyForSpeech(params: Bundle?) {
                    onResult(SpeechResult("开始听写...", isFinal = false))
                }

                override fun onResults(results: Bundle?) {
                    val texts = results?.getStringArrayList(SpeechRecognizer.RESULTS_RECOGNITION)
                    val text = texts?.firstOrNull().orEmpty()
                    onResult(SpeechResult(text, isFinal = true))
                }

                override fun onPartialResults(partialResults: Bundle?) {
                    val texts = partialResults?.getStringArrayList(SpeechRecognizer.RESULTS_RECOGNITION)
                    val text = texts?.firstOrNull().orEmpty()
                    onResult(SpeechResult(text, isFinal = false))
                }

                override fun onError(error: Int) {
                    onResult(SpeechResult("语音识别错误: $error", isFinal = true))
                }

                override fun onBeginningOfSpeech() {}
                override fun onBufferReceived(buffer: ByteArray?) {}
                override fun onEndOfSpeech() {}
                override fun onEvent(eventType: Int, params: Bundle?) {}
                override fun onRmsChanged(rmsdB: Float) {}
            })
        }

        val intent = Intent(RecognizerIntent.ACTION_RECOGNIZE_SPEECH).apply {
            putExtra(RecognizerIntent.EXTRA_LANGUAGE_MODEL, RecognizerIntent.LANGUAGE_MODEL_FREE_FORM)
            putExtra(RecognizerIntent.EXTRA_PARTIAL_RESULTS, true)
        }

        listenJob = scope.launch {
            recognizer?.startListening(intent)
        }
    }

    override fun stopListening() {
        listenJob?.cancel()
        listenJob = null
        recognizer?.stopListening()
    }

    override fun release() {
        stopListening()
        recognizer?.destroy()
        recognizer = null
        scope.cancel()
    }
}

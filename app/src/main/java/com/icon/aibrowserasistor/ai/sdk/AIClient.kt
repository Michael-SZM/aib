package com.icon.aibrowserasistor.ai.sdk

import com.google.gson.Gson
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.flow
import kotlinx.coroutines.flow.flowOn
import okhttp3.Interceptor
import okhttp3.OkHttpClient
import okhttp3.logging.HttpLoggingInterceptor
import retrofit2.Retrofit
import retrofit2.converter.gson.GsonConverterFactory

open class AIClient(
    private val provider: AIProvider,
    protected val promptManager: PromptManager = PromptManager()
) {

    private val api: AIApi
    private val gson = Gson()

    init {
        val authInterceptor = Interceptor { chain ->
            val request = chain.request().newBuilder()
                .addHeader("Authorization", "Bearer ${provider.apiKey}")
                .build()
            chain.proceed(request)
        }

        val logging = HttpLoggingInterceptor().apply {
            level = HttpLoggingInterceptor.Level.BASIC
        }

        val client = OkHttpClient.Builder()
            .addInterceptor(authInterceptor)
            .addInterceptor(logging)
            .build()

        val retrofit = Retrofit.Builder()
            .baseUrl(provider.baseUrl)
            .client(client)
            .addConverterFactory(GsonConverterFactory.create())
            .build()

        api = retrofit.create(AIApi::class.java)
    }

    open suspend fun chat(messages: List<ChatMessage>, model: String? = null): ChatResponse {
        val request = ChatRequest(
            model = model ?: provider.defaultModel,
            messages = messages,
            stream = false
        )
        return api.chat(request)
    }

    open fun streamChat(messages: List<ChatMessage>, model: String? = null): Flow<ChatStreamChunk> {
        return flow {
            val request = ChatRequest(
                model = model ?: provider.defaultModel,
                messages = messages,
                stream = true
            )
            val body = api.streamChat(request)
            body.use { responseBody ->
                responseBody.source().use { source ->
                    while (!source.exhausted()) {
                        val line = source.readUtf8Line() ?: continue
                        if (!line.startsWith("data:")) continue
                        val payload = line.removePrefix("data:").trim()
                        if (payload == "[DONE]") break
                        val chunk = gson.fromJson(payload, ChatStreamChunk::class.java)
                        emit(chunk)
                    }
                }
            }
        }.flowOn(Dispatchers.IO)
    }

    open fun prompt(name: String): String? = promptManager.resolve(name)
}

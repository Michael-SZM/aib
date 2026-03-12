package com.icon.aibrowserasistor.ai.sdk

import okhttp3.ResponseBody
import retrofit2.http.Body
import retrofit2.http.Headers
import retrofit2.http.POST
import retrofit2.http.Streaming

interface AIApi {
    @POST("v1/chat/completions")
    suspend fun chat(@Body request: ChatRequest): ChatResponse

    @Streaming
    @POST("v1/chat/completions")
    suspend fun streamChat(@Body request: ChatRequest): ResponseBody
}

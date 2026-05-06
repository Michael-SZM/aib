package com.icon.aibrowserasistor.hybrid.resource

import android.content.Context
import okhttp3.Call
import okhttp3.Callback
import okhttp3.OkHttpClient
import okhttp3.Request
import okhttp3.Response
import java.io.File
import java.io.FileOutputStream
import java.io.IOException

/**
 * 离线包下载器
 */
class Downloader(private val context: Context) {
    private val client = OkHttpClient()

    fun download(url: String, callback: (File?) -> Unit) {
        val request = Request.Builder().url(url).build()
        client.newCall(request).enqueue(object : Callback {
            override fun onFailure(call: Call, e: IOException) {
                callback(null)
            }

            override fun onResponse(call: Call, response: Response) {
                if (!response.isSuccessful) {
                    callback(null)
                    return
                }
                
                val body = response.body ?: run {
                    callback(null)
                    return
                }

                try {
                    val file = File(context.cacheDir, "temp_offline_${System.currentTimeMillis()}.zip")
                    val sink = FileOutputStream(file)
                    sink.write(body.bytes())
                    sink.close()
                    callback(file)
                } catch (e: Exception) {
                    callback(null)
                }
            }
        })
    }
}

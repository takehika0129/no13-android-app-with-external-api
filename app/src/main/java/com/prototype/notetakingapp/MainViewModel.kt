package com.prototype.notetakingapp

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext
import okhttp3.OkHttpClient
import okhttp3.Request
import org.json.JSONObject

class MainViewModel : ViewModel() {
    private val client = OkHttpClient()
    private var fetchedText: String = ""

    fun fetchTextFromApi(onResult: (String) -> Unit) {
        viewModelScope.launch {
            val result = withContext(Dispatchers.IO) {
                try {
                    val request = Request.Builder()
                        .url("https://your-api-id.execute-api.your-region.amazonaws.com/v1/data.json")
                        .build()

                    client.newCall(request).execute().use { response ->
                        if (!response.isSuccessful) return@withContext "Error: ${response.code}"

                        val jsonData = JSONObject(response.body?.string() ?: "{}")
                        jsonData.optString("message", "No message found")
                    }
                } catch (e: Exception) {
                    e.printStackTrace()
                    "Error fetching data"
                }
            }
            fetchedText = result
            onResult(result)
        }
    }
}

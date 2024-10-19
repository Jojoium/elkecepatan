package com.example.projekmobilekelas

import android.os.Bundle
import android.widget.Button
import android.widget.EditText
import android.widget.TextView
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import okhttp3.*
import okhttp3.MediaType.Companion.toMediaType
import okhttp3.RequestBody.Companion.toRequestBody
import org.json.JSONArray
import org.json.JSONObject
import java.io.IOException
class MainActivity : AppCompatActivity() {

    private lateinit var inputText: EditText
    private lateinit var outputText: TextView
    private lateinit var sendButton: Button
    private val client = OkHttpClient()

    private val API_URL = "https://api.openai.com/v1/chat/completions"
    private val API_KEY = "sk-proj-2qQUsGh9z8NkZrqokvCkT3BlbkFJu7wpk7D6njbcQ2B6gV8o" // Ganti dengan API Key Anda

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)

        inputText = findViewById(R.id.inputText)
        outputText = findViewById(R.id.outputText)
        sendButton = findViewById(R.id.sendButton)

        sendButton.setOnClickListener {
            val userInput = inputText.text.toString()
            if (userInput.isNotEmpty()) {
                sendChatRequest(userInput)
            } else {
                Toast.makeText(this@MainActivity, "Please enter text", Toast.LENGTH_SHORT).show()
            }
        }
    }

    private fun sendChatRequest(userInput: String) {
        val JSON = "application/json; charset=utf-8".toMediaType()
        val jsonBody = JSONObject().apply {
            put("model", "gpt-3.5-turbo")
            val messages = JSONArray().apply {
                val message = JSONObject()
                message.put("role", "user")
                message.put("content", userInput)
                put(message)
            }
            put("messages", messages)
        }

        val body = jsonBody.toString().toRequestBody(JSON)
        val request = Request.Builder()
            .url(API_URL)
            .header("Authorization", "Bearer $API_KEY")
            .post(body)
            .build()

        client.newCall(request).enqueue(object : Callback {
            override fun onFailure(call: Call, e: IOException) {
                e.printStackTrace()
                runOnUiThread {
                    outputText.text = "Request failed"
                }
            }

            override fun onResponse(call: Call, response: Response) {
                if (response.isSuccessful) {
                    val responseData = response.body?.string()
                    try {
                        val jsonResponse = JSONObject(responseData)
                        val choices = jsonResponse.getJSONArray("choices")
                        val firstChoice = choices.getJSONObject(0)
                        val reply = firstChoice.getJSONObject("message").getString("content")

                        runOnUiThread {
                            outputText.text = reply
                        }
                    } catch (e: Exception) {
                        e.printStackTrace()
                    }
                } else {
                    runOnUiThread {
                        outputText.text = "Response not successful"
                    }
                }
            }
        })
    }
}

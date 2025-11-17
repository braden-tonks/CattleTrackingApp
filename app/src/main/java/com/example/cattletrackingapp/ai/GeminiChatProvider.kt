package com.example.cattletrackingapp.ai

import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.withContext
import kotlinx.serialization.SerialName
import kotlinx.serialization.Serializable
import kotlinx.serialization.json.Json
import kotlinx.serialization.json.jsonArray
import kotlinx.serialization.json.jsonObject
import kotlinx.serialization.json.jsonPrimitive
import kotlinx.serialization.json.contentOrNull
import okhttp3.MediaType.Companion.toMediaType
import okhttp3.OkHttpClient
import okhttp3.Request
import okhttp3.RequestBody.Companion.toRequestBody
import java.net.SocketTimeoutException
import java.util.concurrent.TimeUnit

class GeminiChatProvider(
    private val apiKey: String,
    private val modelName: String = "gemini-2.5-flash"
) : ChatProvider {

    private val client by lazy {
        OkHttpClient.Builder()
            .connectTimeout(20, TimeUnit.SECONDS)
            .readTimeout(60, TimeUnit.SECONDS)
            .writeTimeout(60, TimeUnit.SECONDS)
            .callTimeout(90, TimeUnit.SECONDS)
            .retryOnConnectionFailure(true)
            .build()
    }

    private val json = Json { ignoreUnknownKeys = true; explicitNulls = false }

    override suspend fun send(messages: List<ChatMessage>): ChatMessage {
        if (apiKey.isBlank()) {
            return ChatMessage(ChatMessage.Role.Assistant, "Gemini API key is missing. Add GEMINI_API_KEY to local.properties.")
        }

        val userText = messages.lastOrNull { it.role == ChatMessage.Role.User }?.text.orEmpty()
        if (userText.isBlank()) return ChatMessage(ChatMessage.Role.Assistant, "Ask me something!")

        val model = modelName.ifBlank { "gemini-2.5-flash" }
        val url = "https://generativelanguage.googleapis.com/v1beta/models/$model:generateContent?key=$apiKey"

        // Nudge the model to stay concise (helps avoid hitting caps) but we also raise the cap.
        val prompt = "$userText\n\nPlease answer concisely."

        val payload = GenerateContentRequest(
            contents = listOf(ContentDto(role = "user", parts = listOf(PartDto(text = prompt)))),
            generationConfig = GenerationConfig(
                maxOutputTokens = 1600,   // raise cap to avoid MAX_TOKENS truncation
                temperature = 0.7f,
                topK = 40,
                topP = 0.95f
            )
        )

        val first = performCall(url, payload)
        if (first.isTimeout) {
            val smaller = payload.copy(generationConfig = payload.generationConfig?.copy(maxOutputTokens = 512))
            val second = performCall(url, smaller)
            return second.message
        }
        return first.message
    }

    private data class CallResult(val message: ChatMessage, val isTimeout: Boolean = false)

    private suspend fun performCall(url: String, payload: GenerateContentRequest): CallResult {
        return try {
            val replyText = withContext(Dispatchers.IO) {
                val bodyStr = json.encodeToString(GenerateContentRequest.serializer(), payload)
                val req = Request.Builder()
                    .url(url)
                    .post(bodyStr.toRequestBody("application/json; charset=utf-8".toMediaType()))
                    .build()

                client.newCall(req).execute().use { resp ->
                    val respStr = resp.body?.string().orEmpty()
                    if (!resp.isSuccessful) {
                        return@withContext "Gemini error: HTTP ${resp.code} ${resp.message}\n$respStr"
                    }

                    val parsed = json.decodeFromString(GenerateContentResponse.serializer(), respStr)

                    // 1) Try typed DTO extraction
                    var textOut =
                        parsed.candidates.firstOrNull()
                            ?.content
                            ?.parts
                            ?.firstOrNull()
                            ?.text
                            ?.takeIf { it.isNotBlank() }

                    // 2) Fallback: raw JSON extraction
                    if (textOut.isNullOrBlank()) {
                        runCatching {
                            val root = json.parseToJsonElement(respStr).jsonObject
                            val t = root["candidates"]?.jsonArray?.firstOrNull()
                                ?.jsonObject?.get("content")?.jsonObject?.get("parts")?.jsonArray?.firstOrNull()
                                ?.jsonObject?.get("text")?.jsonPrimitive?.contentOrNull
                            if (!t.isNullOrBlank()) textOut = t
                        }
                    }

                    // 3) If still missing, show reason or raw snippet
                    if (textOut.isNullOrBlank()) {
                        val finish = parsed.candidates.firstOrNull()?.finishReason
                        val blocked = parsed.promptFeedback?.blockReason
                        textOut = when {
                            !finish.isNullOrBlank() -> "No text: finishReason=$finish"
                            !blocked.isNullOrBlank() -> "Response blocked: $blocked"
                            else -> "(no text; raw: ${respStr.take(300)})"
                        }
                    }

                    textOut
                }
            }

            CallResult(ChatMessage(ChatMessage.Role.Assistant, replyText))
        } catch (t: SocketTimeoutException) {
            CallResult(
                ChatMessage(ChatMessage.Role.Assistant, "Gemini error: SocketTimeout – retrying with a smaller response…"),
                isTimeout = true
            )
        } catch (t: Throwable) {
            CallResult(ChatMessage(ChatMessage.Role.Assistant, "Gemini error: ${t::class.java.simpleName}: ${t.message ?: "unknown"}"))
        }
    }
}

/* ====== DTOs for generateContent ====== */
@Serializable
data class GenerateContentRequest(
    val contents: List<ContentDto>,
    @SerialName("generationConfig") val generationConfig: GenerationConfig? = null
)

@Serializable
data class GenerationConfig(
    @SerialName("maxOutputTokens") val maxOutputTokens: Int? = null,
    val temperature: Float? = null,
    val topK: Int? = null,
    val topP: Float? = null
)

@Serializable
data class ContentDto(
    val role: String? = null,
    val parts: List<PartDto> = emptyList()
)

@Serializable
data class PartDto(
    val text: String? = null
)

@Serializable
data class GenerateContentResponse(
    val candidates: List<CandidateDto> = emptyList(),
    @SerialName("promptFeedback") val promptFeedback: PromptFeedbackDto? = null
)

@Serializable
data class CandidateDto(
    val content: ContentDto,
    @SerialName("finishReason") val finishReason: String? = null
)

@Serializable
data class PromptFeedbackDto(
    val blockReason: String? = null
)

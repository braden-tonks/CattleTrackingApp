package com.example.cattletrackingapp.ui.screens.ChatBot

import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateListOf
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.setValue
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.cattletrackingapp.ai.ChatMessage
import com.example.cattletrackingapp.ai.ChatService
import com.example.cattletrackingapp.ai.ChatProvider
import dagger.hilt.android.lifecycle.HiltViewModel
import jakarta.inject.Inject
import kotlinx.coroutines.launch

@HiltViewModel
class ChatViewModel @Inject constructor(
    private val chat: ChatService
) : ViewModel() {

    // Messages rendered by the ChatScreen
    val messages = mutableStateListOf<ChatMessage>()

    // "typing…" indicator
    var isTyping by mutableStateOf(false)
        private set

    // Keep answers brief by nudging the model (not shown to user)
    private val brevityInstruction = "\n\nPlease answer in under 125 words."

    fun send(text: String) {
        val raw = text.trim()
        if (raw.isBlank()) return

        // Show ONLY the user's original text in the UI
        messages += ChatMessage(ChatMessage.Role.User, raw)

        // --- Debug shortcut: /ctx <query> shows DB context via ChatService.ask ---
        if (raw.startsWith("/ctx", ignoreCase = true)) {
            val arg = raw.removePrefix("/ctx").trim().ifEmpty { "tag 1234" }

            isTyping = true
            viewModelScope.launch {
                try {
                    // Call ask with a standalone /ctx message so ChatService returns the context
                    val resp = chat.ask(listOf(ChatMessage(ChatMessage.Role.User, "/ctx $arg")))
                    messages += resp
                } catch (t: Throwable) {
                    messages += ChatMessage(
                        ChatMessage.Role.Assistant,
                        "DB context error: ${t.message ?: "unknown"}"
                    )
                } finally {
                    isTyping = false
                }
            }
            return
        }

        // Build the model history: append brevity only to the LAST user message
        val modelHistory: List<ChatMessage> = messages.toList().mapIndexed { idx, msg ->
            val isLastUserMsg = idx == messages.lastIndex && msg.role == ChatMessage.Role.User
            if (isLastUserMsg) msg.copy(text = msg.text + brevityInstruction) else msg
        }

        isTyping = true
        viewModelScope.launch {
            try {
                // ChatService decides whether to augment with DB context or just chat normally
                val reply = chat.ask(modelHistory)
                messages += reply
            } catch (t: Throwable) {
                messages += ChatMessage(
                    ChatMessage.Role.Assistant,
                    "Error: ${t.message ?: "unknown"}"
                )
            } finally {
                isTyping = false
            }
        }
    }
}
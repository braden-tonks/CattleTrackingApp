package com.example.cattletrackingapp.ui.screens.chat

import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateListOf
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.setValue
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.cattletrackingapp.ai.ChatMessage
import com.example.cattletrackingapp.ai.ChatProvider
import dagger.hilt.android.lifecycle.HiltViewModel
import jakarta.inject.Inject
import kotlinx.coroutines.launch

@HiltViewModel
class ChatViewModel @Inject constructor(
    private val provider: ChatProvider
) : ViewModel() {

    // Messages the ChatScreen renders
    val messages = mutableStateListOf<ChatMessage>()

    // Observable typing state for the "three dots" indicator
    var isTyping by mutableStateOf(false)
        private set

    // Private instruction that is NOT shown in the UI
    private val brevityInstruction = "\n\nPlease answer in under 70 words."

    fun send(text: String) {
        if (text.isBlank() || isTyping) return

        // 1) Show ONLY the user's original text in the UI
        messages += ChatMessage(ChatMessage.Role.User, text.trim())

        // 2) Build a separate history for the model, where the LAST user message
        //    is augmented with the brevity instruction
        val modelHistory = messages.toList().mapIndexed { idx, msg ->
            val isLast = idx == messages.lastIndex
            if (isLast && msg.role == ChatMessage.Role.User) {
                msg.copy(text = msg.text + brevityInstruction)
            } else {
                msg
            }
        }

        isTyping = true
        viewModelScope.launch {
            try {
                val reply = provider.send(modelHistory)
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

package com.example.cattletrackingapp.ui.screens.chat

import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateListOf
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.setValue
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.cattletrackingapp.ai.ChatMessage
import com.example.cattletrackingapp.ai.ChatService   // ← updated import
import dagger.hilt.android.lifecycle.HiltViewModel
import javax.inject.Inject
import kotlinx.coroutines.launch

@HiltViewModel
class ChatViewModel @Inject constructor(
    private val chat: ChatService
) : ViewModel() {

    val messages = mutableStateListOf<ChatMessage>()
    var isTyping by mutableStateOf(false)
        private set

    private val brevityInstruction = "\n\nPlease answer in under 70 words."

    fun send(text: String) {
        if (text.isBlank() || isTyping) return
        messages += ChatMessage(ChatMessage.Role.User, text.trim())

        val modelHistory = messages.toList().mapIndexed { idx, msg ->
            if (idx == messages.lastIndex && msg.role == ChatMessage.Role.User)
                msg.copy(text = msg.text + brevityInstruction)
            else msg
        }

        isTyping = true
        viewModelScope.launch {
            try {
                val reply = chat.ask(modelHistory)
                messages += reply
            } catch (t: Throwable) {
                messages += ChatMessage(ChatMessage.Role.Assistant, "Error: ${t.message ?: "unknown"}")
            } finally {
                isTyping = false
            }
        }
    }
}

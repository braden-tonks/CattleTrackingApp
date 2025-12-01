package com.example.cattletrackingapp.ai

// Chat primitives used by the whole chat feature
data class ChatMessage(val role: Role, val text: String) {
    enum class Role { User, Assistant, System }
}

interface ChatProvider {
    suspend fun send(messages: List<ChatMessage>): ChatMessage
}

// Simple local fallback so the UI keeps working without network/keys
class LocalEchoChatProvider : ChatProvider {
    override suspend fun send(messages: List<ChatMessage>): ChatMessage {
        val lastUser = messages.lastOrNull { it.role == ChatMessage.Role.User }?.text.orEmpty()
        val reply = if (lastUser.isBlank()) {
            "Hi! How can I help?"
        } else {
            "You said: \"$lastUser\" (using local echo)"
        }
        return ChatMessage(ChatMessage.Role.Assistant, reply)
    }
}

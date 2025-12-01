package com.example.cattletrackingapp.ai

import javax.inject.Inject

class ChatService @Inject constructor(
    private val retrieval: ChatRetrieval,
    private val provider: ChatProvider
) {
    suspend fun ask(messages: List<ChatMessage>): ChatMessage {
        val userText = messages.lastOrNull { it.role == ChatMessage.Role.User }?.text.orEmpty()

        // ---------- Debug: /ctx <query> shows DB context ----------
        if (userText.startsWith("/ctx", ignoreCase = true)) {
            val q = userText.removePrefix("/ctx").trim()
            val ctx = retrieval.fetchContextFor(q)
            return ChatMessage(ChatMessage.Role.Assistant, "DB Context:\n$ctx")
        }

        // ---------- Default: normal chat ----------
        // We will only augment with DB context if a specific animal/tag is referenced AND resolved.
        if (!wantsEntityLookup(userText)) {
            return provider.send(messages)
        }

        // Try to resolve an animal from the message
        val context = retrieval.fetchContextFor(userText)

        // If we didn't resolve an entity, just answer normally (no canned prompts).
        if (!context.startsWith("Entity:", ignoreCase = true)) {
            return provider.send(messages)
        }

        // We resolved an entity: augment conversation with DB context so the model answers from it.
        val sys = ChatMessage(
            ChatMessage.Role.System,
            """
            Answer strictly using the database context provided below.
            Do not guess or invent fields. Do NOT mention farmer_id (internal-only).
            Keep answers concise. If a value is "(none)" or unknown, say so.
            """.trimIndent()
        )

        val ctxMsg = ChatMessage(
            ChatMessage.Role.User,
            """
            Use ONLY this database context to answer my previous question:

            --- DATABASE CONTEXT START ---
            $context
            --- DATABASE CONTEXT END ---
            """.trimIndent()
        )

        val lastUserIdx = messages.indexOfLast { it.role == ChatMessage.Role.User }
        val augmented = buildList {
            addAll(messages)
            if (lastUserIdx >= 0) add(lastUserIdx, sys) else add(sys)
            add(ctxMsg)
        }

        // No special local fallback: always let the provider generate the answer
        return provider.send(augmented)
    }

    // ---------- Helper: true only if a specific animal/tag is referenced ----------
    private fun wantsEntityLookup(text: String): Boolean {
        val s = text.lowercase()

        // explicit tag patterns: "tag 8", "tag#8", "tag:8", "tag8", "ear tag 8"
        if (Regex("""\b(?:ear\s+)?tag[#:=\s]*([a-z0-9\-]+)\b""").containsMatchIn(s)) return true

        // species + tag patterns: "cow 8", "bull #12", "calf-7"
        if (Regex("""\b(cow|bull|calf|heifer|steer)s?\s*#?\s*([a-z0-9\-]+)\b""").containsMatchIn(s)) return true

        // hash-only tag: "#8", "#A12"
        if (Regex("""#[a-z0-9\-]+\b""").containsMatchIn(s)) return true

        return false
    }
}

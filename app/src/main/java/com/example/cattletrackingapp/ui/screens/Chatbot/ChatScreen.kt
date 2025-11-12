package com.example.cattletrackingapp.ui.screens.chat

import androidx.compose.animation.core.FastOutSlowInEasing
import androidx.compose.animation.core.RepeatMode
import androidx.compose.animation.core.animateFloat
import androidx.compose.animation.core.infiniteRepeatable
import androidx.compose.animation.core.rememberInfiniteTransition
import androidx.compose.animation.core.tween
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.verticalScroll
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.alpha
import androidx.compose.ui.draw.clip
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import com.example.cattletrackingapp.ai.ChatMessage

@Composable
fun ChatScreen(vm: ChatViewModel, modifier: Modifier) {
    var input by remember { mutableStateOf("") }

    // Scroll state + auto-scroll to latest message
    val scrollState = rememberScrollState()
    LaunchedEffect(vm.messages.size) {
        // scroll to bottom whenever a message is added
        scrollState.animateScrollTo(scrollState.maxValue)
    }

    Column(
        modifier = modifier // <-- use the passed-in modifier (e.g., padding from Scaffold)
            .fillMaxSize()
            .padding(12.dp)
    ) {
        // Messages area
        Column(
            modifier = Modifier
                .weight(1f)
                .fillMaxWidth()
                .verticalScroll(scrollState),
            verticalArrangement = Arrangement.spacedBy(8.dp)
        ) {
            for (msg in vm.messages) {
                when (msg.role) {
                    ChatMessage.Role.User -> UserBubble(msg.text)
                    ChatMessage.Role.Assistant, ChatMessage.Role.System -> AssistantBubble(msg.text)
                }
            }

            // Typing indicator bubble while Gemini is responding
            if (vm.isTyping) {
                AssistantTypingBubble()
                Spacer(Modifier.height(8.dp))
            }
        }

        // Input row
        Row(
            modifier = Modifier.fillMaxWidth(),
            verticalAlignment = Alignment.CenterVertically
        ) {
            OutlinedTextField(
                value = input,
                onValueChange = { input = it },
                modifier = Modifier.weight(1f),
                placeholder = { Text("Ask something…") },
                singleLine = true
            )
            Spacer(Modifier.width(8.dp))
            Button(
                onClick = {
                    val text = input.trim()
                    if (text.isNotEmpty()) {
                        vm.send(text)
                        input = ""
                    }
                },
                enabled = !vm.isTyping
            ) {
                Text(if (vm.isTyping) "…" else "Send")
            }
        }
    }
}

/* ---------------- Bubbles ---------------- */

@Composable
private fun UserBubble(text: String) {
    Row(Modifier.fillMaxWidth(), horizontalArrangement = Arrangement.End) {
        Surface(
            color = MaterialTheme.colorScheme.primaryContainer,
            shape = RoundedCornerShape(18.dp),
            tonalElevation = 2.dp
        ) {
            Text(
                text = text,
                modifier = Modifier.padding(horizontal = 14.dp, vertical = 10.dp),
                textAlign = TextAlign.Start
            )
        }
    }
}

@Composable
private fun AssistantBubble(text: String) {
    Row(Modifier.fillMaxWidth(), horizontalArrangement = Arrangement.Start) {
        Surface(
            color = MaterialTheme.colorScheme.surfaceVariant,
            shape = RoundedCornerShape(18.dp),
            tonalElevation = 1.dp
        ) {
            Text(
                text = text,
                modifier = Modifier.padding(horizontal = 14.dp, vertical = 10.dp),
                textAlign = TextAlign.Start
            )
        }
    }
}

/* ------------- Typing indicator ------------- */

@Composable
private fun AssistantTypingBubble() {
    Row(Modifier.fillMaxWidth(), horizontalArrangement = Arrangement.Start) {
        Surface(
            color = MaterialTheme.colorScheme.surfaceVariant,
            shape = RoundedCornerShape(18.dp),
            tonalElevation = 1.dp
        ) {
            Box(Modifier.padding(horizontal = 12.dp, vertical = 10.dp)) {
                TypingIndicatorDots()
            }
        }
    }
}

@Composable
private fun TypingIndicatorDots(modifier: Modifier = Modifier) {
    val transition = rememberInfiniteTransition(label = "typing")
    val a1 by transition.animateFloat(
        initialValue = 0.3f, targetValue = 1f,
        animationSpec = infiniteRepeatable(
            animation = tween(durationMillis = 600, easing = FastOutSlowInEasing),
            repeatMode = RepeatMode.Reverse
        ),
        label = "a1"
    )
    val a2 by transition.animateFloat(
        initialValue = 0.3f, targetValue = 1f,
        animationSpec = infiniteRepeatable(
            animation = tween(durationMillis = 600, delayMillis = 150, easing = FastOutSlowInEasing),
            repeatMode = RepeatMode.Reverse
        ),
        label = "a2"
    )
    val a3 by transition.animateFloat(
        initialValue = 0.3f, targetValue = 1f,
        animationSpec = infiniteRepeatable(
            animation = tween(durationMillis = 600, delayMillis = 300, easing = FastOutSlowInEasing),
            repeatMode = RepeatMode.Reverse
        ),
        label = "a3"
    )

    Row(modifier, horizontalArrangement = Arrangement.spacedBy(6.dp)) {
        Dot(alpha = a1)
        Dot(alpha = a2)
        Dot(alpha = a3)
    }
}

@Composable
private fun Dot(alpha: Float) {
    Box(
        Modifier
            .size(8.dp)
            .clip(CircleShape)
            .background(MaterialTheme.colorScheme.onSurface.copy(alpha = 0.9f))
            .alpha(alpha)
    )
}

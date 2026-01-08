package com.app.fityo.chat.compose

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.LazyRow
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.lazy.rememberLazyListState
import androidx.compose.material3.Scaffold
import androidx.compose.runtime.*
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import com.app.fityo.chat.ChatViewModel
import com.app.fityo.chat.data.QuickSuggestions

/**
 * Schermata principale della chat AI fitness.
 */
@Composable
fun ChatScreen(
    viewModel: ChatViewModel,
    onBack: () -> Unit
) {
    val messages by viewModel.messages.collectAsState()
    val isProcessing by viewModel.isProcessing.collectAsState()
    val gemmaAvailable by viewModel.gemmaAvailable.collectAsState()
    val gemmaReady by viewModel.gemmaReady.collectAsState()

    var inputText by remember { mutableStateOf("") }
    val listState = rememberLazyListState()

    // Auto-scroll quando arrivano nuovi messaggi
    LaunchedEffect(messages.size) {
        if (messages.isNotEmpty()) {
            listState.animateScrollToItem(messages.size - 1)
        }
    }

    ChatTheme {
        Scaffold(
            topBar = {
                ChatTopBar(
                    onBack = onBack,
                    onClearHistory = { viewModel.clearHistory() },
                    gemmaAvailable = gemmaAvailable
                )
            },
            bottomBar = {
                Column {
                    // Quick suggestions
                    if (!isProcessing && messages.size <= 2) {
                        QuickSuggestionsRow(
                            onSuggestionClick = { suggestion ->
                                viewModel.sendSuggestion(suggestion)
                            }
                        )
                    }

                    // Input bar
                    ChatInputBar(
                        value = inputText,
                        onValueChange = { inputText = it },
                        onSend = {
                            if (inputText.isNotBlank() && !isProcessing) {
                                viewModel.sendMessage(inputText)
                                inputText = ""
                            }
                        },
                        isLoading = isProcessing
                    )
                }
            },
            containerColor = DarkBackground
        ) { padding ->
            Column(
                modifier = Modifier
                    .fillMaxSize()
                    .padding(padding)
                    .background(DarkBackground)
            ) {
                // Gemma status banner
                GemmaStatusBanner(
                    isAvailable = gemmaAvailable,
                    isReady = gemmaReady
                )

                // Messages list
                LazyColumn(
                    modifier = Modifier
                        .fillMaxSize()
                        .weight(1f),
                    state = listState,
                    contentPadding = PaddingValues(
                        start = 16.dp,
                        end = 16.dp,
                        top = 16.dp,
                        bottom = 8.dp
                    ),
                    verticalArrangement = Arrangement.spacedBy(12.dp)
                ) {
                    items(
                        items = messages,
                        key = { it.id }
                    ) { message ->
                        ChatBubble(
                            message = message,
                            onRetry = if (message.isError) {
                                { viewModel.retryLastMessage() }
                            } else null
                        )
                    }
                }
            }
        }
    }
}

/**
 * Riga di suggerimenti rapidi per aiutare l'utente.
 */
@Composable
private fun QuickSuggestionsRow(
    onSuggestionClick: (String) -> Unit,
    modifier: Modifier = Modifier
) {
    val suggestions = remember { QuickSuggestions.getRandom(4) }

    LazyRow(
        modifier = modifier
            .fillMaxWidth()
            .background(DarkBackground)
            .padding(vertical = 8.dp),
        contentPadding = PaddingValues(horizontal = 16.dp),
        horizontalArrangement = Arrangement.spacedBy(8.dp)
    ) {
        items(suggestions) { suggestion ->
            SuggestionChip(
                text = suggestion,
                onClick = { onSuggestionClick(suggestion) }
            )
        }
    }
}

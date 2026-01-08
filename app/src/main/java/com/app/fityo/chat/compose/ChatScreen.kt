package com.app.fityo.chat.compose

import androidx.compose.animation.AnimatedVisibility
import androidx.compose.animation.expandVertically
import androidx.compose.animation.shrinkVertically
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.LazyRow
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.lazy.rememberLazyListState
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Person
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.app.fityo.chat.ChatViewModel
import com.app.fityo.chat.data.PredefinedQueries
import com.app.fityo.chat.data.PredefinedQuestion
import com.app.fityo.chat.data.QuestionCategory

/**
 * Schermata principale della chat AI fitness.
 * L'utente puo SOLO selezionare domande predefinite - niente input libero.
 */
@Composable
fun ChatScreen(
    viewModel: ChatViewModel,
    onBack: () -> Unit
) {
    val messages by viewModel.messages.collectAsState()
    val isProcessing by viewModel.isProcessing.collectAsState()
    val gemmaAvailable by viewModel.gemmaAvailable.collectAsState()
    val profileName by viewModel.profileName.collectAsState()
    val isCoachMode = viewModel.isCoachMode

    val listState = rememberLazyListState()

    // Categoria espansa corrente
    var expandedCategory by remember { mutableStateOf<String?>(null) }

    // Categorie filtrate per modalita (coach mode mostra tutte, personal mode nasconde quelle coach-only)
    val categories = remember(isCoachMode) {
        PredefinedQueries.getCategoriesForMode(isCoachMode)
    }

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
                    gemmaAvailable = gemmaAvailable,
                    profileName = profileName
                )
            },
            bottomBar = {
                Column {
                    // Indicatore profilo attivo (solo in coach mode)
                    if (isCoachMode && profileName != null) {
                        ProfileIndicator(profileName = profileName!!)
                    }

                    // Area selezione domande predefinite
                    QuestionSelectionArea(
                        categories = categories,
                        expandedCategory = expandedCategory,
                        onCategoryClick = { categoryTitle ->
                            expandedCategory = if (expandedCategory == categoryTitle) null else categoryTitle
                        },
                        onQuestionClick = { question ->
                            if (!isProcessing) {
                                viewModel.sendPredefinedQuestion(question)
                                expandedCategory = null // Chiudi categoria dopo selezione
                            }
                        },
                        isProcessing = isProcessing
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
 * Indicatore del profilo attivo - mostra chiaramente per chi sono i dati.
 */
@Composable
private fun ProfileIndicator(
    profileName: String,
    modifier: Modifier = Modifier
) {
    Surface(
        color = UserBubbleColor.copy(alpha = 0.15f),
        modifier = modifier.fillMaxWidth()
    ) {
        Row(
            modifier = Modifier
                .padding(horizontal = 16.dp, vertical = 10.dp),
            verticalAlignment = Alignment.CenterVertically,
            horizontalArrangement = Arrangement.Center
        ) {
            Icon(
                imageVector = Icons.Default.Person,
                contentDescription = null,
                tint = UserBubbleColor,
                modifier = Modifier.size(18.dp)
            )
            Spacer(modifier = Modifier.width(8.dp))
            Text(
                text = "Visualizzando dati di: $profileName",
                color = UserBubbleColor,
                style = MaterialTheme.typography.bodyMedium,
                fontWeight = FontWeight.SemiBold
            )
        }
    }
}

/**
 * Area di selezione domande con categorie espandibili.
 */
@Composable
private fun QuestionSelectionArea(
    categories: List<QuestionCategory>,
    expandedCategory: String?,
    onCategoryClick: (String) -> Unit,
    onQuestionClick: (PredefinedQuestion) -> Unit,
    isProcessing: Boolean,
    modifier: Modifier = Modifier
) {
    Surface(
        color = DarkSurface,
        modifier = modifier.fillMaxWidth()
    ) {
        Column(
            modifier = Modifier.padding(vertical = 8.dp)
        ) {
            // Riga categorie
            LazyRow(
                contentPadding = PaddingValues(horizontal = 12.dp),
                horizontalArrangement = Arrangement.spacedBy(8.dp)
            ) {
                items(categories) { category ->
                    CategoryChip(
                        emoji = category.emoji,
                        title = category.title,
                        isExpanded = expandedCategory == category.title,
                        onClick = { onCategoryClick(category.title) },
                        enabled = !isProcessing,
                        isCoachOnly = category.coachOnly
                    )
                }
            }

            // Domande della categoria espansa
            AnimatedVisibility(
                visible = expandedCategory != null,
                enter = expandVertically(),
                exit = shrinkVertically()
            ) {
                val currentCategory = categories.find { it.title == expandedCategory }
                if (currentCategory != null) {
                    Column(
                        modifier = Modifier
                            .fillMaxWidth()
                            .padding(top = 12.dp, start = 12.dp, end = 12.dp)
                    ) {
                        currentCategory.questions.forEach { question ->
                            QuestionButton(
                                text = question.displayText,
                                onClick = { onQuestionClick(question) },
                                enabled = !isProcessing,
                                modifier = Modifier.padding(vertical = 4.dp)
                            )
                        }
                    }
                }
            }
        }
    }
}

/**
 * Chip per categoria di domande.
 */
@Composable
private fun CategoryChip(
    emoji: String,
    title: String,
    isExpanded: Boolean,
    onClick: () -> Unit,
    enabled: Boolean,
    isCoachOnly: Boolean = false,
    modifier: Modifier = Modifier
) {
    Surface(
        onClick = onClick,
        enabled = enabled,
        shape = RoundedCornerShape(20.dp),
        color = when {
            isExpanded -> UserBubbleColor
            isCoachOnly -> AccentOrange.copy(alpha = 0.3f)
            else -> SuggestionChipColor
        },
        modifier = modifier
    ) {
        Row(
            modifier = Modifier.padding(horizontal = 14.dp, vertical = 10.dp),
            verticalAlignment = Alignment.CenterVertically,
            horizontalArrangement = Arrangement.spacedBy(6.dp)
        ) {
            Text(
                text = emoji,
                fontSize = 16.sp
            )
            Text(
                text = title,
                color = TextPrimary,
                style = MaterialTheme.typography.bodyMedium,
                fontWeight = if (isExpanded) FontWeight.Bold else FontWeight.Normal
            )
        }
    }
}

/**
 * Bottone per selezionare una domanda predefinita.
 */
@Composable
private fun QuestionButton(
    text: String,
    onClick: () -> Unit,
    enabled: Boolean,
    modifier: Modifier = Modifier
) {
    Surface(
        onClick = onClick,
        enabled = enabled,
        shape = RoundedCornerShape(12.dp),
        color = BotBubbleColor,
        modifier = modifier.fillMaxWidth()
    ) {
        Text(
            text = text,
            color = if (enabled) TextPrimary else TextSecondary,
            style = MaterialTheme.typography.bodyMedium,
            modifier = Modifier.padding(horizontal = 16.dp, vertical = 12.dp)
        )
    }
}

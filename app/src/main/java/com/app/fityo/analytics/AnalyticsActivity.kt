package com.app.fityo.analytics

import android.content.Intent
import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.activity.viewModels
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.*
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.ArrowBack
import androidx.compose.material.icons.filled.Chat
import androidx.compose.material3.*
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.sp
import com.app.fityo.analytics.ui.AnalyticsDashboard
import com.app.fityo.chat.ChatActivity
import com.app.fityo.ui.dashboard.compose.DashboardAccentBlue
import com.app.fityo.ui.dashboard.compose.DashboardAccentGreen
import com.app.fityo.ui.dashboard.compose.DashboardBackground
import com.app.fityo.ui.dashboard.compose.DashboardTextPrimary

class AnalyticsActivity : ComponentActivity() {

    private val viewModel: AnalyticsViewModel by viewModels()

    companion object {
        const val EXTRA_PROFILE_ID = "extra_profile_id"
        const val EXTRA_PROFILE_NAME = "extra_profile_name"
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        // Ricevi profilo dalla Dashboard
        val profileId = intent.getIntExtra(EXTRA_PROFILE_ID, -1).takeIf { it != -1 }
        val profileName = intent.getStringExtra(EXTRA_PROFILE_NAME)

        // Passa profilo al ViewModel
        viewModel.setSelectedProfile(profileId, profileName)

        setContent {
            AnalyticsScreen(
                viewModel = viewModel,
                profileName = profileName,
                onBackClick = { finish() },
                onOpenChat = {
                    startActivity(
                        Intent(this, ChatActivity::class.java).apply {
                            profileId?.let { putExtra(ChatActivity.EXTRA_PROFILE_ID, it) }
                        }
                    )
                }
            )
        }
    }
}

@OptIn(ExperimentalMaterial3Api::class)
@Composable
private fun AnalyticsScreen(
    viewModel: AnalyticsViewModel,
    profileName: String?,
    onBackClick: () -> Unit,
    onOpenChat: () -> Unit
) {
    Scaffold(
        topBar = {
            TopAppBar(
                title = {
                    Column {
                        Text(
                            "Analytics Avanzate",
                            color = DashboardTextPrimary,
                            fontSize = 20.sp,
                            fontWeight = FontWeight.Bold
                        )
                        profileName?.let { name ->
                            Text(
                                text = name,
                                color = DashboardAccentBlue,
                                fontSize = 12.sp
                            )
                        }
                    }
                },
                navigationIcon = {
                    IconButton(onClick = onBackClick) {
                        Icon(
                            Icons.AutoMirrored.Filled.ArrowBack,
                            contentDescription = "Indietro",
                            tint = DashboardTextPrimary
                        )
                    }
                },
                actions = {
                    // Il bot risponde con query sul database: sta qui perche' e' il
                    // posto dove ci si fanno domande sui propri numeri.
                    IconButton(onClick = onOpenChat) {
                        Icon(
                            Icons.Default.Chat,
                            contentDescription = "Apri il bot delle domande",
                            tint = DashboardAccentGreen
                        )
                    }
                },
                colors = TopAppBarDefaults.topAppBarColors(
                    containerColor = DashboardBackground
                )
            )
        },
        containerColor = DashboardBackground
    ) { paddingValues ->
        AnalyticsDashboard(
            viewModel = viewModel,
            modifier = Modifier
                .fillMaxSize()
                .padding(paddingValues)
        )
    }
}

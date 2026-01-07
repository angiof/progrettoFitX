package com.app.fityo.analytics

import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.activity.viewModels
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.*
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.ArrowBack
import androidx.compose.material3.*
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.app.fityo.analytics.ui.AnalyticsDashboard

private val DarkBackground = Color(0xFF0D0D0D)
private val TextPrimary = Color(0xFFE8E8E8)

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
                onBackClick = { finish() }
            )
        }
    }
}

@OptIn(ExperimentalMaterial3Api::class)
@Composable
private fun AnalyticsScreen(
    viewModel: AnalyticsViewModel,
    profileName: String?,
    onBackClick: () -> Unit
) {
    Scaffold(
        topBar = {
            TopAppBar(
                title = {
                    Column {
                        Text(
                            "Analytics Avanzate",
                            color = TextPrimary,
                            fontSize = 20.sp,
                            fontWeight = FontWeight.Bold
                        )
                        profileName?.let { name ->
                            Text(
                                text = name,
                                color = Color(0xFF8B5CF6),
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
                            tint = TextPrimary
                        )
                    }
                },
                colors = TopAppBarDefaults.topAppBarColors(
                    containerColor = DarkBackground
                )
            )
        },
        containerColor = DarkBackground
    ) { paddingValues ->
        AnalyticsDashboard(
            viewModel = viewModel,
            modifier = Modifier
                .fillMaxSize()
                .padding(paddingValues)
                .background(DarkBackground)
        )
    }
}

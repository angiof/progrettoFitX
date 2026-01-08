package com.app.fityo.ui.dashboard

import android.content.Intent
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Toast
import androidx.compose.ui.platform.ComposeView
import androidx.compose.ui.platform.ViewCompositionStrategy
import androidx.fragment.app.Fragment
import androidx.fragment.app.viewModels
import com.app.fityo.analytics.AnalyticsActivity
import com.app.fityo.chat.ChatActivity
import com.app.fityo.R
import com.app.fityo.data_layer.db.DB.DbFit
import com.app.fityo.ui.dashboard.compose.DashboardScreen
import com.app.fityo.utils.convertTimestampsToFormattedDates
import com.google.android.material.datepicker.MaterialDatePicker
import java.text.SimpleDateFormat
import java.util.Locale


class DashFrag : Fragment() {

    private val viewModel: DashViewModel by viewModels {
        val db = DbFit.getDatabase(requireContext())
        DashViewModelFactory(
            db.schedeDao(),
            requireActivity().application,
            db.coachProfileDao()
        )
    }

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?, savedInstanceState: Bundle?
    ): View {
        return ComposeView(requireContext()).apply {
            setViewCompositionStrategy(ViewCompositionStrategy.DisposeOnViewTreeLifecycleDestroyed)
            setContent {
                DashboardScreen(
                    viewModel = viewModel,
                    onOpenAnalytics = {
                        val intent = Intent(requireContext(), AnalyticsActivity::class.java).apply {
                            viewModel.selectedProfileId.value?.let { profileId ->
                                putExtra(AnalyticsActivity.EXTRA_PROFILE_ID, profileId)
                            }
                            viewModel.selectedProfileName.value?.let { profileName ->
                                putExtra(AnalyticsActivity.EXTRA_PROFILE_NAME, profileName)
                            }
                        }
                        startActivity(intent)
                    },
                    onSelectDateRange = {
                        showDateRangePickerDialog()
                    },
                    onOpenChat = {
                        val intent = Intent(requireContext(), ChatActivity::class.java).apply {
                            viewModel.selectedProfileId.value?.let { profileId ->
                                putExtra(ChatActivity.EXTRA_PROFILE_ID, profileId)
                            }
                        }
                        startActivity(intent)
                    }
                )
            }
        }
    }

    override fun onResume() {
        super.onResume()
        viewModel.loadCoachProfiles()
        viewModel.refreshStats()
        viewModel.refreshPercentuali()
        viewModel.loadMediaIntensitaAll()
        viewModel.loadWeekFrequencyAll()
    }

    private fun showDateRangePickerDialog() {
        val dateRangePicker = MaterialDatePicker.Builder.dateRangePicker()
            .setTitleText(getString(R.string.seleziona_un_periodo))
            .setTheme(R.style.CustomDatePicker)
            .setTitleText(getString(R.string.select_period))
            .setSelection(
                androidx.core.util.Pair(
                    System.currentTimeMillis(),
                    System.currentTimeMillis()
                )
            )
            .build()

        dateRangePicker.addOnPositiveButtonClickListener { selection ->
            val startDate = selection.first
            val endDate = selection.second

            val formattedStartDateString =
                SimpleDateFormat(getString(R.string.dd_mm_yyyy), Locale.getDefault()).format(
                    startDate
                )
            val formattedEndDateString =
                SimpleDateFormat(
                    getString(R.string.dd_mm_yyyy_simplea_daat_format),
                    Locale.getDefault()
                ).format(endDate)

            val dateRange = "$formattedStartDateString - $formattedEndDateString"

            val (queryStartDateString, queryEndDateString) = convertTimestampsToFormattedDates(
                startDate,
                endDate
            )
            viewModel.refreshChartsForRange(queryStartDateString, queryEndDateString)
            viewModel.setStatusData(dateRange)
            viewModel.refreshStats()

            Toast.makeText(
                requireContext(),
                getString(R.string.filter_range_feedback, formattedStartDateString, formattedEndDateString),
                Toast.LENGTH_SHORT
            ).show()
        }
        dateRangePicker.show(parentFragmentManager, "date_range_picker")
    }
}


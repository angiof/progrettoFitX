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
import androidx.navigation.fragment.findNavController
import com.app.fityo.analytics.AnalyticsActivity
import com.app.fityo.R
import com.app.fityo.data_layer.db.DB.DbFit
import com.app.fityo.ui.coach.CoachActivity
import com.app.fityo.ui.dashboard.compose.DashboardScreen
import com.google.android.material.datepicker.MaterialDatePicker
import java.time.Instant
import java.time.ZoneOffset
import java.time.format.DateTimeFormatter


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
                    onBack = {
                        findNavController().popBackStack()
                    },
                    onManageProfiles = {
                        startActivity(Intent(requireContext(), CoachActivity::class.java))
                    }
                )
            }
        }
    }

    override fun onResume() {
        super.onResume()
        viewModel.loadCoachProfiles()
        viewModel.refresh()
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

            // MaterialDatePicker exposes UTC midnights, independent of the device timezone.
            val from = Instant.ofEpochMilli(startDate).atZone(ZoneOffset.UTC).toLocalDate()
            val to = Instant.ofEpochMilli(endDate).atZone(ZoneOffset.UTC).toLocalDate()
            val format = DateTimeFormatter.ofPattern("dd/MM/yyyy")
            val formattedStartDateString = from.format(format)
            val formattedEndDateString = to.format(format)
            viewModel.refreshChartsForRange(from.toString(), to.toString())

            Toast.makeText(
                requireContext(),
                getString(R.string.filter_range_feedback, formattedStartDateString, formattedEndDateString),
                Toast.LENGTH_SHORT
            ).show()
        }
        dateRangePicker.show(parentFragmentManager, "date_range_picker")
    }
}


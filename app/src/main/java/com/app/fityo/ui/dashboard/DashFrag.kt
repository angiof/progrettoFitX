package com.app.fityo.ui.dashboard

import android.content.Intent
import android.graphics.Color
import android.graphics.Typeface
import android.os.Bundle
import android.view.LayoutInflater
import android.view.MotionEvent
import android.view.View
import android.view.ViewGroup
import android.view.animation.AccelerateInterpolator
import android.view.animation.Animation
import android.widget.Toast
import androidx.fragment.app.Fragment
import androidx.fragment.app.viewModels
import com.app.fityo.analytics.AnalyticsActivity
import com.app.fityo.R
import com.app.fityo.data_layer.db.CoachProfileEntity
import com.app.fityo.data_layer.db.DB.DbFit
import com.app.fityo.data_layer.db.dao.GruppoMuscolareIntensitaMedia
import com.app.fityo.databinding.FragmentDashboardBinding
import com.app.fityo.dominio.GruppoMuscolarePercentuale
import com.app.fityo.ui.dashboard.DashViewModel
import com.app.fityo.ui.dashboard.DashViewModelFactory
import com.app.fityo.ui.dashboard.RotateAnimationx
import com.app.fityo.utils.convertTimestampsToFormattedDates
import com.app.fityo.dominio.WeekdayWorkoutCount
import com.github.mikephil.charting.charts.PieChart
import com.github.mikephil.charting.components.Legend
import com.github.mikephil.charting.components.XAxis
import com.github.mikephil.charting.data.BarData
import com.github.mikephil.charting.data.BarDataSet
import com.github.mikephil.charting.data.BarEntry
import com.github.mikephil.charting.data.Entry
import com.github.mikephil.charting.data.PieData
import com.github.mikephil.charting.data.PieDataSet
import com.github.mikephil.charting.data.PieEntry
import com.github.mikephil.charting.formatter.IndexAxisValueFormatter
import com.github.mikephil.charting.highlight.Highlight
import com.github.mikephil.charting.listener.ChartTouchListener
import com.github.mikephil.charting.listener.OnChartGestureListener
import com.github.mikephil.charting.listener.OnChartValueSelectedListener
import com.google.android.material.datepicker.MaterialDatePicker
import com.google.android.material.dialog.MaterialAlertDialogBuilder
import java.text.SimpleDateFormat
import java.util.Locale


class DashFrag : Fragment() {
    private lateinit var binding: FragmentDashboardBinding

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
        binding = FragmentDashboardBinding.inflate(inflater, container, false)



        binding.tvMese.setOnClickListener {
            showDateRangePickerDialogX()
        }

        binding.btnAnalytics.setOnClickListener {
            startActivity(Intent(requireContext(), AnalyticsActivity::class.java))
        }

        binding.btnSelectProfile.setOnClickListener {
            showProfileSelectionDialog()
        }

        viewModel.percentualiGruppiMuscolari.observe(viewLifecycleOwner) { percentuali ->
            updatePieChart(percentuali)
        }

        viewModel.selectedProfileName.observe(viewLifecycleOwner) { name ->
            binding.btnSelectProfile.text = if (name != null) {
                getString(R.string.dashboard_showing_profile, name)
            } else {
                getString(R.string.dashboard_profile_all)
            }
        }

        observeStatusData()
        observerbar()
        observeWeekFrequency()
        observeStats()

        return binding.root
    }

    private fun showProfileSelectionDialog() {
        val profiles = viewModel.coachProfiles.value ?: emptyList()

        val items = mutableListOf(getString(R.string.dashboard_profile_all))
        items.addAll(profiles.map { it.name })

        MaterialAlertDialogBuilder(requireContext())
            .setTitle(getString(R.string.dashboard_select_profile))
            .setItems(items.toTypedArray()) { _, which ->
                if (which == 0) {
                    viewModel.setSelectedProfile(null, null)
                } else {
                    val selectedProfile = profiles[which - 1]
                    viewModel.setSelectedProfile(selectedProfile.id, selectedProfile.name)
                }
            }
            .show()
    }

    override fun onResume() {
        super.onResume()
        viewModel.loadCoachProfiles()
        viewModel.refreshStats()
        viewModel.refreshPercentuali()
        viewModel.loadMediaIntensitaAll()
        viewModel.loadWeekFrequencyAll()
    }

    fun observeStatusData() {
        viewModel.statusDataLive.observe(viewLifecycleOwner) { strData ->
            Toast.makeText(requireContext(), strData, Toast.LENGTH_SHORT).show()
        }
    }

    private fun observerbar() {
        viewModel.mediaIntensita.observe(viewLifecycleOwner) { mediaIntensitaList ->
            if (mediaIntensitaList != null && mediaIntensitaList.isNotEmpty()) {
                setupBarChart(mediaIntensitaList)
            } else {
                binding.barchartInclude.barchartExt.clear()
                binding.barchartInclude.barchartExt.invalidate()
            }
        }
    }

    private fun observeWeekFrequency() {
        viewModel.weekFrequency.observe(viewLifecycleOwner) { list ->
            setupWeekChart(list)
        }
    }

    private fun observeStats() {
        viewModel.dashboardStats.observe(viewLifecycleOwner) { stats ->
            val cardBinding = binding.cardUserInfoDasboard
            cardBinding.tvTotalSchedeValue.text = stats.totalSchede.toString()
            cardBinding.tvFavoriteSchedeValue.text = stats.favoriteSchede.toString()
            cardBinding.tvTotalExercisesValue.text = stats.totalExercises.toString()
            cardBinding.tvLastSessionValue.text =
                stats.lastWorkoutDate ?: getString(R.string.dashboard_last_session_none)

            cardBinding.tvDaysSinceValue.text = stats.daysSinceLastWorkout?.let {
                getString(R.string.dashboard_days_format, it)
            } ?: getString(R.string.dashboard_no_data)

            cardBinding.tvMostTrainedValue.text = stats.mostTrainedMuscle
                ?: getString(R.string.dashboard_no_data)

            cardBinding.tvAvgPerWeekValue.text = stats.avgWorkoutsPerWeek?.let {
                getString(R.string.dashboard_avg_format, it)
            } ?: getString(R.string.dashboard_no_data)
        }
    }


    private fun setupBarChart(mediaIntensitaList: List<GruppoMuscolareIntensitaMedia>) {
        val barChart = binding.barchartInclude.barchartExt
        // Crea le entries per il HorizontalBarChart
        val labels = ArrayList<String>()

        val entries = ArrayList<BarEntry>()
        mediaIntensitaList.forEachIndexed { index, gruppoMuscolareIntensita ->
            entries.add(BarEntry(index.toFloat(), gruppoMuscolareIntensita.mediaIntensita))
            labels.add(gruppoMuscolareIntensita.gruppoMuscolare)
        }

        val dataSet = BarDataSet(entries, getString(R.string.bar_label_intensity))

        val colors = listOf(
            Color.parseColor("#FF6F61"),
            Color.parseColor("#4DD0E1"),
            Color.parseColor("#9575CD"),
            Color.parseColor("#81C784"),
            Color.parseColor("#FFD54F")
        )
        dataSet.colors = colors

        val data = BarData(dataSet)
        barChart.data = data

        // Impostazioni del grafico
        barChart.description.isEnabled = false
        barChart.setFitBars(true)
        barChart.setNoDataText(getString(R.string.no_data_chart))
        barChart.setNoDataTextColor(Color.WHITE)
        barChart.setDrawGridBackground(false)

        // Stilizzazione della legenda
        val legend = barChart.legend
        legend.verticalAlignment = Legend.LegendVerticalAlignment.BOTTOM
        legend.horizontalAlignment = Legend.LegendHorizontalAlignment.LEFT
        legend.orientation = Legend.LegendOrientation.HORIZONTAL
        legend.setDrawInside(false)
        legend.isWordWrapEnabled = true
        legend.textColor = Color.WHITE


        // Stilizzazione del testo dei valori
        dataSet.setDrawValues(true) // Abilita la visualizzazione dei valori
        dataSet.valueTextSize = 10f
        dataSet.valueTextColor = Color.WHITE
        dataSet.valueTypeface = Typeface.DEFAULT_BOLD


        barChart.xAxis.valueFormatter = IndexAxisValueFormatter(labels)
        barChart.xAxis.labelCount = labels.size
        barChart.xAxis.granularity = 1f
        barChart.xAxis.position = XAxis.XAxisPosition.BOTTOM
        barChart.xAxis.textColor = Color.WHITE
        barChart.xAxis.setDrawGridLines(false)
        barChart.axisLeft.textColor = Color.WHITE
        barChart.axisRight.isEnabled = false
        // Animazione
        barChart.animateY(2000)
        barChart.animationMatrix


        // Configura l'asse Y per avere incrementi interi e partire da 0
        barChart.axisLeft.axisMinimum = 0f
        barChart.axisLeft.granularity = 1f
        barChart.axisLeft.setDrawLabels(true)
        barChart.axisLeft.setDrawGridLines(true)

        // Aggiorna il grafico
        barChart.invalidate()
    }

    private fun setupWeekChart(data: List<WeekdayWorkoutCount>) {
        val chart = binding.weeklyChartInclude.weekChart
        if (data.isEmpty()) {
            chart.clear()
            chart.invalidate()
            return
        }
        val dayLabels = listOf(
            getString(R.string.day_sun_short),
            getString(R.string.day_mon_short),
            getString(R.string.day_tue_short),
            getString(R.string.day_wed_short),
            getString(R.string.day_thu_short),
            getString(R.string.day_fri_short),
            getString(R.string.day_sat_short)
        )
        val map = data.associate { it.dayOfWeek to it.count }
        val entries = dayLabels.indices.map { index ->
            val value = map[index] ?: 0
            BarEntry(index.toFloat(), value.toFloat())
        }
        val dataSet = BarDataSet(entries, getString(R.string.bar_label_week)).apply {
            color = Color.parseColor("#4DD0E1")
            valueTextColor = Color.WHITE
            valueTypeface = Typeface.DEFAULT_BOLD
            valueTextSize = 10f
        }
        chart.data = BarData(dataSet)
        chart.description.isEnabled = false
        chart.legend.isEnabled = false
        chart.axisRight.isEnabled = false
        chart.axisLeft.textColor = Color.WHITE
        chart.axisLeft.axisMinimum = 0f
        chart.xAxis.valueFormatter = IndexAxisValueFormatter(dayLabels)
        chart.xAxis.textColor = Color.WHITE
        chart.xAxis.granularity = 1f
        chart.xAxis.position = XAxis.XAxisPosition.BOTTOM
        chart.setDrawGridBackground(false)
        chart.setNoDataText(getString(R.string.no_data_chart))
        chart.setNoDataTextColor(Color.WHITE)
        chart.animateY(1500)
        chart.invalidate()
    }


    private fun updatePieChart(percentuali: List<GruppoMuscolarePercentuale>) {
        val pieChart: PieChart = binding.layoutExt.pieChart
        pieChart.setNoDataText(getString(R.string.no_data_chart))
        pieChart.setNoDataTextColor(Color.WHITE)
        if (percentuali.isEmpty()) {
            pieChart.clear()
            pieChart.invalidate()
            return
        }

        val entries = percentuali.map { percentuale ->
            PieEntry(percentuale.percentuale, percentuale.gruppoMuscolare).apply {
                data = percentuale.gruppoMuscolare
            }
        }

        val set = PieDataSet(entries, getString(R.string.dashboard_total_esercizi)).apply {
            val colors = listOf(
                Color.parseColor("#4777c0"),
                Color.parseColor("#a374c6"),
                Color.parseColor("#4fb3e8"),
                Color.parseColor("#99cf43"),
                Color.parseColor("#fdc135"),
                Color.parseColor("#fd9a47"),
                Color.parseColor("#eb6e7a"),
                Color.parseColor("#6785c2")
            )
            this.colors = colors
            setDrawValues(true)
            valueTextColor = Color.WHITE
            yValuePosition = PieDataSet.ValuePosition.OUTSIDE_SLICE
            valueTextSize = 16f
            valueTypeface = Typeface.DEFAULT_BOLD
            valueLinePart1Length = 0.6f
            valueLinePart2Length = 0.3f
            valueLineWidth = 2f
            valueLinePart1OffsetPercentage = 115f
            isUsingSliceColorAsValueLineColor = true
        }

        val data = PieData(set).apply {
            setValueTextColor(Color.WHITE)
        }
        pieChart.data = data
        pieChart.setUsePercentValues(true)
        pieChart.isDrawHoleEnabled = true
        pieChart.holeRadius = 50f
        pieChart.transparentCircleRadius = 55f
        pieChart.setHoleColor(Color.TRANSPARENT)
        pieChart.setDrawCenterText(true)
        pieChart.setCenterTextSize(20f)
        pieChart.setCenterTextTypeface(Typeface.DEFAULT_BOLD)
        pieChart.setCenterTextColor(Color.WHITE)
        pieChart.centerText = getString(R.string.title_dashboard)
        pieChart.legend.apply {
            textColor = Color.WHITE
            isWordWrapEnabled = true
        }
        pieChart.setExtraOffsets(20f, 0f, 20f, 0f)
        pieChart.animateY(1200)
        pieChart.invalidate()
        rotatePieChart(pieChart, 8000L)

        pieChart.setOnChartValueSelectedListener(object : OnChartValueSelectedListener {
            override fun onValueSelected(e: Entry?, h: Highlight?) {
                val label = (e as? PieEntry)?.label ?: return
                Toast.makeText(
                    requireContext(),
                    getString(R.string.dashboard_total_esercizi) + ": " + label,
                    Toast.LENGTH_SHORT
                ).show()
            }

            override fun onNothingSelected() = Unit
        })
        pieChart.onChartGestureListener = object : OnChartGestureListener {
            override fun onChartGestureStart(
                me: MotionEvent?,
                lastPerformedGesture: ChartTouchListener.ChartGesture?
            ) = Unit

            override fun onChartGestureEnd(
                me: MotionEvent?,
                lastPerformedGesture: ChartTouchListener.ChartGesture?
            ) = Unit

            override fun onChartLongPressed(me: MotionEvent?) = Unit
            override fun onChartDoubleTapped(me: MotionEvent?) = Unit
            override fun onChartSingleTapped(me: MotionEvent?) = Unit
            override fun onChartFling(
                me1: MotionEvent?,
                me2: MotionEvent?,
                velocityX: Float,
                velocityY: Float
            ) = Unit

            override fun onChartScale(me: MotionEvent?, scaleX: Float, scaleY: Float) = Unit
            override fun onChartTranslate(me: MotionEvent?, dX: Float, dY: Float) = Unit
        }
    }

    fun rotatePieChart(pieChart: PieChart, durationMs: Long = 4000L) {
        val rotateAnimation = RotateAnimationx(pieChart, 360f).apply {
            duration = durationMs
            repeatCount = Animation.INFINITE
            interpolator = AccelerateInterpolator()
        }
        pieChart.startAnimation(rotateAnimation)
    }


    private fun showDateRangePickerDialogX() {
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

            binding.tvMeseCorrente.text = formattedStartDateString
            binding.tvMeseFine.text = formattedEndDateString
            viewModel.setStatusData(dateRange)
            viewModel.refreshStats()
        }
        dateRangePicker.show(parentFragmentManager, "date_range_picker")
    }


}


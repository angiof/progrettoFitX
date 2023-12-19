import android.graphics.Color
import android.graphics.Typeface
import android.os.Bundle
import android.util.Log
import android.view.LayoutInflater
import android.view.MotionEvent
import android.view.View
import android.view.ViewGroup
import android.view.animation.AccelerateInterpolator
import android.view.animation.Animation
import android.widget.Toast
import androidx.fragment.app.Fragment
import androidx.fragment.app.viewModels
import androidx.lifecycle.viewModelScope
import com.app.progrettofitx.R
import com.app.progrettofitx.data_layer.db.DB.DbFit
import com.app.progrettofitx.data_layer.db.dao.GruppoMuscolareIntensitaMedia
import com.app.progrettofitx.databinding.FragmentDashboardBinding
import com.app.progrettofitx.dominio.GruppoMuscolarePercentuale
import com.app.progrettofitx.ui.dashboard.DashViewModel
import com.app.progrettofitx.ui.dashboard.DashViewModelFactory
import com.app.progrettofitx.ui.dashboard.RotateAnimationx
import com.app.progrettofitx.utils.convertTimestampsToFormattedDates
import com.github.mikephil.charting.charts.PieChart
import com.github.mikephil.charting.components.Legend
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
import com.github.mikephil.charting.utils.ColorTemplate
import com.google.android.material.datepicker.MaterialDatePicker
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import java.text.SimpleDateFormat
import java.util.Locale


class DashFrag : Fragment() {
    private lateinit var binding: FragmentDashboardBinding

    private val viewModel: DashViewModel by viewModels {
        DashViewModelFactory(
            DbFit.getDatabase(requireContext()).schedeDao(),
            requireActivity().application
        )
    }

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?, savedInstanceState: Bundle?
    ): View {
        binding = FragmentDashboardBinding.inflate(inflater, container, false)



        binding.tvMese.setOnClickListener {

            showDateRangePickerDialogX()
        }

        viewModel.percentualiGruppiMuscolari.observe(viewLifecycleOwner) { percentuali ->
            updatePieChart(percentuali)
        }

        observeStatusData()
        observerbar()

        return binding.root
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
                Log.d("local dashFragment", "i dati non sono presente nel periodo scelto")
            }
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

        val dataSet = BarDataSet(entries, "media di intensità per gruppo muscolare")

        // Imposta i colori come nell'immagine caricata
        val colors = intArrayOf(
            Color.parseColor("#757575"), // Low - Gray
            Color.parseColor("#4CAF50"), // Normal - Green
            Color.parseColor("#FF9800"), // High - Orange
            Color.parseColor("#F44336")  // Very High - Red
        )
        dataSet.colors = colors.toList()

        val data = BarData(dataSet)
        barChart.data = data

        // Impostazioni del grafico
        barChart.description.isEnabled = false
        barChart.setFitBars(true)

        // Stilizzazione della legenda
        val legend = barChart.legend
        legend.verticalAlignment = Legend.LegendVerticalAlignment.BOTTOM
        legend.horizontalAlignment = Legend.LegendHorizontalAlignment.LEFT
        legend.orientation = Legend.LegendOrientation.HORIZONTAL
        legend.setDrawInside(false)
        legend.isWordWrapEnabled = true


        // Stilizzazione del testo dei valori
        dataSet.setDrawValues(true) // Abilita la visualizzazione dei valori
        dataSet.valueTextSize = 10f // Imposta la dimensione del testo
        dataSet.valueTextColor = Color.BLUE // Imposta il colore del testo
        dataSet.valueTypeface = Typeface.DEFAULT_BOLD // Imposta il tipo di carattere in grassetto


        barChart.xAxis.valueFormatter = IndexAxisValueFormatter(labels)
        barChart.xAxis.labelCount = labels.size
        // Animazione
        barChart.animateY(2000)
        barChart.animationMatrix


        // Configura l'asse Y per avere incrementi interi e partire da 0
        barChart.axisLeft.axisMinimum = 5f // inizia da 0
        barChart.axisLeft.axisMaximum = 20f // massimo a 15
        barChart.axisLeft.granularity = 5f // incrementi di 1
        barChart.axisLeft.setDrawLabels(true) // mostra le etichette
        barChart.axisLeft.setDrawGridLines(true) // mostra la griglia

        // Aggiorna il grafico
        barChart.invalidate()
    }


    private fun updatePieChart(percentuali: List<GruppoMuscolarePercentuale>) {
        viewModel.viewModelScope.launch(Dispatchers.Main) {
            val pieChart: PieChart = binding.layoutExt.pieChart
            val entries: MutableList<PieEntry> = ArrayList()

            for (percentuale in percentuali) {
                entries.add(PieEntry(percentuale.percentuale, percentuale.gruppoMuscolare))
            }

            val set = PieDataSet(entries, "Esercizi")
            set.setColors(*ColorTemplate.MATERIAL_COLORS)
            set.setDrawValues(true)

            // Chart colors
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
            set.colors = colors
            set.setValueTextColors(colors)

            val data = PieData(set)
            pieChart.data = data
            pieChart.setUsePercentValues(true)
            // Hole
            pieChart.isDrawHoleEnabled = true
            pieChart.holeRadius = 50f
            pieChart.clearAnimation()

            set.valueLinePart1Length = 0.6f
            set.valueLinePart2Length = 0.3f
            set.valueLineWidth = 2f
            set.valueLinePart1OffsetPercentage = 115f  // Line starts outside of chart
            set.isUsingSliceColorAsValueLineColor = true


            // Value text appearance
            set.yValuePosition = PieDataSet.ValuePosition.OUTSIDE_SLICE
            set.valueTextSize = 16f
            set.valueTypeface = Typeface.DEFAULT_BOLD

            // Center text
            pieChart.setDrawCenterText(true)
            pieChart.setCenterTextSize(20f)
            pieChart.setCenterTextTypeface(Typeface.DEFAULT_BOLD)
            pieChart.setCenterTextColor(Color.parseColor("#222222"))
            pieChart.centerText = "muscoli"

            pieChart.setExtraOffsets(20f, 0f, 20f, 0f)
            pieChart.data = data
            pieChart.invalidate() //
            // Aggiorna il grafico con i nuovi dati
            rotatePieChart(pieChart, 10000)


            binding.layoutExt.pieChart.let {
                if (pieChart.data.dataSet == null) {
                    it.visibility = View.GONE
                } else {
                    it.visibility = View.VISIBLE
                    pieChart.clearAnimation()
                    rotatePieChart(pieChart)
                }
            }



            pieChart.onChartGestureListener = object : OnChartValueSelectedListener,
                OnChartGestureListener {
                override fun onValueSelected(e: Entry?, h: Highlight?) {
                    e?.let {
                        // Ottieni l'etichetta del gruppo muscolare selezionato
                        val gruppoMuscolare = it.data as String
                        Toast.makeText(
                            requireContext(),
                            "Selezionato: $gruppoMuscolare",
                            Toast.LENGTH_SHORT
                        ).show()
                    }
                }


                override fun onNothingSelected() {
                }

                override fun onChartGestureStart(
                    me: MotionEvent?,
                    lastPerformedGesture: ChartTouchListener.ChartGesture?
                ) {
                }

                override fun onChartGestureEnd(
                    me: MotionEvent?,
                    lastPerformedGesture: ChartTouchListener.ChartGesture?
                ) {
                }

                override fun onChartLongPressed(me: MotionEvent?) {
                }

                override fun onChartDoubleTapped(me: MotionEvent?) {
                }

                override fun onChartSingleTapped(me: MotionEvent?) {
                }

                override fun onChartFling(
                    me1: MotionEvent?,
                    me2: MotionEvent?,
                    velocityX: Float,
                    velocityY: Float
                ) {
                }

                override fun onChartScale(me: MotionEvent?, scaleX: Float, scaleY: Float) {
                }

                override fun onChartTranslate(me: MotionEvent?, dX: Float, dY: Float) {
                }

            }
        }

    }

    fun rotatePieChart(pieChart: PieChart, secondos: Long = 1000) {
        val rotateAnimation = RotateAnimationx(pieChart, 360f).apply {
            duration = secondos // 10 secondi per una rotazione completa
            repeatCount = Animation.RESTART // Ripete all'infinito
            interpolator = AccelerateInterpolator() // Movimento uniforme
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
            viewModel.loadMediaIntensita(queryStartDateString, queryEndDateString)

            viewModel.loadPercentualiGruppiMuscolariInDateRange(
                queryStartDateString,
                queryEndDateString
            )

            binding.tvMeseCorrente.text = dateRange
            viewModel.setStatusData(dateRange)
        }
        dateRangePicker.show(parentFragmentManager, "date_range_picker")
    }


}

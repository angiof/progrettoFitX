import android.app.DatePickerDialog
import android.graphics.Color
import android.graphics.Typeface
import android.os.Bundle
import android.view.LayoutInflater
import android.view.MotionEvent
import android.view.View
import android.view.ViewGroup
import android.view.animation.Animation
import android.view.animation.LinearInterpolator
import android.widget.Toast
import androidx.fragment.app.Fragment
import androidx.fragment.app.viewModels
import com.app.progrettofitx.R
import com.app.progrettofitx.data_layer.db.DB.DbFit
import com.app.progrettofitx.databinding.FragmentDashboardBinding
import com.app.progrettofitx.dominio.GruppoMuscolarePercentuale
import com.app.progrettofitx.ui.dashboard.DashViewModel
import com.app.progrettofitx.ui.dashboard.DashViewModelFactory
import com.app.progrettofitx.ui.dashboard.RotateAnimationx
import com.github.mikephil.charting.charts.PieChart
import com.github.mikephil.charting.data.Entry
import com.github.mikephil.charting.data.PieData
import com.github.mikephil.charting.data.PieDataSet
import com.github.mikephil.charting.data.PieEntry
import com.github.mikephil.charting.highlight.Highlight
import com.github.mikephil.charting.listener.ChartTouchListener
import com.github.mikephil.charting.listener.OnChartGestureListener
import com.github.mikephil.charting.listener.OnChartValueSelectedListener
import com.github.mikephil.charting.utils.ColorTemplate
import java.util.Calendar


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

        viewModel.percentualiGruppiMuscolari.observe(viewLifecycleOwner) { percentuali ->
            updatePieChart(percentuali)
        }

        binding.tvMeseCorrente.setOnClickListener {
            showDatePickerDialog()
        }
        return binding.root
    }


    private fun updatePieChart(percentuali: List<GruppoMuscolarePercentuale>) {
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

        pieChart.invalidate() // Aggiorna il grafico con i nuovi dati

        rotatePieChart(pieChart)

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

    // Uso dell'animazione nel tuo fragment
    fun rotatePieChart(pieChart: PieChart) {
        val rotateAnimation = RotateAnimationx(pieChart, 360f).apply {
            duration = 10000 // 10 secondi per una rotazione completa
            repeatCount = Animation.INFINITE // Ripete all'infinito
            interpolator = LinearInterpolator() // Movimento uniforme
        }
        pieChart.startAnimation(rotateAnimation)
    }


    fun showDatePickerDialog() {
        val calendar = Calendar.getInstance()
        val year = calendar.get(Calendar.YEAR)
        val month = calendar.get(Calendar.MONTH)
        val day = calendar.get(Calendar.DAY_OF_MONTH)

        val datePickerDialog =
            DatePickerDialog(requireContext(), R.style.DialogTheme, { _, y, m, d ->
                val selectedDate =
                    "${y}-${String.format("%02d", m + 1)}-${String.format("%02d", d)}"
                binding.tvMeseCorrente.text = "Data: "+selectedDate
            }, year, month, day)
        datePickerDialog.datePicker.maxDate = System.currentTimeMillis() - 1000
        datePickerDialog.show()
    }
}

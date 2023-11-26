import android.os.Bundle
import android.view.LayoutInflater
import android.view.MotionEvent
import android.view.View
import android.view.ViewGroup
import android.widget.Toast
import androidx.fragment.app.Fragment
import androidx.fragment.app.viewModels
import com.app.progrettofitx.data_layer.db.DB.DbFit
import com.app.progrettofitx.databinding.FragmentDashboardBinding
import com.app.progrettofitx.dominio.GruppoMuscolarePercentuale
import com.app.progrettofitx.ui.dashboard.DashViewModel
import com.app.progrettofitx.ui.dashboard.DashViewModelFactory
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

        val data = PieData(set)
        pieChart.data = data
        pieChart.invalidate() // Aggiorna il grafico con i nuovi dati

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

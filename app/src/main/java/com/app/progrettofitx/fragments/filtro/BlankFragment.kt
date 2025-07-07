package com.app.progrettofitx.fragments.filtro

import android.os.Bundle
import android.view.View
import android.view.animation.Animation
import android.widget.ArrayAdapter
import android.widget.AutoCompleteTextView
import android.widget.Toast
import androidx.fragment.app.Fragment
import androidx.lifecycle.ViewModelProvider
import androidx.recyclerview.widget.GridLayoutManager
import com.app.progrettofitx.R
import com.app.progrettofitx.R.layout
import com.app.progrettofitx.databinding.FragmentBlankBinding
import com.app.progrettofitx.ui.schede.SchedaAdapter
import java.text.DateFormat
import java.util.Calendar
import java.util.Locale

class BlankFragment : Fragment(layout.fragment_blank) {


    private var _b: FragmentBlankBinding? = null
    private val b get() = _b!!

    private lateinit var vm: BlankViewModel
    private lateinit var adapter: SchedaAdapter

    private var startDate: Long? = null
    private var endDate: Long? = null
    private var selectingStart = true

    override fun onViewCreated(v: View, s: Bundle?) {


        //test
        _b = FragmentBlankBinding.bind(v)

        // 1) ViewModel
        vm = ViewModelProvider(
            this,
            ViewModelProvider.AndroidViewModelFactory(requireActivity().application)
        ).get(BlankViewModel::class.java)

        // 2) RecyclerView + Adapter
        // 2) Adapter e GridLayoutManager a 2 colonne
        adapter = SchedaAdapter()
        b.rvSchede.layoutManager = GridLayoutManager(requireContext(), 2)
        b.rvSchede.adapter = adapter

        // 3) Osserva i dati
        vm.schede.observe(viewLifecycleOwner) { list ->
            adapter.updateList(list)
        }
        // 3) Osserva i dati


        // 4) Il tuo CalendarView range (come già avevi)
        b.calendarView.setOnDateChangeListener { _, y, m, d ->
            val cal = Calendar.getInstance().apply {
                set(y, m, d, 0, 0, 0)
                set(Calendar.MILLISECOND, 0)
            }
            val millis = cal.timeInMillis
            if (selectingStart) {
                startDate = millis; endDate = null; selectingStart = false
            } else {
                endDate = millis; selectingStart = true
            }
            // Formato Italiano
            val fmt = DateFormat.getDateInstance(DateFormat.SHORT, Locale("it", "IT"))
            val sTxt = startDate?.let { fmt.format(it) } ?: "-"
            val eTxt = endDate?.let { fmt.format(it) } ?: "-"
            Toast.makeText(requireContext(), "Da $sTxt a $eTxt", Toast.LENGTH_SHORT).show()
        }

        // 5) Filtri: se hai startDate/endDate, puoi ricaricare il ViewModel
        b.applyFiltersButton.setOnClickListener {
            if (startDate != null && endDate != null) {
                vm.loadSchedeInDateRange(startDate!!, endDate!!)
            } else {
                vm.loadSchede()
            }
            // Chiudi i filtri e torna alla lista
            b.layoutSchede.visibility = View.VISIBLE
            b.layoutfilter.visibility = View.GONE
            b.btnFilter.visibility = View.VISIBLE
            b.btnCloseFilter.visibility = View.GONE
        }
        b.clearFiltersButton.setOnClickListener {
            startDate = null; endDate = null; selectingStart = true
            vm.loadSchede()
            Toast.makeText(requireContext(), "Filtro rimosso", Toast.LENGTH_SHORT).show()
        }


        swithScreen()

        autocomplete()





        setupDropdown(
            b.edIntensita,
            b.layoutIntensita,
            listOf(
                "Alta - Molto intensa",
                "Media - Allenamento standard",
                "Bassa - Leggera o recupero",
                "Cardio - Resistenza o attività aerobica"
            )
        )


        setupDropdown(
            b.gruppuMuscolari,
            b.layotGruppoMuscolare,
            listOf(
                "Dorsali",
                "Petorali",
                "Gambe",
                "Spalle",
                "Bicipiti",
                "Tricipiti",
                "Addominali",
                "Cardio",
                "Full Body",
                "Altro"
            )
        )

    }


    fun swithScreen() {
        b.btnFilter.setOnClickListener {
            b.layoutSchede.visibility = View.GONE
            b.layoutfilter.visibility = View.VISIBLE
            b.btnFilter.visibility = View.GONE
            b.btnCloseFilter.visibility = View.VISIBLE
        }

        b.btnCloseFilter.setOnClickListener {
            b.layoutSchede.visibility = View.VISIBLE
            b.layoutfilter.visibility = View.GONE
            b.btnFilter.visibility = View.VISIBLE
            b.btnCloseFilter.visibility = View.GONE
        }
    }


    private fun setupDropdown(
        textView: AutoCompleteTextView,
        containerLayout: View,
        items: List<String>
    ) {
        val adapter = ArrayAdapter(
            requireContext(),
            layout.layout_custom_line_drop,
            items
        )
        textView.setAdapter(adapter)

        containerLayout.setOnClickListener {
            textView.showDropDown()
        }
    }


    fun autocomplete(){
        vm.schede.observe(viewLifecycleOwner) { list ->
            adapter.updateList(list)

            val titoli = list.map { it.titolo }
            val titleAdapter = ArrayAdapter(
                requireContext(),
                android.R.layout.simple_dropdown_item_1line,
                titoli
            )
            b.edTitle.setAdapter(titleAdapter)
        }

        b.layoutCOmplete.setEndIconOnClickListener {
            b.edTitle.showDropDown()
        }
    }

    override fun onDestroyView() {
        super.onDestroyView()
        _b = null
    }
}
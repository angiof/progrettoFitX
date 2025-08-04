package com.app.progrettofitx.fragments.filtro

import android.app.AlertDialog
import android.content.Context
import android.content.Intent
import android.os.Bundle
import android.view.View
import android.widget.ArrayAdapter
import android.widget.AutoCompleteTextView
import android.widget.Toast
import androidx.fragment.app.Fragment
import androidx.lifecycle.ViewModelProvider
import androidx.lifecycle.lifecycleScope
import androidx.navigation.fragment.findNavController
import androidx.recyclerview.widget.GridLayoutManager
import com.app.progrettofitx.R
import com.app.progrettofitx.data_layer.db.DB.DbFit
import com.app.progrettofitx.data_layer.db.repos.EsserciziRepository
import com.app.progrettofitx.databinding.FragmentBlankBinding
import com.app.progrettofitx.dominio.UsesCasesEssercissi
import com.app.progrettofitx.ui.factory.GenericViewModelFactory
import com.app.progrettofitx.ui.forms.AcitivySheda
import com.app.progrettofitx.fragments.filtro.adapter.SchedaAdapter
import com.app.progrettofitx.ui.shedeForms.EsserciziViewModel
import kotlinx.coroutines.launch
import java.text.DateFormat
import java.util.Calendar
import java.util.Locale

class ShedeFragments : Fragment(R.layout.fragment_blank) {

    private var _b: FragmentBlankBinding? = null
    private val b get() = _b!!

    private lateinit var vmSchede: BlankViewModel
    private lateinit var vmEs: EsserciziViewModel
    private lateinit var adapter: SchedaAdapter

    private var startDate: Long? = null
    private var endDate: Long? = null
    private var selectingStart = true


    override fun onAttach(context: Context) {
        super.onAttach(context)

    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        _b = FragmentBlankBinding.bind(view)

        // ViewModels
        vmSchede = ViewModelProvider(
            this, ViewModelProvider.AndroidViewModelFactory(requireActivity().application)
        )[BlankViewModel::class.java]

        vmEs = ViewModelProvider(
            this,
            GenericViewModelFactory {
                EsserciziViewModel(
                    UsesCasesEssercissi(
                        EsserciziRepository(
                            DbFit.getDatabase(requireContext()).essercissiDao()
                        )
                    )
                )
            }
        )[EsserciziViewModel::class.java]

        onApriSchedeClicked()


        // Adapter con click, long-click, count e favorite
        adapter = SchedaAdapter(
            onItemClick = { scheda ->
                startActivity(
                    Intent(requireContext(), AcitivySheda::class.java)
                        .putExtra("isNew", false)
                        .putExtra("f", scheda)
                )
            },
            onItemLongClick = { scheda ->
                AlertDialog.Builder(requireContext())
                    .setTitle("Elimina scheda")
                    .setMessage("Vuoi davvero eliminare “${scheda.titolo}”?")
                    .setNegativeButton("Annulla", null)
                    .setPositiveButton("Elimina") { _, _ ->
                        vmSchede.deleteScheda(scheda)
                    }
                    .show()
            },
            getCount = { id -> vmEs.getTotalEss(id) },
            onToggleFavorite = { scheda, fav ->
                vmSchede.setFavorite(scheda.id!!, fav)
            }
        )

        // RecyclerView
        b.rvSchede.layoutManager = GridLayoutManager(requireContext(), 2)
        b.rvSchede.adapter = adapter

        // Osservo la lista e la passo all’adapter
        vmSchede.schede.observe(viewLifecycleOwner) { list ->
            adapter.submitList(list)
        }

        // --- LOGICA FILTRI ---

        // 1) Selezione range date dal CalendarView
        b.calendarView.setOnDateChangeListener { _, y, m, d ->
            val cal = Calendar.getInstance().apply {
                set(y, m, d, 0, 0, 0)
                set(Calendar.MILLISECOND, 0)
            }
            val millis = cal.timeInMillis
            if (selectingStart) {
                startDate = millis
                endDate = null
                selectingStart = false
            } else {
                endDate = millis
                selectingStart = true
            }
            val fmt = DateFormat.getDateInstance(DateFormat.SHORT, Locale("it", "IT"))
            Toast.makeText(
                requireContext(),
                "Da ${startDate?.let { fmt.format(it) } ?: "-"} a ${endDate?.let { fmt.format(it) } ?: "-"}",
                Toast.LENGTH_SHORT
            ).show()
        }

        // 2) Applica filtri
        b.applyFiltersButton.setOnClickListener {
            if (startDate != null && endDate != null) {
                vmSchede.loadSchedeInDateRange(startDate!!, endDate!!)
            } else {
                vmSchede.loadSchede()
            }
            // chiudo pannello filtri
            b.layoutSchede.visibility = View.VISIBLE
            b.layoutfilter.visibility = View.GONE
            b.btnFilter.visibility = View.VISIBLE
            b.btnCloseFilter.visibility = View.GONE
        }

        // 3) Cancella filtri
        b.clearFiltersButton.setOnClickListener {
            startDate = null; endDate = null; selectingStart = true
            vmSchede.loadSchede()
            Toast.makeText(requireContext(), "Filtro rimosso", Toast.LENGTH_SHORT).show()
        }

        // 4) Toggle visibilità pannello filtri
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

        // 5) Autocomplete titolo scheda
        vmSchede.schede.observe(viewLifecycleOwner) { list ->
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

        // 6) Dropdown per intensità e gruppi
        setupDropdown(
            b.edIntensita, b.layoutIntensita, listOf(
                "Alta - Molto intensa",
                "Media - Allenamento standard",
                "Bassa - Leggera o recupero",
                "Cardio - Resistenza o attività aerobica"
            )
        )
        setupDropdown(
            b.gruppuMuscolari, b.layotGruppoMuscolare, listOf(
                "Dorsali", "Petorali", "Gambe", "Spalle",
                "Bicipiti", "Tricipiti", "Addominali", "Cardio",
                "Full Body", "Altro"
            )
        )
    }

    private fun setupDropdown(
        textView: AutoCompleteTextView,
        containerLayout: View,
        items: List<String>
    ) {
        val ddAdapter = ArrayAdapter(
            requireContext(),
            R.layout.layout_custom_line_drop,
            items
        )
        textView.setAdapter(ddAdapter)
        containerLayout.setOnClickListener { textView.showDropDown() }
    }

    override fun onDestroyView() {
        super.onDestroyView()
        _b = null
    }


    fun onApriSchedeClicked() {
        lifecycleScope.launch {
            val cnt = vmSchede.getSchedeCount()
            if (cnt == 0) {
                AlertDialog.Builder(requireContext())
                    .setTitle("Nessuna scheda trovata")
                    .setMessage("Non hai ancora creato nessuna scheda. Vuoi crearne una nuova?")
                    .setNegativeButton("No", null)
                    .setPositiveButton("Sì") { _, _ ->
                        // naviga al fragment di creazione, dentro lo stesso NavHost
                        findNavController().navigate(R.id.placeholderFragment)
                    }
                    .show()
            } else {
                // apri AcitivySheda con lista esistente
                startActivity(
                    Intent(requireContext(), AcitivySheda::class.java)
                        .putExtra("isNew", false)
                )
            }
        }
    }
}
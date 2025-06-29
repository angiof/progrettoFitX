package com.app.progrettofitx.ui.shedeForms

import android.app.DatePickerDialog
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ArrayAdapter
import android.widget.AutoCompleteTextView
import android.widget.Toast
import androidx.fragment.app.Fragment
import androidx.lifecycle.ViewModelProvider
import androidx.lifecycle.lifecycleScope
import androidx.lifecycle.viewModelScope
import androidx.navigation.fragment.findNavController
import com.app.progrettofitx.R
import com.app.progrettofitx.R.*
import com.app.progrettofitx.data_layer.db.DB.DbFit
import com.app.progrettofitx.data_layer.db.SchedeEntity
import com.app.progrettofitx.databinding.FragmentBaseAcitivityBinding
import com.app.progrettofitx.dominio.UsesCasesSheda
import com.app.progrettofitx.ui.factory.GenericViewModelFactory
import com.app.progrettofitx.ui.forms.BaseAcitivity
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.coroutineScope
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext
import java.util.Calendar


class FragCreateSchedeForm : Fragment() {
    private lateinit var binding: FragmentBaseAcitivityBinding
    private var shedaForm: SchedeEntity? = null
    private lateinit var viewModel: SchedeViewModel
    private var existingRecordId: Int? = null


    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {


        viewModel = ViewModelProvider(this, GenericViewModelFactory {
            SchedeViewModel(
                activity?.application!!,
                UsesCasesSheda(SchedeRepository(DbFit.getDatabase(requireContext()).schedeDao()))
            )
        })[SchedeViewModel::class.java]

        binding = FragmentBaseAcitivityBinding.inflate(inflater, container, false)
        return binding.root
    }




    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)



        // Ripristina lo stato da savedInstanceState
        savedInstanceState?.let {
            existingRecordId = it.getInt("existingRecordId", -1)
            if (existingRecordId == -1) existingRecordId = null
        }

        binding.edEventData.setOnClickListener {
            showDatePickerDialog()
        }
        setupDropdown(
            binding.gruppuMuscolari,
            binding.layoutSpinnerCompleteGruppiMuscolari,
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

        binding.listaAttrezziTxt.setOnClickListener {
            setupDropdown(
                binding.listaAttrezziTxt,
                binding.listaAttrezziLayout,
                listOf(
                    "Manubrio",
                    "Bilanciere",
                    "Bilanciere EZ",
                    "Kettlebell",
                    "Palla Medica",
                    "Elastico",
                    "Corda per saltare",
                    "FatGrip",
                    "Catene",
                    "Sbarra per trazioni",
                    "Anelli da ginnastica",
                    "Parallele",
                    "Box pliometrico",
                    "Power Rack",
                    "Panca piana",
                    "Panca inclinata",
                    "Leg Press",
                    "Lat Machine",
                    "Pectoral Machine",
                    "Shoulder Press",
                    "Chest Press",
                    "Leg Extension",
                    "Leg Curl",
                    "Calf Machine",
                    "Macchina cavi",
                    "Vogatore",
                    "Air Bike",
                    "GHD",
                    "Tapis Roulant",
                    "Cyclette",
                    "Ellittica",
                    "Stepper",
                    "Sacco da boxe",
                    "Punching ball",
                    "Corda per saltare",
                    "Corpo libero",
                    "Macchinario Bicipiti",
                    "Macchinario Tricipiti",
                    "Macchinario Gambe",
                    "Macchinario Glutei",
                    "Altro",
                    "Nessuno"
                )
            )        }

        lifecycleScope.launch(Dispatchers.IO) {
            checkInputs()
        }
    }

    private fun showDatePickerDialog() {
        val calendar = Calendar.getInstance()
        val year = calendar.get(Calendar.YEAR)
        val month = calendar.get(Calendar.MONTH)
        val day = calendar.get(Calendar.DAY_OF_MONTH)

        val datePickerDialog =
            DatePickerDialog(requireContext(), style.DialogTheme, { _, y, m, d ->
                val selectedDate =
                    "${y}-${String.format("%02d", m + 1)}-${String.format("%02d", d)}"
                binding.edEventData.setText(selectedDate)
            }, year, month, day)
        datePickerDialog.datePicker.minDate = System.currentTimeMillis() - 1000
        datePickerDialog.show()
    }


    suspend fun checkInputs() = coroutineScope {

        binding.btnNext.setOnClickListener {

            val data: String = binding.edEventData.text.toString()
            val titolo: String = binding.edTitle.text.toString()
            val notes: String = binding.edNotes.text.toString()
            val intensita: String = binding.edIntensita.text.toString()
            val gruppoMuscolare: String = binding.gruppuMuscolari.text.toString()

            if (checkStrings(data, intensita, gruppoMuscolare, titolo)) {
                shedaForm = SchedeEntity(
                    gruppoMuscolare = gruppoMuscolare,
                    intesita = intensita,
                    notes = notes,
                    titolo = titolo,
                    data = data
                )

                viewModel.viewModelScope.launch(Dispatchers.IO) {
                    if (existingRecordId == null) {
                        // Inserisci un nuovo record e ottieni l'ID
                        val newSchedeId = viewModel.insert(shedaForm!!).toInt()
                        shedaForm!!.id = newSchedeId
                        existingRecordId = newSchedeId
                    } else {
                        // Aggiorna il record esistente
                        shedaForm!!.id = existingRecordId!!
                        viewModel.update(shedaForm!!)
                    }

                    withContext(Dispatchers.Main) {
                        val shedaFormBundle = Bundle().apply {
                            putSerializable("f", shedaForm)
                        }
                        findNavController().navigate(R.id.fragEssercissi, shedaFormBundle)
                    }
                }
            } else {
                Toast.makeText(requireContext(), "c'è un campo vuoto", Toast.LENGTH_SHORT).show()
            }
        }
    }

    private fun checkStrings(vararg strings: String?): Boolean {
        for (str in strings) {
            if (str == null || str.trim().isEmpty()) {
                return false
            }
        }
        return true
    }

    override fun onSaveInstanceState(outState: Bundle) {
        super.onSaveInstanceState(outState)
        outState.putInt("existingRecordId", existingRecordId ?: -1)
    }

    override fun onResume() {
        super.onResume()

        setupDropdown(
            binding.edIntensita,
            binding.layoutSpinnerInsita,
            listOf(
                "Alta - Molto intensa",
                "Media - Allenamento standard",
                "Bassa - Leggera o recupero",
                "Cardio - Resistenza o attività aerobica"
            )
        )

        (activity as? BaseAcitivity)?.apply {
            this.selectTab(0)
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

}


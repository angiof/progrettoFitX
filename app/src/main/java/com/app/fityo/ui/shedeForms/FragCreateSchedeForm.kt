package com.app.fityo.ui.shedeForms

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
import com.app.fityo.R
import com.app.fityo.R.*
import com.app.fityo.data_layer.db.DB.DbFit
import com.app.fityo.data_layer.db.SchedeEntity
import com.app.fityo.databinding.FragmentBaseAcitivityBinding
import com.app.fityo.dominio.UsesCasesSheda
import com.app.fityo.data_layer.repository.SchedeRepository
import com.app.fityo.ui.factory.GenericViewModelFactory
import com.app.fityo.ui.forms.BaseAcitivity
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.coroutineScope
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext
import com.google.android.material.datepicker.MaterialDatePicker
import java.time.Instant
import java.time.LocalDate
import java.time.ZoneId
import java.time.format.DateTimeFormatter


class FragCreateSchedeForm : Fragment() {
    private lateinit var binding: FragmentBaseAcitivityBinding
    private var shedaForm: SchedeEntity? = null
    private lateinit var viewModel: SchedeViewModel
    private var existingRecordId: Int? = null
    private val isoFormatter: DateTimeFormatter = DateTimeFormatter.ISO_LOCAL_DATE
    private val selectedMuscleGroups = mutableSetOf<String>()


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

        binding.edEventData.setOnClickListener { showDatePickerDialog() }

        binding.layoutSpinnerCompleteGruppiMuscolari.setOnClickListener {
            showMultiSelectMuscleGroupsDialog()
        }

        binding.gruppuMuscolari.apply {
            isFocusable = false
            isClickable = true
            setOnClickListener {
                showMultiSelectMuscleGroupsDialog()
            }
        }

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
            )
        }

        lifecycleScope.launch(Dispatchers.IO) {
            checkInputs()
        }
    }

    private fun showMultiSelectMuscleGroupsDialog() {
        val muscleGroups = listOf(
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

        val dialogView = LayoutInflater.from(requireContext())
            .inflate(R.layout.dialog_multi_select_groups, null, false)

        val chipGroup = dialogView.findViewById<com.google.android.material.chip.ChipGroup>(
            R.id.chipGroupMuscleGroups
        )

        muscleGroups.forEach { group ->
            val chip = com.google.android.material.chip.Chip(requireContext()).apply {
                text = group
                isCheckable = true
                isChecked = selectedMuscleGroups.contains(group)
                setChipBackgroundColorResource(R.color.second)
                setTextColor(resources.getColor(R.color.text_primary, null))
            }
            chipGroup.addView(chip)
        }

        com.google.android.material.dialog.MaterialAlertDialogBuilder(requireContext())
            .setView(dialogView)
            .setPositiveButton(android.R.string.ok) { _, _ ->
                selectedMuscleGroups.clear()
                for (i in 0 until chipGroup.childCount) {
                    val chip = chipGroup.getChildAt(i) as com.google.android.material.chip.Chip
                    if (chip.isChecked) {
                        selectedMuscleGroups.add(chip.text.toString())
                    }
                }
                updateMuscleGroupsDisplay()
            }
            .setNegativeButton(android.R.string.cancel, null)
            .show()
    }

    private fun updateMuscleGroupsDisplay() {
        binding.gruppuMuscolari.setText(
            if (selectedMuscleGroups.isEmpty()) {
                ""
            } else {
                selectedMuscleGroups.joinToString(", ")
            }
        )
    }

    private fun showDatePickerDialog() {
        val currentDateMillis = binding.edEventData.text?.toString()?.takeIf { it.isNotBlank() }
            ?.let { existing ->
                runCatching { LocalDate.parse(existing, isoFormatter) }
                    .getOrNull()
                    ?.atStartOfDay(ZoneId.systemDefault())
                    ?.toInstant()
                    ?.toEpochMilli()
            }
            ?: MaterialDatePicker.todayInUtcMilliseconds()

        val picker = MaterialDatePicker.Builder.datePicker()
            .setTitleText(getString(R.string.inserisce_data))
            .setTheme(R.style.CustomDatePicker)
            .setSelection(currentDateMillis)
            .build()

        picker.addOnPositiveButtonClickListener { selection ->
            val date = Instant.ofEpochMilli(selection)
                .atZone(ZoneId.systemDefault())
                .toLocalDate()
            binding.edEventData.setText(date.format(isoFormatter))
        }

        picker.show(parentFragmentManager, "create_date_picker")
    }


    suspend fun checkInputs() = coroutineScope {

        binding.btnNext.setOnClickListener {

            val data: String = binding.edEventData.text.toString()
            val titolo: String = binding.edTitle.text.toString()
            val notes: String = binding.edNotes.text.toString()
            val intensita: String = binding.edIntensita.text.toString()
            val gruppoMuscolare: String = binding.gruppuMuscolari.text.toString()

            if (checkStrings(data, intensita, gruppoMuscolare, titolo) ||
                (selectedMuscleGroups.isNotEmpty() && checkStrings(data, intensita, titolo))) {
                shedaForm = SchedeEntity(
                    gruppoMuscolare = if (selectedMuscleGroups.isNotEmpty())
                        selectedMuscleGroups.first()
                    else
                        gruppoMuscolare,
                    gruppiMuscolari = if (selectedMuscleGroups.size > 1)
                        selectedMuscleGroups.toList()
                    else
                        null,
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
                        val bundle = Bundle().apply {
                            putSerializable("f", shedaForm)
                            putBoolean("isNew", true)
                        }
                        findNavController().navigate(R.id.fragEssercissi, bundle)
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


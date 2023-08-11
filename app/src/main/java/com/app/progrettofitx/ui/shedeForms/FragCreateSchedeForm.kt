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
import androidx.navigation.fragment.findNavController
import com.app.progrettofitx.R
import com.app.progrettofitx.databinding.FragmentBaseAcitivityBinding
import com.app.progrettofitx.db.SchedeEntity
import com.app.progrettofitx.ui.forms.BaseAcitivity
import java.util.Calendar


class FragCreateSchedeForm : Fragment() {
    private lateinit var binding: FragmentBaseAcitivityBinding
    private var shedaForm: SchedeEntity? = null

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {

        binding = FragmentBaseAcitivityBinding.inflate(inflater, container, false)
        return binding.root
    }


    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        binding.edEventData.setOnClickListener {
            showDatePickerDialog()
        }
        //da rinominare
        gruppiMuscolari()
        populateAutoCompleteMusocli()

        checkInputs()

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
                binding.edEventData.setText(selectedDate)
            }, year, month, day)
        datePickerDialog.datePicker.minDate = System.currentTimeMillis() - 1000
        datePickerDialog.show()
    }


    fun gruppiMuscolari() {
        val items = listOf("spalle", "petto", "gambe", "braccia")

        val adapter = ArrayAdapter(
            requireContext(),
            androidx.appcompat.R.layout.support_simple_spinner_dropdown_item,
            items
        )
        (binding.gruppuMuscolari as? AutoCompleteTextView)?.setAdapter(adapter)

        binding.layoutSpinnerCompleteGruppiMuscolari.setEndIconOnClickListener {
            (binding.gruppuMuscolari as? AutoCompleteTextView)?.showDropDown()

        }
    }

    fun populateAutoCompleteMusocli() {
        val items = listOf("Alta", "Media", "Bassa", "Cardio")

        val adapter = ArrayAdapter(
            requireContext(),
            androidx.appcompat.R.layout.support_simple_spinner_dropdown_item,
            items
        )
        (binding.edIntensita as? AutoCompleteTextView)?.setAdapter(adapter)

        binding.layoutSpinnerInsita.setEndIconOnClickListener {
            (binding.edIntensita as? AutoCompleteTextView)?.showDropDown()
        }
    }


    fun checkInputs() {

        binding.btnNext.setOnClickListener {

            val data: String = binding.edEventData.text.toString()
            val titolo: String = binding.edTitle.text.toString()
            val notes: String? = binding.edNotes.text.toString()
            val intensita: String = binding.edIntensita.text.toString()
            val gruppoMuscolare: String = binding.gruppuMuscolari.text.toString()

            if (checkStrings(data, intensita, notes, gruppoMuscolare, titolo)) {
                shedaForm = SchedeEntity(
                    gruppoMuscolare = gruppoMuscolare,
                    intesita = intensita,
                    notes = notes,
                    titolo = titolo,
                    data = data
                )

                val shedaFormBundle = Bundle().apply {
                putSerializable("f",shedaForm)
                }

                findNavController().navigate(R.id.fragEssercissi, shedaFormBundle)
            } else {
                Toast.makeText(requireContext(), "cè un campo vuoto", Toast.LENGTH_SHORT).show()
            }
        }
    }


    fun checkStrings(vararg strings: String?): Boolean {
        for (str in strings) {
            if (str == null || str.trim().isEmpty()) {
                return false
            }
        }
        return true
    }


    override fun onResume() {
        super.onResume()
        (activity as? BaseAcitivity)?.selectTab(0)
    }
}

package com.app.progrettofitx.ui.sheet

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ArrayAdapter
import android.widget.AutoCompleteTextView
import androidx.appcompat.R
import androidx.lifecycle.ViewModelProvider
import androidx.lifecycle.lifecycleScope
import androidx.lifecycle.viewModelScope
import com.app.progrettofitx.data_layer.db.DB.DbFit
import com.app.progrettofitx.data_layer.db.EsserciziEntity
import com.app.progrettofitx.data_layer.db.repos.EsserciziRepository
import com.app.progrettofitx.databinding.BottomSheetLayoutBinding
import com.app.progrettofitx.dominio.UsesCasesEssercissi
import com.app.progrettofitx.ui.factory.GenericViewModelFactory
import com.app.progrettofitx.ui.shedeForms.EsserciziViewModel
import com.google.android.material.bottomsheet.BottomSheetDialogFragment
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch

class MyBottomSheetFragment(private val idScheda: Int) : BottomSheetDialogFragment() {
    private var nome: String? = null
    private var nSerie: Int? = null
    private var nIntervallo: Int? = null
    private var isometria: Int? = null
    private lateinit var viewModel: EsserciziViewModel

    private val binding: BottomSheetLayoutBinding by lazy {
        BottomSheetLayoutBinding.inflate(
            layoutInflater
        )
    }

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        listaAttrezzi()
        setInfoAndSave()


        val dao = DbFit.getDatabase(requireContext()).essercissiDao()
        val esserciziRepository = EsserciziRepository(dao)
        val getEserciziByIdUseCase = UsesCasesEssercissi(esserciziRepository)

        // Inizializza il ViewModel tramite la Factory
        val viewModelFactory = GenericViewModelFactory {
            EsserciziViewModel(getEserciziByIdUseCase)
        }

        viewModel = ViewModelProvider(this, viewModelFactory!!).get(EsserciziViewModel::class.java)


        return binding.root

    }

    private fun setInfoAndSave() {

        binding.layoutEssercissiSheet.bntSave.setOnClickListener { view ->

            isometria = binding.layoutEssercissiSheet.edIsometria.text.toString().toInt()

            nome = binding.layoutEssercissiSheet.edNome.text.toString()
            nSerie = binding.layoutEssercissiSheet.edSerie.text.toString().toInt()
            nIntervallo = binding.layoutEssercissiSheet.edRiposo.text.toString().toInt()

            viewModel.viewModelScope.launch(Dispatchers.IO) {
                viewModel.insert(
                    EsserciziEntity(
                        nome = nome!!,
                        nRipetizione = nSerie!!,
                        intervallo = nIntervallo,
                        insometria = isometria,
                        schedaId = idScheda
                    )
                )
            }

            lifecycleScope.launch(Dispatchers.Main) {
                dismiss()
            }
        }
    }

    private fun listaAttrezzi() {
        val items = listOf("Manubrio", "Bilanciere", "Elastico", "FatGrip", "Nessuno")

        val adapter = ArrayAdapter(
            requireContext(),
            R.layout.support_simple_spinner_dropdown_item,
            items
        )
        (binding.layoutEssercissiSheet.listaAttrezziTxtx as? AutoCompleteTextView)?.setAdapter(
            adapter
        )

        // binding.layoutSpinnerCompleteGruppiMuscolari.setEndIconOnClickListener {
        (binding.layoutEssercissiSheet.listaAttrezziTxtx as? AutoCompleteTextView)?.showDropDown()
    }
}




package com.app.progrettofitx.ui.sheet

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.viewModels
import androidx.lifecycle.ViewModel
import androidx.lifecycle.lifecycleScope
import androidx.lifecycle.viewModelScope
import com.app.progrettofitx.databinding.BottomSheetLayoutBinding
import com.app.progrettofitx.db.DB.DbFit
import com.app.progrettofitx.db.EsserciziEntity
import com.app.progrettofitx.ui.shedeForms.EsserciziViewModel
import com.app.progrettofitx.ui.shedeForms.SchedeViewModel
import com.google.android.material.bottomsheet.BottomSheetDialogFragment
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.GlobalScope
import kotlinx.coroutines.launch

class MyBottomSheetFragment(private val idScheda:Int) : BottomSheetDialogFragment() {
    private var nome: String? = null
    private var nSerie: Int? = null
    private var nIntervallo: Int? = null
    private var isometria: Int? = null
    private val viewModel: EsserciziViewModel by viewModels()

    private val binding: BottomSheetLayoutBinding by lazy {
        BottomSheetLayoutBinding.inflate(
            layoutInflater
        )
    }

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {

        setInfoAndSave()

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
}

package com.app.progrettofitx.ui.sheet

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.lifecycle.lifecycleScope
import com.app.progrettofitx.databinding.BottomSheetLayoutBinding
import com.app.progrettofitx.db.DB.DbFit
import com.app.progrettofitx.db.EsserciziEntity
import com.google.android.material.bottomsheet.BottomSheetDialogFragment
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.GlobalScope
import kotlinx.coroutines.launch

class MyBottomSheetFragment : BottomSheetDialogFragment() {
    private var nome: String? = null
    private var nSerie: Int? = null
    private var nIntervallo: Int? = null
    private var isometria: Int? = null
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
            nome = binding.layoutEssercissiSheet.edNome.text.toString()
            nSerie = binding.layoutEssercissiSheet.edSerie.text.toString().toInt()
            nIntervallo = binding.layoutEssercissiSheet.edRiposo.text.toString().toInt()
            isometria = binding.layoutEssercissiSheet.edIsometria.text.toString().toInt()

            lifecycleScope.launch(Dispatchers.IO) {
                DbFit.getDatabase(requireContext()).essercissiDao()
                    .insert(
                        EsserciziEntity(
                            0,
                            nome!!,
                            nSerie!!,
                            nIntervallo,
                            isometria
                        )
                    )
            }
            lifecycleScope.launch(Dispatchers.Main) {
                dismiss()
            }
        }

    }
}

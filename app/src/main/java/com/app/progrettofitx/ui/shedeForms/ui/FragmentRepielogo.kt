package com.app.progrettofitx.ui.shedeForms.ui

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Toast
import androidx.fragment.app.Fragment
import androidx.fragment.app.viewModels
import androidx.lifecycle.lifecycleScope
import com.app.progrettofitx.R
import com.app.progrettofitx.databinding.FragmentFragmentRepielogoBinding
import com.app.progrettofitx.db.SchedeEntity
import com.app.progrettofitx.ui.shedeForms.EsserciziViewModel
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext

class FragmentRepielogo : Fragment() {
    private lateinit var binding: FragmentFragmentRepielogoBinding
    private val viewModel: EsserciziViewModel by viewModels()


    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        binding = FragmentFragmentRepielogoBinding.inflate(inflater, container, false)
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        arguments?.let {
            val schedeEntity = it.getSerializable("schedeEntity") as SchedeEntity
            // Fai qualcosa con schedeEntity
            binding.layoutRe.titoloRe.text = schedeEntity.titolo
            binding.layoutRe.intensitaTxt.text = schedeEntity.intesita
            binding.layoutRe.ruppoMusResTxt.text = schedeEntity.gruppoMuscolare
            binding.layoutRe.dataResTxt.text= schedeEntity.data.toString()
            binding.layoutRe.lyBtn.bntSaveInc.text= getString(R.string.salva_ed_esci).apply {
                Toast.makeText(requireContext(), "uscire", Toast.LENGTH_SHORT).show()
            }
            lifecycleScope.launch(Dispatchers.IO) {

                val tot = viewModel.getTotalEss(schedeEntity.id!!)
                withContext(Dispatchers.Main) {
                    binding.layoutRe.totaleEsResTxt.text = tot.toString()
                }
            }
        }
        super.onViewCreated(view, savedInstanceState)
    }
}
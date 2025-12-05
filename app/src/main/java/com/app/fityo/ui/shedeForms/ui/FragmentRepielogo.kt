package com.app.fityo.ui.shedeForms.ui

import android.app.TimePickerDialog
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Toast
import androidx.fragment.app.Fragment
import androidx.lifecycle.ViewModelProvider
import androidx.lifecycle.lifecycleScope
import androidx.lifecycle.viewModelScope
import com.app.fityo.R
import com.app.fityo.data_layer.db.DB.DbFit
import com.app.fityo.data_layer.db.SchedeEntity
import com.app.fityo.data_layer.db.repos.EsserciziRepository
import com.app.fityo.databinding.FragmentFragmentRepielogoBinding
import com.app.fityo.dominio.UsesCasesEssercissi
import com.app.fityo.dominio.UsesCasesSheda
import com.app.fityo.ui.factory.GenericViewModelFactory
import com.app.fityo.ui.forms.BaseAcitivity
import com.app.fityo.ui.shedeForms.EsserciziViewModel
import com.app.fityo.data_layer.repository.SchedeRepository
import com.app.fityo.ui.shedeForms.SchedeViewModel
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext

class FragmentRepielogo : Fragment() {
    private lateinit var binding: FragmentFragmentRepielogoBinding
    private lateinit var viewModel: EsserciziViewModel
    private var ora: Int? = null
    private lateinit var viewModel2: SchedeViewModel
    private var minuti: Int? = null
    private var schedeEntity :SchedeEntity? =null
    var selectedTime: String? = null
    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        binding = FragmentFragmentRepielogoBinding.inflate(inflater, container, false)

        // Inizializza il ViewModel tramite la Factory
        viewModel = ViewModelProvider(this, GenericViewModelFactory {
            EsserciziViewModel(
                UsesCasesEssercissi(
                    EsserciziRepository(
                        DbFit.getDatabase(
                            requireContext()
                        ).essercissiDao()
                    )
                )
            )
        })[EsserciziViewModel::class.java]

        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        arguments?.let {
             schedeEntity = it.getSerializable("schedeEntity") as SchedeEntity
            // Popola i campi con i dati della scheda
            binding.layoutRe.titoloRe.text = schedeEntity?.titolo
            binding.layoutRe.intensitaTxt.text = schedeEntity?.intesita
            binding.layoutRe.ruppoMusResTxt.text = schedeEntity?.getGruppiMuscolariDisplay() ?: schedeEntity?.gruppoMuscolare
            binding.layoutRe.dataResTxt.text = schedeEntity?.data.toString()
            binding.layoutRe.lyBtn.bntSaveInc.text = getString(R.string.salva_ed_esci)

            lifecycleScope.launch(Dispatchers.IO) {
                val tot = viewModel.getTotalEss(schedeEntity?.id!!)
                withContext(Dispatchers.Main) {
                    binding.layoutRe.totaleEsResTxt.text = tot.toString()
                }
            }
        }


        binding.layoutRe.checkYes.setOnCheckedChangeListener { buttonView, isChecked ->
            if (isChecked) {
                val timePickerDialog = TimePickerDialog(
                    requireContext(),
                    { view, hourOfDay, minute ->

                        selectedTime = String.format("%02d:%02d", hourOfDay, minute)

                    },
                    12, 0, true
                )
                timePickerDialog.show()
            }
        }


        binding.layoutRe.lyBtn.bntSaveInc.setOnClickListener {

            viewModel2 = ViewModelProvider(this, GenericViewModelFactory {
                SchedeViewModel(
                    activity?.application!!,
                    UsesCasesSheda(SchedeRepository(DbFit.getDatabase(requireContext()).schedeDao()))
                )
            })[SchedeViewModel::class.java]
            viewModel2.viewModelScope.launch (Dispatchers.IO){
                schedeEntity?.id?.let { it1 -> selectedTime?.let { it2 ->
                    viewModel2.updateTime(it1,
                        it2
                    )
                } }
            }

            activity?.finish()
        }

        super.onViewCreated(view, savedInstanceState)
    }


    override fun onResume() {
        super.onResume()
        (activity as? BaseAcitivity)?.selectTab(2)
    }
}

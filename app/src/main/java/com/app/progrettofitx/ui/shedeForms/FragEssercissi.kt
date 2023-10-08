package com.app.progrettofitx.ui.shedeForms

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.LinearLayout
import androidx.fragment.app.Fragment
import androidx.lifecycle.ViewModelProvider
import androidx.lifecycle.lifecycleScope
import androidx.navigation.fragment.findNavController
import androidx.recyclerview.widget.DividerItemDecoration
import androidx.recyclerview.widget.LinearLayoutManager
import com.app.progrettofitx.R
import com.app.progrettofitx.data_layer.db.DB.DbFit
import com.app.progrettofitx.data_layer.db.SchedeEntity
import com.app.progrettofitx.data_layer.db.repos.EsserciziRepository
import com.app.progrettofitx.databinding.FragmentFragEssercissiBinding
import com.app.progrettofitx.dominio.UsesCasesEssercissi
import com.app.progrettofitx.ui.factory.GenericViewModelFactory
import com.app.progrettofitx.ui.forms.BaseAcitivity
import com.app.progrettofitx.ui.shedeForms.recyclreview.EserciziAdapter
import com.app.progrettofitx.ui.sheet.MyBottomSheetFragment
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch

class FragEssercissi : Fragment() {
    private lateinit var binding: FragmentFragEssercissiBinding
    private lateinit var adapterx: EserciziAdapter
    private lateinit var schedeEntity: SchedeEntity
    private lateinit var viewModel: EsserciziViewModel

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        binding = FragmentFragEssercissiBinding.inflate(inflater, container, false)
        adapterx = EserciziAdapter()

        // Crea il Repository e il UseCase
        val dao = DbFit.getDatabase(requireContext()).essercissiDao()
        val esserciziRepository = EsserciziRepository(dao)
        val getEserciziByIdUseCase = UsesCasesEssercissi(esserciziRepository)

        // Inizializza il ViewModel tramite la Factory
        val viewModelFactory = GenericViewModelFactory {
            EsserciziViewModel(getEserciziByIdUseCase, activity?.application!!)
        }

        viewModel = ViewModelProvider(this, viewModelFactory!!).get(EsserciziViewModel::class.java)

        binding.apply {
            recylcreview.apply {
                val decorationSpan = DividerItemDecoration(requireContext(), LinearLayout.VERTICAL)
                addItemDecoration(decorationSpan)
                this.adapter = adapterx

                layoutManager = LinearLayoutManager(requireContext())
            }
        }

        binding.floatingActionButton.setOnClickListener {
            val bottomSheetFragment = MyBottomSheetFragment(schedeEntity.id!!)
            bottomSheetFragment.show(
                requireActivity().supportFragmentManager,
                "MyBottomSheetFragment"
            )
        }

        binding.button.setOnClickListener {
            val bundle = Bundle()
            bundle.putSerializable("schedeEntity", schedeEntity)
            findNavController().navigate(R.id.fragmentRepielogo, bundle)
        }

        return binding.root
    }

    override fun onResume() {
        super.onResume()
        arguments?.let {
            schedeEntity = it.getSerializable("f") as SchedeEntity

            lifecycleScope.launch(Dispatchers.Main) {
                // Observe changes to the list of exercises
                viewModel.getAllById(schedeEntity.id!!).observe(viewLifecycleOwner) { list ->
                    adapterx.submitList(list)
                }
            }
        }

        (activity as? BaseAcitivity)?.selectTab(1)
    }
}

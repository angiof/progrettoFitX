package com.app.progrettofitx.ui.shedeForms

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.LinearLayout
import androidx.fragment.app.Fragment
import androidx.fragment.app.viewModels
import androidx.lifecycle.lifecycleScope
import androidx.lifecycle.viewModelScope
import androidx.recyclerview.widget.DividerItemDecoration
import androidx.recyclerview.widget.LinearLayoutManager
import com.app.progrettofitx.databinding.FragmentFragEssercissiBinding
import com.app.progrettofitx.db.EsserciziEntity
import com.app.progrettofitx.db.SchedeEntity
import com.app.progrettofitx.ui.forms.BaseAcitivity
import com.app.progrettofitx.ui.shedeForms.recyclreview.EserciziAdapter
import com.app.progrettofitx.ui.sheet.MyBottomSheetFragment
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext
import java.lang.ArithmeticException
class FragEssercissi : Fragment() {
    private lateinit var binding: FragmentFragEssercissiBinding
    private lateinit var adapterx: EserciziAdapter
    private lateinit var schedeEntity: SchedeEntity
    private val viewModel: EsserciziViewModel by viewModels()

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        binding = FragmentFragEssercissiBinding.inflate(inflater, container, false)

        // Initialize the adapter
        adapterx = EserciziAdapter()



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
            bottomSheetFragment.show(requireActivity().supportFragmentManager, "MyBottomSheetFragment")
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
                }            }
        }

        (activity as? BaseAcitivity)?.selectTab(1)
    }
}

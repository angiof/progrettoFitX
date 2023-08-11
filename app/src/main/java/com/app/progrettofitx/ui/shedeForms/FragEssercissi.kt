package com.app.progrettofitx.ui.shedeForms

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.Fragment
import com.app.progrettofitx.databinding.FragmentFragEssercissiBinding
import com.app.progrettofitx.ui.forms.BaseAcitivity
import com.app.progrettofitx.ui.shedeForms.recyclreview.EserciziAdapter
import com.app.progrettofitx.ui.sheet.MyBottomSheetFragment

class FragEssercissi : Fragment() {
    private lateinit var binding: FragmentFragEssercissiBinding
    private lateinit var adaper: EserciziAdapter
    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        binding = FragmentFragEssercissiBinding.inflate(inflater, container, false)


        adaper = EserciziAdapter()
        binding.apply {
            recylcreview.apply {
                adapter = adapter
            }
        }
        binding.floatingActionButton.setOnClickListener {
            val bottomSheetFragment = MyBottomSheetFragment()
            bottomSheetFragment.show(
                requireActivity().supportFragmentManager,
                "MyBottomSheetFragment"
            )
        }
        return binding.root
    }



    override fun onResume() {
        super.onResume()
        (activity as? BaseAcitivity)?.selectTab(1)
    }
}

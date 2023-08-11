package com.app.progrettofitx.ui.home

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.Fragment
import androidx.recyclerview.widget.LinearLayoutManager
import com.app.progrettofitx.ModelHomemenu
import com.app.progrettofitx.R
import com.app.progrettofitx.databinding.FragmentHomeBinding

class HomeFragment : Fragment() {

    private var _binding: FragmentHomeBinding? = null
    private val binding get() = _binding!!

    private lateinit var adapters: AdapterHome

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {

        _binding = FragmentHomeBinding.inflate(inflater, container, false)
        adapters = AdapterHome()

        binding.listaMenuHome.apply {
            // Prepara i dati per la RecyclerView
            val data = listOf(
                ModelHomemenu(2, "Crea Scheda")

            )
            binding.listaMenuHome.layoutManager = LinearLayoutManager(context)

            adapters.submitList(data)
            this.adapter = adapters
        }

        return binding.root
    }

    override fun onDestroy() {
        super.onDestroy()
        _binding = null
    }


}


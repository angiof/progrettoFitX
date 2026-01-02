package com.app.fityo.ui.home

import android.content.Intent
import android.os.Bundle
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.Fragment
import androidx.navigation.fragment.findNavController
import androidx.recyclerview.widget.LinearLayoutManager
import com.app.fityo.R
import com.app.fityo.databinding.FragmentHomeBinding
import com.app.fityo.dominio.HomeMenuAction
import com.app.fityo.dominio.ModelHomemenu
import com.app.fityo.ui.compose.SchedeListActivity
import com.app.fityo.ui.forms.AcitivySheda
import com.app.fityo.ui.musclecompare.MuscleCompareActivity
import com.app.fityo.ui.tutor.TutorActivity

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
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        adapters = AdapterHome(::handleMenuClick)

        binding.listaMenuHome.apply {
            layoutManager = LinearLayoutManager(context)
            adapter = adapters
            setHasFixedSize(true)
        }
        adapters.submitList(createHomeMenu())
    }

    private fun createHomeMenu(): List<ModelHomemenu> = listOf(
        ModelHomemenu(
            copertina = R.drawable.powerlifting,
            titolo = getString(R.string.crea_scheda),
            action = HomeMenuAction.CREATE_SCHEDE
        ),
        ModelHomemenu(
            copertina = R.drawable.scheda_ia_fitnes,
            titolo = getString(R.string.apri_schede),
            action = HomeMenuAction.OPEN_SCHEDE
        ),
        ModelHomemenu(
            copertina = R.drawable.ic_calendario2,
            titolo = getString(R.string.title_dashboard),
            action = HomeMenuAction.VIEW_STATS
        ),
        ModelHomemenu(
            copertina = R.drawable.donna_spalle,
            titolo = getString(R.string.muscle_compare),
            action = HomeMenuAction.MUSCLE_COMPARE
        ),
        ModelHomemenu(
            copertina = R.drawable.powerlifting,
            titolo = getString(R.string.tutor_mode),
            action = HomeMenuAction.TUTOR_MODE
        )
    )

    private fun handleMenuClick(item: ModelHomemenu) {
        when (item.action) {
            HomeMenuAction.CREATE_SCHEDE -> {
                startActivity(Intent(requireContext(), AcitivySheda::class.java))
            }

            HomeMenuAction.OPEN_SCHEDE -> {
                startActivity(Intent(requireContext(), SchedeListActivity::class.java))
            }

            HomeMenuAction.VIEW_STATS -> {
                findNavController().navigate(R.id.navigation_dashboard)
            }

            HomeMenuAction.MUSCLE_COMPARE -> {
                startActivity(Intent(requireContext(), MuscleCompareActivity::class.java))
            }

            HomeMenuAction.TUTOR_MODE -> {
                startActivity(Intent(requireContext(), TutorActivity::class.java))
            }
        }
    }

    override fun onResume() {
        super.onResume()
        Log.d("MyFragmentTag", "${this::class.java.simpleName} is resumed")
    }

    override fun onDestroyView() {
        super.onDestroyView()
        _binding = null
    }
}


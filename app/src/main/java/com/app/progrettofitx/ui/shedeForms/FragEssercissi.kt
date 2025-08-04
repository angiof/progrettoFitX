package com.app.progrettofitx.ui.shedeForms

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.LinearLayout
import androidx.fragment.app.Fragment
import androidx.fragment.app.setFragmentResultListener
import androidx.lifecycle.ViewModelProvider
import androidx.lifecycle.lifecycleScope
import androidx.lifecycle.viewModelScope
import androidx.navigation.fragment.findNavController
import androidx.recyclerview.widget.DividerItemDecoration
import androidx.recyclerview.widget.ItemTouchHelper
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.app.progrettofitx.R
import com.app.progrettofitx.data_layer.db.DB.DbFit
import com.app.progrettofitx.data_layer.db.EsserciziEntity
import com.app.progrettofitx.data_layer.db.SchedeEntity
import com.app.progrettofitx.data_layer.db.repos.EsserciziRepository
import com.app.progrettofitx.databinding.FragmentFragEssercissiBinding
import com.app.progrettofitx.dominio.UsesCasesEssercissi
import com.app.progrettofitx.ui.factory.GenericViewModelFactory
import com.app.progrettofitx.ui.forms.BaseAcitivity
import com.app.progrettofitx.ui.shedeForms.recyclreview.EserciziAdapter
import com.app.progrettofitx.ui.sheet.RipetizioniSheetFragment
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext

class FragEssercissi : Fragment() {
    private lateinit var binding: FragmentFragEssercissiBinding
    private lateinit var adapterx: EserciziAdapter
    private lateinit var schedeEntity: SchedeEntity
    private lateinit var viewModel: EsserciziViewModel
    private var positionRecy: Int? = null
    private var mEssercizi: EsserciziEntity? = null
    val isNew = arguments?.getBoolean("isNew") ?: true


    private var hasUnsavedChanges = false


    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?, savedInstanceState: Bundle?
    ): View {
        binding = FragmentFragEssercissiBinding.inflate(inflater, container, false)
        adapterx = EserciziAdapter(object : EserciziAdapter.ItemClick {

            override fun onEdit(item: EsserciziEntity) {
                RipetizioniSheetFragment
                    .newInstance(schedeEntity.id!!, item)
                    .show(parentFragmentManager, "EditSheet")
            }

            override suspend fun onDelete(item: EsserciziEntity) {
                viewModel.delateEsser(item)

            }
        })


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

        setRecyListainer()

        return binding.root
    }


    private fun setRecyListainer() {


        binding.apply {
            recylcreview.apply {
                val decorationSpan = DividerItemDecoration(requireContext(), LinearLayout.VERTICAL)
                addItemDecoration(decorationSpan)
                this.adapter = adapterx
                layoutManager = LinearLayoutManager(requireContext())


                val onItemMove: (fromPosition: Int, toPosition: Int) -> Boolean =
                    { fromPosition, toPosition ->
                        // Implementa la logica per spostare l'elemento da fromPosition a toPosition
                        // Ritorna true se l'elemento è stato spostato
                        true
                    }

                val onItemSwiped: (position: Int, direction: Int) -> Unit = { position, direction ->
                    // Implementa la logica per gestire lo swipe dell'elemento
                    viewModel.viewModelScope.launch {
                        withContext(Dispatchers.IO) {
                            viewModel.delateEsser(position)
                        }
                    }

                }
                val itemTouchHelper =
                    ItemTouchHelper(SimpleItemTouchHelperCallback(onItemMove, onItemSwiped))
                itemTouchHelper.attachToRecyclerView(binding.recylcreview)

            }
        }
        RipetizioniSheetFragment



        binding.btnAdd.setOnClickListener {
            RipetizioniSheetFragment
                .newInstance(schedeEntity.id!!)
                .show(parentFragmentManager, "AddSheet")
        }

        binding.btnSave.setOnClickListener {
            val bundle = Bundle()
            bundle.putSerializable("schedeEntity", schedeEntity)
            findNavController().navigate(R.id.fragmentRepielogo, bundle)
        }
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


    inner class SimpleItemTouchHelperCallback(
        private val onMove: (fromPosition: Int, toPosition: Int) -> Boolean,
        private val onSwiped: (position: Int, direction: Int) -> Unit
    ) : ItemTouchHelper.Callback() {

        override fun isLongPressDragEnabled(): Boolean {
            // Abilita il drag & drop lungo premendo, imposta a false se non necessario
            return true
        }

        override fun isItemViewSwipeEnabled(): Boolean {
            // Abilita lo swipe, imposta a false se non necessario
            return false
        }

        override fun getMovementFlags(
            recyclerView: RecyclerView, viewHolder: RecyclerView.ViewHolder
        ): Int {
            val dragFlags =
                ItemTouchHelper.UP or ItemTouchHelper.DOWN // o altre direzioni se necessario
            val swipeFlags = ItemTouchHelper.START or ItemTouchHelper.END
            return makeMovementFlags(dragFlags, swipeFlags)
        }

        override fun onMove(
            recyclerView: RecyclerView,
            viewHolder: RecyclerView.ViewHolder,
            target: RecyclerView.ViewHolder
        ): Boolean {
            return onMove(viewHolder.adapterPosition, target.adapterPosition)
        }

        override fun onSwiped(viewHolder: RecyclerView.ViewHolder, direction: Int) {
            onSwiped(viewHolder.adapterPosition, direction)
        }
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        // se manca l’argomento, consideralo true
        val isNew = arguments?.getBoolean("isNew", true) ?: true

        if (isNew) {
            // scheda nuova → salva sempre abilitato
            binding.btnSave.isEnabled = true
        } else {
            // scheda esistente → parte spento e si abilita al primo cambiamento
            binding.btnSave.isEnabled = false
            setFragmentResultListener("exercise_changed") { _, _ ->
                binding.btnSave.isEnabled = true
            }
        }
    }

}

package com.app.progrettofitx.ui.sheet

import android.app.Dialog
import android.graphics.Rect
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.view.ViewTreeObserver
import android.view.WindowManager
import android.widget.ArrayAdapter
import android.widget.AutoCompleteTextView
import androidx.appcompat.R
import androidx.lifecycle.ViewModelProvider
import androidx.lifecycle.lifecycleScope
import androidx.lifecycle.viewModelScope
import com.app.progrettofitx.data_layer.db.DB.DbFit
import com.app.progrettofitx.data_layer.db.EsserciziEntity
import com.app.progrettofitx.data_layer.db.repos.EsserciziRepository
import com.app.progrettofitx.databinding.BottomSheetLayoutBinding
import com.app.progrettofitx.dominio.UsesCasesEssercissi
import com.app.progrettofitx.ui.factory.GenericViewModelFactory
import com.app.progrettofitx.ui.shedeForms.EsserciziViewModel
import com.google.android.material.bottomsheet.BottomSheetBehavior
import com.google.android.material.bottomsheet.BottomSheetDialog
import com.google.android.material.bottomsheet.BottomSheetDialogFragment
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch

class MyBottomSheetFragment(private val idScheda: Int) :
    BottomSheetDialogFragment() {

    private var nome: String? = null
    private var nSerie: Int? = null
    private var nIntervallo: Int? = null
    private var nRipetizioni: Int? = null

    private var isometria: Int? = null
    private var attrezzo: String? = null
    private lateinit var viewModel: EsserciziViewModel


    private val binding: BottomSheetLayoutBinding by lazy {
        BottomSheetLayoutBinding.inflate(
            layoutInflater
        )
    }

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        listaAttrezzi()
        setInfoAndSave()


        val dao = DbFit.getDatabase(requireContext()).essercissiDao()
        val esserciziRepository = EsserciziRepository(dao)
        val getEserciziByIdUseCase = UsesCasesEssercissi(esserciziRepository)

        // Inizializza il ViewModel tramite la Factory
        val viewModelFactory = GenericViewModelFactory {
            EsserciziViewModel(getEserciziByIdUseCase)
        }

        viewModel = ViewModelProvider(this, viewModelFactory)[EsserciziViewModel::class.java]
        //modificaTastiera()
       // adjustForKeyboard()
        adjustForFocusedView()

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
                        nRipetizione = nRipetizioni ?: 0,
                        intervallo = nIntervallo,
                        insometria = isometria,
                        schedaId = idScheda,
                        attrezzo = attrezzo ?: "nessuno",
                        nSerie = nSerie ?: 0
                    )
                )
            }

            lifecycleScope.launch(Dispatchers.Main) {
                dismiss()
            }
        }
    }

    private fun listaAttrezzi() {
        val items = listOf("Manubrio", "Bilanciere", "Elastico", "fatGrip", "Nessuno")

        val adapter = ArrayAdapter(
            requireContext(),
            R.layout.support_simple_spinner_dropdown_item,
            items
        )
        (binding.layoutEssercissiSheet.listaAttrezziTxtx as? AutoCompleteTextView)?.setAdapter(
            adapter
        )
        (binding.layoutEssercissiSheet.listaAttrezziTxtx as? AutoCompleteTextView)?.setOnItemClickListener { parent, _, position, _ ->

            (binding.layoutEssercissiSheet.listaAttrezziTxtx as? AutoCompleteTextView)?.showDropDown()
            attrezzo = parent.getItemAtPosition(position) as String
        }

    }


    fun adjustForFocusedView() {
        val rootView = binding.root
        val listener = ViewTreeObserver.OnGlobalLayoutListener {
            val rect = Rect()
            rootView.getWindowVisibleDisplayFrame(rect)
            val screenHeight = rootView.height
            val keypadHeight = screenHeight - rect.bottom

            val focusedView = dialog?.currentFocus
            if (focusedView != null && keypadHeight > screenHeight * 0.15) {
                val scrollAmount = (focusedView.bottom + keypadHeight) - screenHeight
                if (scrollAmount > 0) {
                    rootView.scrollTo(0, scrollAmount)
                }
            } else {
                rootView.scrollTo(0, 0)
            }
        }
        rootView.viewTreeObserver.addOnGlobalLayoutListener(listener)
    }

    override fun onActivityCreated(savedInstanceState: Bundle?) {
        super.onActivityCreated(savedInstanceState)
        dialog?.window?.setSoftInputMode(WindowManager.LayoutParams.SOFT_INPUT_ADJUST_RESIZE)
    }



}




package com.app.fityo.ui.sheet

import android.graphics.Rect
import android.os.Bundle
import android.text.InputType
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.view.ViewTreeObserver
import android.view.WindowManager
import android.widget.ArrayAdapter
import android.widget.NumberPicker
import androidx.appcompat.R
import androidx.core.os.bundleOf
import androidx.lifecycle.ViewModelProvider
import androidx.lifecycle.viewModelScope
import com.app.fityo.data_layer.db.DB.DbFit
import com.app.fityo.data_layer.db.EsserciziEntity
import com.app.fityo.data_layer.db.repos.EsserciziRepository
import com.app.fityo.databinding.BottomSheetLayoutBinding
import com.app.fityo.dominio.UsesCasesEssercissi
import com.app.fityo.ui.factory.GenericViewModelFactory
import com.app.fityo.ui.shedeForms.EsserciziViewModel
import com.google.android.material.bottomsheet.BottomSheetDialogFragment
import com.google.android.material.dialog.MaterialAlertDialogBuilder
import com.kizitonwose.calendar.view.CalendarView
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import java.util.Locale

class RipetizioniSheetFragment : BottomSheetDialogFragment() {

    private lateinit var calendarView: CalendarView


    companion object {
        private const val ARG_SCHEDA = "schedaId"
        private const val ARG_ITEM = "item"
        fun newInstance(schedaId: Int, item: EsserciziEntity? = null) =
            RipetizioniSheetFragment().apply {
                arguments = bundleOf(
                    ARG_SCHEDA to schedaId,
                    ARG_ITEM to item
                )
            }
    }

    private val idScheda by lazy { requireArguments().getInt(ARG_SCHEDA) }
    private val editing by lazy { arguments?.getSerializable(ARG_ITEM) as? EsserciziEntity }
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
        editing?.let { e ->
            binding.layoutEssercissiSheet.apply {
                edNome.setText(e.nome)
                edSerie.setText(e.nSerie.toString())
                edReps.setText(e.nRipetizione.toString())
                edRiposo.setText(formatDuration(e.intervallo))
                edIsometria.setText(formatDuration(e.insometria))
                edPeso.setText(e.peso?.toString() ?: "")
                listaAttrezziTxtx.setText(e.attrezzo, false)
            }
        }
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
        setupDurationPickers()

        return binding.root





    }

    override fun onResume() {
        super.onResume()
        Log.d("MyFragmentTag", "${this::class.java.simpleName} is resumed")
    }


    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)



    }



    private fun setInfoAndSave() = with(binding.layoutEssercissiSheet) {
        bntSave.setOnClickListener {
            // Leggo i testi direttamente dagli EditText
            val serieText = edSerie.text.toString()
            val repsText  = edReps.text.toString()
            val riposoText = edRiposo.text.toString()
            val isoText    = edIsometria.text.toString()
            val pesoText = edPeso.text.toString()

            val serie = serieText.toIntOrNull() ?: 0
            val reps  = repsText.toIntOrNull() ?: 0
            val riposo= parseDurationInput(riposoText)
            val iso   = parseDurationInput(isoText)
            val peso = pesoText.toFloatOrNull()

            val nuovo = (editing ?: EsserciziEntity(
                nome         = "",
                attrezzo     = "",
                nSerie       = 0,
                nRipetizione = 0,
                insometria   = null,
                intervallo   = null,
                peso         = null,
                schedaId     = idScheda
            )).copy(
                nome         = edNome.text.toString(),
                nSerie       = serie,
                nRipetizione = reps,
                intervallo   = riposo,
                insometria   = iso,
                peso         = peso,
                attrezzo     = attrezzo ?: editing?.attrezzo.orEmpty(),
                schedaId     = idScheda
            )

            viewModel.viewModelScope.launch(Dispatchers.IO) {
                if (editing == null) viewModel.insert(nuovo) else viewModel.update(nuovo)
            }
            dismiss()
        }
    }

    private fun listaAttrezzi() {
        val items = listOf(
            "Manubrio",
            "Bilanciere",
            "Bilanciere EZ",
            "Kettlebell",
            "Palla Medica",
            "Elastico",
            "Corda per saltare",
            "FatGrip",
            "Catene",
            "Sbarra per trazioni",
            "Anelli da ginnastica",
            "Parallele",
            "Box pliometrico",
            "Power Rack",
            "Panca piana",
            "Panca inclinata",
            "Leg Press",
            "Lat Machine",
            "Pectoral Machine",
            "Shoulder Press",
            "Chest Press",
            "Leg Extension",
            "Leg Curl",
            "Calf Machine",
            "Macchina cavi",
            "Vogatore",
            "Air Bike",
            "GHD",
            "Tapis Roulant",
            "Cyclette",
            "Ellittica",
            "Stepper",
            "Sacco da boxe",
            "Punching ball",
            "Corda per saltare",
            "Corpo libero",
            "Macchinario Bicipiti",
            "Macchinario Tricipiti",
            "Macchinario Gambe",
            "Macchinario Glutei",
            "Altro",
            "Nessuno"
        )

        val adapter = ArrayAdapter(
            requireContext(),
            R.layout.support_simple_spinner_dropdown_item,
            items
        )
        binding.layoutEssercissiSheet.listaAttrezziTxtx.setAdapter(
            adapter
        )
        binding.layoutEssercissiSheet.listaAttrezziTxtx.setOnItemClickListener { parent, _, position, _ ->

            binding.layoutEssercissiSheet.listaAttrezziTxtx.showDropDown()
            attrezzo = parent.getItemAtPosition(position) as String
        }

    }

    private fun setupDurationPickers() = with(binding.layoutEssercissiSheet) {
        fun attachDurationPicker(
            field: com.google.android.material.textfield.TextInputEditText,
            title: Int
        ) {
            field.inputType = InputType.TYPE_NULL
            field.isFocusable = false
            field.isClickable = true
            field.isCursorVisible = false
            field.setOnClickListener {
                val currentSeconds = parseDurationInput(field.text.toString()) ?: 0
                showDurationPicker(getString(title), currentSeconds) { seconds ->
                    field.setText(formatDuration(seconds))
                }
            }
        }

        attachDurationPicker(edIsometria, com.app.fityo.R.string.exercise_isometria_label)
        attachDurationPicker(edRiposo, com.app.fityo.R.string.exercise_recupero_label)
    }

    private fun showDurationPicker(
        title: String,
        initialSeconds: Int,
        onSelected: (Int) -> Unit
    ) {
        val context = requireContext()
        val dialogView = LayoutInflater.from(context).inflate(
            com.app.fityo.R.layout.dialog_duration_picker,
            null,
            false
        )
        val minutesPicker = dialogView.findViewById<NumberPicker>(com.app.fityo.R.id.minutesPicker)
        val secondsPicker = dialogView.findViewById<NumberPicker>(com.app.fityo.R.id.secondsPicker)

        val initialMinutes = (initialSeconds / 60).coerceIn(0, 59)
        val initialSecs = (initialSeconds % 60).coerceIn(0, 59)

        listOf(minutesPicker, secondsPicker).forEach { picker ->
            picker.minValue = 0
            picker.maxValue = 59
            picker.setFormatter { value -> String.format(Locale.getDefault(), "%02d", value) }
        }
        minutesPicker.value = initialMinutes
        secondsPicker.value = initialSecs

        MaterialAlertDialogBuilder(context)
            .setTitle(title)
            .setView(dialogView)
            .setNegativeButton(android.R.string.cancel, null)
            .setPositiveButton(android.R.string.ok) { _, _ ->
                val totalSeconds = minutesPicker.value * 60 + secondsPicker.value
                onSelected(totalSeconds)
            }
            .show()
    }

    private fun formatDuration(value: Int?): String {
        if (value == null || value <= 0) return ""
        val minutes = value / 60
        val seconds = value % 60
        return String.format(Locale.getDefault(), "%02d:%02d", minutes, seconds)
    }

    private fun parseDurationInput(value: String): Int? {
        if (value.isBlank()) return null
        val trimmed = value.trim()
        return if (trimmed.contains(':')) {
            val parts = trimmed.split(":")
            if (parts.size == 2) {
                val minutes = parts[0].toIntOrNull() ?: return null
                val seconds = parts[1].toIntOrNull() ?: return null
                minutes.coerceAtLeast(0) * 60 + seconds.coerceIn(0, 59)
            } else {
                null
            }
        } else {
            trimmed.toIntOrNull()
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


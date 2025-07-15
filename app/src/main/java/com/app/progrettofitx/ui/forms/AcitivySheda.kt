package com.app.progrettofitx.ui.forms

import android.os.Bundle
import android.text.TextUtils
import android.view.Gravity
import android.view.View
import androidx.appcompat.widget.AppCompatTextView
import androidx.core.content.ContextCompat
import androidx.core.os.bundleOf
import androidx.lifecycle.ViewModelProvider
import androidx.navigation.NavGraph
import androidx.navigation.fragment.NavHostFragment
import com.app.progrettofitx.R
import com.app.progrettofitx.data_layer.db.DB.DbFit
import com.app.progrettofitx.data_layer.db.SchedeEntity
import com.app.progrettofitx.databinding.ActivityBaseAcitivityBinding
import com.app.progrettofitx.dominio.UsesCasesSheda
import com.app.progrettofitx.ui.factory.GenericViewModelFactory
import com.app.progrettofitx.ui.shedeForms.SchedeRepository
import com.app.progrettofitx.ui.shedeForms.SchedeViewModel

class AcitivySheda : BaseAcitivity() {

    lateinit var binding: ActivityBaseAcitivityBinding
    private lateinit var schedeViewModel: SchedeViewModel
    private var isNew: Boolean = true
    private lateinit var schedaIntent: SchedeEntity



    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityBaseAcitivityBinding.inflate(layoutInflater)

        // 1) Recupera extra in modo sicuro
        val schedaIntent = intent.getSerializableExtra("f") as? SchedeEntity
        val isNew = intent.getBooleanExtra("isNew", true)

        // 2) Se non ho ricevuto alcuna scheda, proseguo senza logica "esistente"
        if (schedaIntent != null) {
            // imposta il nav graph con startDestination basato su isNew
            val navGraph = navInflater.inflate(R.navigation.create_schedes_navigations)
            if (!isNew) navGraph.setStartDestination(R.id.fragEssercissi)
            navHostFragment.navController.setGraph(
                navGraph,
                bundleOf("f" to schedaIntent, "isNew" to isNew)
            )

            // nascondi la tab di creazione se non è nuova
            if (!isNew) {
                tabLayout.getTabAt(0)?.view?.visibility = View.GONE
                selectTab(1)
            }

            // override pulsante close: solo se è scheda nuova presente nel DB la cancelli
            binding.btnClose.setOnClickListener {
                if (isNew) {
                    schedeViewModel.delete(schedaIntent)
                }
                finish()
            }
        } else {
            // non ho scheda: chiamo il navGraph normale senza extras
            val navGraph = navInflater.inflate(R.navigation.create_schedes_navigations)
            navHostFragment.navController.graph = navGraph
            binding.btnClose.setOnClickListener { finish() }
        }

        // aggiungo comunque le tab
        val tabs = mutableListOf(
            tabLayout.newTab().setCustomView(createCustomTabView("Crea Scheda")),
            tabLayout.newTab().setCustomView(createCustomTabView("Esercizi")),
            tabLayout.newTab().setCustomView(createCustomTabView("Riepilogo"))
        )
        addTab(tabs)
    }

    private fun createCustomTabView(text: String): AppCompatTextView {
        return AppCompatTextView(this).apply {
            this.text = text
            maxLines = 1
            ellipsize = TextUtils.TruncateAt.END
            setTextColor(ContextCompat.getColorStateList(this.context, R.color.black))
            setTextAppearance(R.style.testoForm2)
            gravity = Gravity.CENTER
        }
    }
}



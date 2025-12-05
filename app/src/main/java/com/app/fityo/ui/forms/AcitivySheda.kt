package com.app.fityo.ui.forms

import android.os.Bundle
import android.text.TextUtils
import android.view.Gravity
import android.view.View
import androidx.appcompat.widget.AppCompatTextView
import androidx.core.content.ContextCompat
import androidx.core.os.bundleOf
import androidx.lifecycle.ViewModelProvider
import androidx.navigation.NavGraph
import com.app.fityo.R
import com.app.fityo.data_layer.db.DB.DbFit
import com.app.fityo.data_layer.db.SchedeEntity
import com.app.fityo.dominio.UsesCasesSheda
import com.app.fityo.ui.factory.GenericViewModelFactory
import com.app.fityo.data_layer.repository.SchedeRepository
import com.app.fityo.ui.shedeForms.SchedeViewModel

class AcitivySheda : BaseAcitivity() {

    private lateinit var schedeViewModel: SchedeViewModel

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        schedeViewModel = ViewModelProvider(
            this,
            GenericViewModelFactory {
                SchedeViewModel(
                    application,
                    UsesCasesSheda(
                        SchedeRepository(
                            DbFit.getDatabase(application).schedeDao()
                        )
                    )
                )
            }
        )[SchedeViewModel::class.java]

        // 1) Recupera extra in modo sicuro
        val schedaIntent = intent.getSerializableExtra("f") as? SchedeEntity
        val isNew = intent.getBooleanExtra("isNew", true)

        // 2) Se non ho ricevuto alcuna scheda, proseguo senza logica "esistente"
        val navGraph: NavGraph = navInflater.inflate(R.navigation.create_schedes_navigations)
        if (schedaIntent != null) {
            if (!isNew) navGraph.setStartDestination(R.id.fragEssercissi)
            navHostFragment.navController.setGraph(
                navGraph,
                bundleOf("f" to schedaIntent, "isNew" to isNew)
            )

            if (!isNew) {
                tabLayout.getTabAt(0)?.view?.visibility = View.GONE
                selectTab(1)
            }

            binding.btnClose.setOnClickListener {
                if (isNew) {
                    schedeViewModel.delete(schedaIntent)
                }
                finish()
            }
        } else {
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
            setTextColor(ContextCompat.getColor(this.context, R.color.text_primary))
            setTextAppearance(R.style.testoForm2)
            gravity = Gravity.CENTER
        }
    }
}


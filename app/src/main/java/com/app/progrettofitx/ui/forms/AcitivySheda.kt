package com.app.progrettofitx.ui.forms

import android.os.Bundle
import android.text.TextUtils
import android.view.Gravity
import android.view.View
import androidx.appcompat.widget.AppCompatTextView
import androidx.core.content.ContextCompat
import androidx.core.os.bundleOf
import androidx.navigation.NavGraph
import com.app.progrettofitx.R
import com.app.progrettofitx.data_layer.db.SchedeEntity
import com.app.progrettofitx.databinding.ActivityBaseAcitivityBinding

class AcitivySheda : BaseAcitivity() {

    lateinit var binding: ActivityBaseAcitivityBinding

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        binding = ActivityBaseAcitivityBinding.inflate(layoutInflater)
        // 1) Recupera extra
        val isNew   = intent.getBooleanExtra("isNew", true)
        val scheda  = intent.getSerializableExtra("f") as? SchedeEntity

        // 2) Inflating nav graph e forzare startDestination
        val navGraph = navInflater.inflate(R.navigation.create_schedes_navigations)
        if (!isNew) {
            // salto al tab #2 (FragEssercissi)
            navGraph.setStartDestination(R.id.fragEssercissi)
        }
        // 3) Passo la scheda al primo fragment visibile
        navHostFragment.navController.setGraph(navGraph, bundleOf("f" to scheda))

        // 4) Aggiungo le tab
        val tabs = mutableListOf(
            tabLayout.newTab().setCustomView(createCustomTabView("Crea Scheda")),
            tabLayout.newTab().setCustomView(createCustomTabView("Esercizi")),
            tabLayout.newTab().setCustomView(createCustomTabView("Riepilogo"))
        )
        addTab(tabs)

        // 5) Nascondi prima tab se è scheda esistente
        if (!isNew) {
            tabLayout.getTabAt(0)?.view?.visibility = View.GONE
            selectTab(1)
        }
    }

    private fun createCustomTabView(text: String): AppCompatTextView {
        return AppCompatTextView(this).apply {
            this.text = text
            maxLines = 1
            ellipsize = TextUtils.TruncateAt.END
            setTextColor(ContextCompat.getColorStateList(this@AcitivySheda, R.color.black))
            setTextAppearance(R.style.testoForm2)
            gravity = Gravity.CENTER
        }
    }
}
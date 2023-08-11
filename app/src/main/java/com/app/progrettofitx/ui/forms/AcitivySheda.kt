package com.app.progrettofitx.ui.forms

import android.os.Bundle
import android.text.TextUtils
import android.view.Gravity
import androidx.appcompat.widget.AppCompatTextView
import androidx.core.content.ContextCompat
import androidx.navigation.NavGraph
import com.app.progrettofitx.R
import com.app.progrettofitx.databinding.ActivityBaseAcitivityBinding

class AcitivySheda : BaseAcitivity() {

    lateinit var binding: ActivityBaseAcitivityBinding

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        binding = ActivityBaseAcitivityBinding.inflate(layoutInflater)


        val navGraph: NavGraph = navInflater.inflate(R.navigation.create_schedes_navigations)
        navHostFragment.navController.graph = navGraph

        val tabs = mutableListOf(
            tabLayout.newTab().setCustomView(createCustomTabView("Crea Scheda")),
            tabLayout.newTab().setCustomView(createCustomTabView("Essercissi")),
            tabLayout.newTab().setCustomView(createCustomTabView("repilogo"))
        )
        addTab(tabs = tabs)

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
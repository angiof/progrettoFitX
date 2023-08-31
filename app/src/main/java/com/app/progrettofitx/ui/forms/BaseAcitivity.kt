package com.app.progrettofitx.ui.forms

import android.os.Bundle
import android.widget.LinearLayout
import androidx.appcompat.app.AppCompatActivity
import androidx.appcompat.content.res.AppCompatResources
import androidx.navigation.NavInflater
import androidx.navigation.fragment.NavHostFragment
import com.app.progrettofitx.R
import com.app.progrettofitx.databinding.ActivityBaseAcitivityBinding
import com.google.android.material.tabs.TabLayout

abstract class BaseAcitivity : AppCompatActivity() {

    private lateinit var binding: ActivityBaseAcitivityBinding
    lateinit var tabLayout: TabLayout
    lateinit var navInflater: NavInflater
    lateinit var navHostFragment: NavHostFragment


    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        binding = ActivityBaseAcitivityBinding.inflate(layoutInflater)
        setContentView(binding.root)

        navHostFragment =
            supportFragmentManager.findFragmentById(R.id.nav_host_fragment_activity_base) as NavHostFragment
        navInflater = navHostFragment.navController.navInflater

        tabLayout = binding.baseTabLayout

        binding.btnClose.setOnClickListener {
            finish()
        }



        setWalper()
    }

    fun addTab(tabs: MutableList<TabLayout.Tab>) {
        tabs.forEach { tab ->
            binding.baseTabLayout.addTab(tab)
            val tabStrip = binding.baseTabLayout.getChildAt(0) as LinearLayout
            for (i in 0 until tabStrip.childCount) {
                tabStrip.getChildAt(i).setOnTouchListener { v, event -> true }
            }

        }
    }


    fun selectTab(position: Int) {
        val tab = binding.baseTabLayout.getTabAt(position)
        binding.baseTabLayout.selectTab(tab)
    }

    fun setWalper() {
        tabLayout.addOnTabSelectedListener(object : TabLayout.OnTabSelectedListener {
            override fun onTabSelected(tab: TabLayout.Tab?) {
                when (tab?.position) {
                    0 -> {
                        binding.title.setImageDrawable(
                            AppCompatResources.getDrawable(this@BaseAcitivity, R.drawable.manubrio1)
                        )

                    }

                    1 -> {
                        binding.title.setImageDrawable(
                            AppCompatResources.getDrawable(this@BaseAcitivity, R.drawable.ragazza_scheda))
                    }
                    // Aggiungi altri casi qui
                }
            }

            override fun onTabUnselected(tab: TabLayout.Tab?) {
                // Codice opzionale per quando una scheda viene deselezionata
            }

            override fun onTabReselected(tab: TabLayout.Tab?) {
                // Codice opzionale per quando una scheda viene reselezionata
            }
        })
    }
}
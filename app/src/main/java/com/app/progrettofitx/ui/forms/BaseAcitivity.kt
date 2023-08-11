package com.app.progrettofitx.ui.forms

import android.os.Bundle
import android.widget.LinearLayout
import com.google.android.material.floatingactionbutton.FloatingActionButton
import com.google.android.material.snackbar.Snackbar
import com.google.android.material.tabs.TabLayout
import androidx.viewpager.widget.ViewPager
import androidx.appcompat.app.AppCompatActivity
import androidx.navigation.NavInflater
import androidx.navigation.fragment.NavHostFragment
import com.app.progrettofitx.R
import com.app.progrettofitx.databinding.ActivityBaseAcitivityBinding

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
}
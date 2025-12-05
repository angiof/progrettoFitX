package com.app.fityo.ui.filter.ui

import android.os.Bundle
import androidx.appcompat.app.AppCompatActivity
import com.app.fityo.databinding.ActivityFilterSchedeBinding

class ActivityFilterSchede : AppCompatActivity() {

    private lateinit var binding: ActivityFilterSchedeBinding

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        binding = ActivityFilterSchedeBinding.inflate(layoutInflater)
        setContentView(binding.root)
    }
}

package com.app.progrettofitx.ui.shedeForms.recyclreview

import android.view.View
import androidx.recyclerview.widget.RecyclerView
import com.app.progrettofitx.databinding.ListaEssercissiBinding
import com.app.progrettofitx.db.EsserciziEntity

class EserciziViewHolder(itemView: View, private val binding: ListaEssercissiBinding) : RecyclerView.ViewHolder(itemView) {


    fun bind(esercizi: EsserciziEntity) {
        binding.listaTitolo.text = esercizi.nome.toString()
        }
    }
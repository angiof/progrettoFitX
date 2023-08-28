package com.app.progrettofitx.ui.shedeForms.recyclreview

import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.recyclerview.widget.DiffUtil
import androidx.recyclerview.widget.ListAdapter
import com.app.progrettofitx.R
import com.app.progrettofitx.databinding.ListaEssercissiBinding
import com.app.progrettofitx.db.EsserciziEntity


class   EserciziAdapter : ListAdapter<EsserciziEntity, EserciziViewHolder>(EserciziDiffCallback()) {


    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): EserciziViewHolder {

        val binding = ListaEssercissiBinding.inflate(LayoutInflater.from(parent.context),parent,false)
        return EserciziViewHolder(LayoutInflater.from(parent.context).inflate(R.layout.lista_essercissi, parent, false),
            binding
        )
    }

    override fun onBindViewHolder(holder: EserciziViewHolder, position: Int) {
        val esercizi = getItem(position)
        holder.bind(esercizi)
    }

    class EserciziDiffCallback : DiffUtil.ItemCallback<EsserciziEntity>() {

        override fun areItemsTheSame(oldItem: EsserciziEntity, newItem: EsserciziEntity): Boolean {
            return oldItem.id == newItem.id
        }

        override fun areContentsTheSame(
            oldItem: EsserciziEntity, newItem: EsserciziEntity
        ): Boolean {
            return oldItem == newItem
        }
    }

}


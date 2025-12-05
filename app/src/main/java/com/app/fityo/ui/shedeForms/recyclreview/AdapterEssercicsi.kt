package com.app.fityo.ui.shedeForms.recyclreview

import android.annotation.SuppressLint
import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.recyclerview.widget.DiffUtil
import androidx.recyclerview.widget.ListAdapter
import androidx.recyclerview.widget.RecyclerView
import com.app.fityo.R
import com.app.fityo.data_layer.db.EsserciziEntity
import com.app.fityo.databinding.ListaEssercissiBinding
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import kotlinx.coroutines.runBlocking


class EserciziAdapter(val param: ItemClick) :
    ListAdapter<EsserciziEntity, EserciziAdapter.EserciziViewHolder>(EserciziDiffCallback()) {


    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): EserciziViewHolder {

        val binding =
            ListaEssercissiBinding.inflate(LayoutInflater.from(parent.context), parent, false)
        return EserciziViewHolder(
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

    interface ItemClick {
        fun onEdit(item: EsserciziEntity)
        suspend fun onDelete(item: EsserciziEntity)
    }

    inner class EserciziViewHolder(private val b: ListaEssercissiBinding) :
        RecyclerView.ViewHolder(b.root) {

        @SuppressLint("SetTextI18n")
        fun bind(e: EsserciziEntity) = with(b) {

            title.text = e.nome
            subtitle.text = root.context.getString(
                R.string.exercise_series_reps_format,
                e.nSerie,
                e.nRipetizione
            )
            val intervallo = e.intervallo ?: 0
            val intervalloTesto = if (intervallo > 60) {
                val minuti = intervallo / 60
                val secondi = intervallo % 60
                if (secondi == 0) "$minuti min" else "$minuti min $secondi s"
            } else {
                "$intervallo s"
            }
            details.text = root.context.getString(
                R.string.exercise_isometria_format,
                e.insometria ?: 0
            ) + " - " + root.context.getString(
                R.string.exercise_recupero_format,
                intervalloTesto
            ) + " - " + root.context.getString(
                R.string.exercise_attrezzo_format,
                e.attrezzo
            )
            imDelete.setOnClickListener {
                runBlocking { launch(Dispatchers.IO) { param.onDelete(e) } }
            }

            svgEdit.setOnClickListener {
                runBlocking { launch(Dispatchers.IO) { param.onEdit(e) } }
            }

            root.setOnClickListener { param.onEdit(e) }

        }
    }
}



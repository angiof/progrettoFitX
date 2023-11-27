package com.app.progrettofitx.ui.shedeForms.recyclreview

import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.recyclerview.widget.DiffUtil
import androidx.recyclerview.widget.ListAdapter
import androidx.recyclerview.widget.RecyclerView
import com.app.progrettofitx.R
import com.app.progrettofitx.data_layer.db.EsserciziEntity
import com.app.progrettofitx.databinding.ListaEssercissiBinding
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
        suspend fun getEssercissio(esserciziEntity: EsserciziEntity)
    }

    inner class EserciziViewHolder(private val binding: ListaEssercissiBinding) :
        RecyclerView.ViewHolder(binding.root) {


        fun bind(esercizi: EsserciziEntity) {
            //qui richiamo il

            binding.listaTitolo.text = esercizi.nome
            binding.tvInformazione.text =
                esercizi.nRipetizione.toString() + "X" + esercizi.nSerie.toString()

            binding.tvIsometria.text =
                " ${esercizi.insometria.toString()} " + "X" + " ${esercizi.intervallo.toString()}"

            binding.imageView.setOnClickListener {
                runBlocking {
                    launch(Dispatchers.IO) {
                        param.getEssercissio(esercizi)
                    }
                }
            }

            val imageRes = when {
                esercizi.attrezzo.equals("Manubrio", true) -> R.drawable.remo
                esercizi.attrezzo.equals("Bilanciere", true) -> R.drawable.bialnciere11
                esercizi.attrezzo.equals("Elastico", true) -> R.drawable.elastico11
                esercizi.attrezzo.equals("fatGrip", true) -> R.drawable.faa
                //  esercizi.attrezzo.equals("Nessuno", true) -> R.drawable.some_drawable_for_nessuno
                else -> null
            }
            binding.shapeableImageView.setImageResource(imageRes ?: 0)
        }
    }

}


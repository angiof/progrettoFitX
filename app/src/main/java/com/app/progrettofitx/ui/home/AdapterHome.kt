package com.app.progrettofitx.ui.home

import android.content.Intent
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ImageView
import android.widget.TextView
import androidx.recyclerview.widget.DiffUtil
import androidx.recyclerview.widget.ListAdapter
import androidx.recyclerview.widget.RecyclerView
import com.app.progrettofitx.ModelHomemenu
import com.app.progrettofitx.R
import com.app.progrettofitx.ui.forms.AcitivySheda
import com.google.android.material.card.MaterialCardView

class AdapterHome : ListAdapter<ModelHomemenu, AdapterHome.HomeViewHolder>(AdapterHomeCallback()) {


    inner class HomeViewHolder(itemView: View) : RecyclerView.ViewHolder(itemView) {
        private val titoloTextView: TextView = itemView.findViewById(R.id.tv_titolo)
        private val copertina: ImageView = itemView.findViewById(R.id.im_copertina)
        private val card: MaterialCardView = itemView.findViewById(R.id.card_menu)

        fun bin(voci: ModelHomemenu) {
            titoloTextView.text = voci.titolo

            if (voci.copertina==1){
                copertina.setImageResource(R.drawable.gym_tabs)
            }
            if (voci.copertina==2){
                copertina.setImageResource(R.drawable.powerlifting)
            }

            card.setOnClickListener {
                if (position==0){
                    it.context.startActivity(Intent(it.context, AcitivySheda::class.java))
                }
            }
        }

    }


    override fun onBindViewHolder(holder: HomeViewHolder, position: Int) {
        val voci = getItem(position)
        holder.bin(voci)
    }

    class AdapterHomeCallback : DiffUtil.ItemCallback<ModelHomemenu>() {

        override fun areItemsTheSame(oldItem: ModelHomemenu, newItem: ModelHomemenu): Boolean {
            return oldItem.copertina == newItem.copertina
        }

        override fun areContentsTheSame(
            oldItem: ModelHomemenu, newItem: ModelHomemenu
        ): Boolean {
            return oldItem.copertina == newItem.copertina
        }
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): HomeViewHolder {
        return HomeViewHolder(
            LayoutInflater.from(parent.context).inflate(R.layout.lista_voci_menu, parent, false)
        )
    }
}



package com.app.progrettofitx.ui.home

import android.animation.ValueAnimator
import android.content.Context
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.TextView
import androidx.recyclerview.widget.DiffUtil
import androidx.recyclerview.widget.ListAdapter
import androidx.recyclerview.widget.RecyclerView
import com.app.progrettofitx.R
import com.app.progrettofitx.dominio.HomeMenuAction
import com.app.progrettofitx.dominio.ModelHomemenu
import com.flaviofaria.kenburnsview.KenBurnsView
import com.google.android.material.card.MaterialCardView

class AdapterHome(
    private val onItemClick: (ModelHomemenu) -> Unit
) : ListAdapter<ModelHomemenu, AdapterHome.HomeViewHolder>(AdapterHomeCallback()) {

    inner class HomeViewHolder(itemView: View) : RecyclerView.ViewHolder(itemView) {
        private val titoloTextView: TextView = itemView.findViewById(R.id.tv_titolo)
        private val subtitleTextView: TextView = itemView.findViewById(R.id.tv_subtitle)
        private val cover: KenBurnsView = itemView.findViewById(R.id.im_copertina)
        private val card: MaterialCardView = itemView.findViewById(R.id.card_menu)

        fun bind(item: ModelHomemenu) {
            titoloTextView.text = item.titolo
            subtitleTextView.text = subtitleFor(itemView.context, item.action)
            cover.setImageResource(item.copertina)
            card.setOnClickListener {
                val position = bindingAdapterPosition
                if (position != RecyclerView.NO_POSITION) {
                    flashStroke()
                    onItemClick(item)
                }
            }
        }

        fun resume() {
            cover.resume()
        }

        fun pause() {
            cover.pause()
        }

        private fun flashStroke() {
            val startColor = card.strokeColor
            val accent = itemView.context.getColor(R.color.second)
            ValueAnimator.ofArgb(startColor, accent, startColor).apply {
                duration = 320
                addUpdateListener { card.strokeColor = it.animatedValue as Int }
                start()
            }
        }
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): HomeViewHolder {
        val view = LayoutInflater.from(parent.context)
            .inflate(R.layout.lista_voci_menu, parent, false)
        return HomeViewHolder(view)
    }

    override fun onBindViewHolder(holder: HomeViewHolder, position: Int) {
        holder.bind(getItem(position))
    }

    override fun onViewAttachedToWindow(holder: HomeViewHolder) {
        super.onViewAttachedToWindow(holder)
        holder.resume()
    }

    override fun onViewDetachedFromWindow(holder: HomeViewHolder) {
        super.onViewDetachedFromWindow(holder)
        holder.pause()
    }

    private fun subtitleFor(context: Context, action: HomeMenuAction): String {
        val res = context.resources
        return when (action) {
            HomeMenuAction.CREATE_SCHEDE -> res.getString(R.string.home_card_subtitle_create)
            HomeMenuAction.OPEN_SCHEDE -> res.getString(R.string.home_card_subtitle_open)
            HomeMenuAction.VIEW_STATS -> res.getString(R.string.home_card_subtitle_dashboard)
        }
    }

    class AdapterHomeCallback : DiffUtil.ItemCallback<ModelHomemenu>() {
        override fun areItemsTheSame(oldItem: ModelHomemenu, newItem: ModelHomemenu): Boolean {
            return oldItem.action == newItem.action
        }

        override fun areContentsTheSame(oldItem: ModelHomemenu, newItem: ModelHomemenu): Boolean {
            return oldItem == newItem
        }
    }
}

package com.app.progrettofitx.fragments.filtro.adapter

import android.graphics.Color
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ImageView
import android.widget.Toast
import androidx.recyclerview.widget.RecyclerView
import com.app.progrettofitx.R
import com.app.progrettofitx.data_layer.db.SchedeEntity
import com.google.android.material.card.MaterialCardView
import com.google.android.material.textview.MaterialTextView
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch

class SchedaAdapter(
  private val onItemClick: (SchedeEntity) -> Unit,
  private val onItemLongClick: (SchedeEntity) -> Unit,
  private val getCount: suspend (Int) -> Int,
  private val onToggleFavorite: suspend (SchedeEntity, Boolean) -> Unit
) : RecyclerView.Adapter<SchedaAdapter.SchedaHolder>() {

  private val items = mutableListOf<SchedeEntity>()
  private val uiScope = CoroutineScope(Dispatchers.Main)

  inner class SchedaHolder(view: View) : RecyclerView.ViewHolder(view) {
    private val card        = view.findViewById<MaterialCardView>(R.id.card_scheda_item)
    private val groupText   = view.findViewById<MaterialTextView>(R.id.tv_group)
    private val countText   = view.findViewById<MaterialTextView>(R.id.tv_count)
    private val descText    = view.findViewById<MaterialTextView>(R.id.tv_description)
    private val favIcon     = view.findViewById<ImageView>(R.id.ic_attrezzo)
    private val titolo: MaterialTextView   = view.findViewById(R.id.tv_title)
    private val gruppo: MaterialTextView   = view.findViewById(R.id.tv_group)
    private val descr: MaterialTextView    = view.findViewById(R.id.tv_description)
    private val count: MaterialTextView    = view.findViewById(R.id.tv_count)
    private val favoriteIcon: ImageView    = view.findViewById(R.id.ic_attrezzo)

    fun bind(s: SchedeEntity) {

      titolo.text     = s.titolo
      gruppo.text     = s.gruppoMuscolare
      descr.text      = s.notes ?: ""
      count.text      = "… esercizi"

      // placeholder mentre carica
      countText.text = "… esercizi"
      uiScope.launch {
        val cnt = getCount(s.id!!)
        countText.text = "$cnt esercizi"
      }

      // aggiorna colore icona in base a favorite
      val accent = Color.parseColor("#40C4FF")
      val gray   = Color.parseColor("#CCCCCC")
      favIcon.setColorFilter(if (s.favorite) accent else gray)

      // singolo tap → apri dettagli
      card.setOnClickListener { onItemClick(s) }

      // long press → dialog di conferma
      card.setOnLongClickListener {
        onItemLongClick(s)
        true
      }

      // tap su icona → toggle favorite
      favIcon.setOnClickListener {
        uiScope.launch {
          val newFav = !s.favorite
          s.favorite = newFav
          onToggleFavorite(s, newFav)
          favIcon.setColorFilter(if (newFav) accent else gray)
          Toast.makeText(
            it.context,
            if (newFav) "Aggiunto ai preferiti" else "Rimosso dai preferiti",
            Toast.LENGTH_SHORT
          ).show()
        }
      }
    }
  }

  override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): SchedaHolder {
    val v = LayoutInflater.from(parent.context)
      .inflate(R.layout.m_list_schede, parent, false)
    return SchedaHolder(v)
  }

  override fun onBindViewHolder(holder: SchedaHolder, position: Int) {
    holder.bind(items[position])
  }

  override fun getItemCount() = items.size

  fun submitList(newItems: List<SchedeEntity>) {
    items.clear()
    items.addAll(newItems)
    notifyDataSetChanged()
  }
}
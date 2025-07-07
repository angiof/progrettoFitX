package com.app.progrettofitx.ui.schede

import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.recyclerview.widget.RecyclerView
import com.app.progrettofitx.R
import com.app.progrettofitx.data_layer.db.SchedeEntity
import com.google.android.material.card.MaterialCardView
import com.google.android.material.textview.MaterialTextView

class SchedaAdapter(
  var items: List<SchedeEntity> = emptyList()
) : RecyclerView.Adapter<SchedaAdapter.SchedaHolder>() {

  inner class SchedaHolder(view: View) : RecyclerView.ViewHolder(view) {
    private val titolo: MaterialTextView = view.findViewById(R.id.m_titolo)
    private val gruppo: MaterialTextView = view.findViewById(R.id.m_grupppo_muscolare)
    private val descr: MaterialTextView = view.findViewById(R.id.m_descrizione)

    fun bind(s: SchedeEntity) {
      titolo.text = s.titolo
      gruppo.text = s.gruppoMuscolare
      descr.text = s.notes ?: ""  // usa notes come descrizione
    }
  }

  override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): SchedaHolder {
    val view = LayoutInflater.from(parent.context)
      .inflate(R.layout.m_list_schede, parent, false)
    return SchedaHolder(view)
  }

  override fun onBindViewHolder(holder: SchedaHolder, position: Int) {
    holder.bind(items[position])
  }

  override fun getItemCount(): Int = items.size

  /** Per aggiornare la lista da fuori */
  fun updateList(newItems: List<SchedeEntity>) {
    items = newItems
    notifyDataSetChanged()
  }
}
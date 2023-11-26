package com.app.progrettofitx.ui.shedeForms.ui

import android.graphics.Canvas
import android.view.View
import android.view.ViewGroup
import androidx.recyclerview.widget.ItemTouchHelper
import androidx.recyclerview.widget.RecyclerView
import com.app.progrettofitx.R
import com.app.progrettofitx.ui.shedeForms.recyclreview.EserciziAdapter

class SwipeCallback(private val adapter: EserciziAdapter) : ItemTouchHelper.SimpleCallback(0, ItemTouchHelper.RIGHT or ItemTouchHelper.LEFT) {

    override fun onMove(
        recyclerView: RecyclerView,
        viewHolder: RecyclerView.ViewHolder,
        target: RecyclerView.ViewHolder
    ): Boolean {
        // Non vogliamo gestire l'evento di spostamento, quindi ritorniamo false
        return false
    }

    override fun onSwiped(viewHolder: RecyclerView.ViewHolder, direction: Int) {
        val position = viewHolder.adapterPosition
        val esercizi = adapter.currentList[position]

        // Qui puoi mostrare un dialogo o un menu che chiede all'utente se vuole cancellare o modificare l'elemento
        // Per ora, come esempio, lo cancelliamo direttamente
        adapter.notifyItemRemoved(position)
    }


    override fun onChildDraw(
        c: Canvas,
        recyclerView: RecyclerView,
        viewHolder: RecyclerView.ViewHolder,
        dX: Float,
        dY: Float,
        actionState: Int,
        isCurrentlyActive: Boolean
    ) {
        val itemView = viewHolder.itemView
        val itemContent = itemView.findViewById<View>(R.id.itemConte)

        // Se stiamo swipando verso sinistra
        if (dX < 0) {
            itemContent.translationX = dX
        } else {
            itemContent.translationX = 0f
        }

        super.onChildDraw(c, recyclerView, viewHolder, dX, dY, actionState, isCurrentlyActive)
    }


}

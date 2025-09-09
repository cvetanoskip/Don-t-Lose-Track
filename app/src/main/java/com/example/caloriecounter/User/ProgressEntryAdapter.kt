package com.example.caloriecounter.User

import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Button
import android.widget.ImageButton
import android.widget.TextView
import androidx.recyclerview.widget.RecyclerView
import com.example.caloriecounter.R

class ProgressEntryAdapter(
    private var entries: MutableList<ProgressEntry>,
    private val onDeleteClicked: (ProgressEntry) -> Unit
) : RecyclerView.Adapter<ProgressEntryAdapter.ViewHolder>() {

    class ViewHolder(itemView: View) : RecyclerView.ViewHolder(itemView) {
        val dateText: TextView = itemView.findViewById(R.id.entry_date)
        val weightText: TextView = itemView.findViewById(R.id.entry_weight)
        val bodyFatText: TextView = itemView.findViewById(R.id.entry_bodyfat)
        val deleteButton: ImageButton = itemView.findViewById(R.id.delete_button)
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ViewHolder {
        val view = LayoutInflater.from(parent.context)
            .inflate(R.layout.progress_entry_item, parent, false)
        return ViewHolder(view)
    }

    override fun onBindViewHolder(holder: ViewHolder, position: Int) {
        val entry = entries[position]
        holder.dateText.text = entry.date
        holder.weightText.text = "Weight: ${entry.weight} kg"
        holder.bodyFatText.text = "Body Fat: ${entry.bodyFat} %"
        holder.deleteButton.setOnClickListener {
            onDeleteClicked(entry)
        }
    }

    override fun getItemCount(): Int = entries.size

    fun updateList(newEntries: List<ProgressEntry>) {
        entries.clear()
        entries.addAll(newEntries)
        notifyDataSetChanged()
    }
}



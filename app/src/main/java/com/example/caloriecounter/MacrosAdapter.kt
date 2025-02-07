package com.example.caloriecounter

import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.TextView
import androidx.recyclerview.widget.RecyclerView

class MacrosAdapter(private val macrosList: List<Map<String, Any>>) : RecyclerView.Adapter<MacrosAdapter.MacroViewHolder>() {

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): MacroViewHolder {
        val view = LayoutInflater.from(parent.context).inflate(R.layout.item_macro, parent, false)
        return MacroViewHolder(view)
    }

    override fun onBindViewHolder(holder: MacroViewHolder, position: Int) {
        val macro = macrosList[position]
        holder.proteinTextView.text = "Protein: ${macro["protein"]}g"
        holder.carbsTextView.text = "Carbs: ${macro["carbs"]}g"
        holder.fatsTextView.text = "Fats: ${macro["fats"]}g"
        holder.caloriesTextView.text = "Calories: ${macro["calories"]} kcal"
    }

    override fun getItemCount(): Int {
        return macrosList.size
    }

    class MacroViewHolder(itemView: View) : RecyclerView.ViewHolder(itemView) {
        val proteinTextView: TextView = itemView.findViewById(R.id.protein_text)
        val carbsTextView: TextView = itemView.findViewById(R.id.carbs_text)
        val fatsTextView: TextView = itemView.findViewById(R.id.fats_text)
        val caloriesTextView: TextView = itemView.findViewById(R.id.calories_text)
    }
}


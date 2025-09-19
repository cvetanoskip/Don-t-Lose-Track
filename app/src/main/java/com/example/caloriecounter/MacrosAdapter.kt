package com.example.caloriecounter

import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Button
import android.widget.TextView
import androidx.recyclerview.widget.RecyclerView
import java.text.SimpleDateFormat
import java.util.Locale


class MacrosAdapter( val macrosList: MutableList<Map<String, Any>>,  val onDeleteClick: (String, Int) -> Unit) :
    RecyclerView.Adapter<MacrosAdapter.MacroViewHolder>() {
    fun updateData(newList: MutableList<Map<String, Any>>) {
        macrosList.clear()
        macrosList.addAll(newList)
        notifyDataSetChanged()
    }


    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): MacroViewHolder {
        val view = LayoutInflater.from(parent.context)
            .inflate(R.layout.item_macro, parent, false)
        return MacroViewHolder(view)
    }

    override fun onBindViewHolder(holder: MacroViewHolder, position: Int) {
        val macro = macrosList[position]

        val date = macro["date"] as? String ?: "Unknown Date"
        val dayOfWeek = try {
            val sdf = SimpleDateFormat("yyyy-MM-dd", Locale.getDefault())
            val dateObj = sdf.parse(date)
            val dayFormat = SimpleDateFormat("EEEE", Locale.getDefault())
            dayFormat.format(dateObj!!)
        } catch (e: Exception) {
            "Unknown Day"
        }
        holder.dateTextView.text = dayOfWeek

        val protein = (macro["protein"] as? Double)?.toInt() ?: 0
        val carbs = (macro["carbs"] as? Double)?.toInt() ?: 0
        val fats = (macro["fats"] as? Double)?.toInt() ?: 0
        val fiber = (macro["fiber"] as? Double)?.toInt() ?: 0

        // Adjust calories for fiber
        val adjustedCalories = (protein * 4) + ((carbs - fiber) * 4) + (fiber * 2) + (fats * 9)

        val deleteButton: Button = holder.itemView.findViewById(R.id.delete_button)
        val docId = macro["id"] as? String
        if (docId != null) {
            deleteButton.setOnClickListener {
                onDeleteClick(docId, position)
            }
        }

        holder.proteinTextView.text = "Protein: ${protein}g"
        holder.carbsTextView.text = "Carbs: ${carbs}g"
        holder.fatsTextView.text = "Fats: ${fats}g"
        holder.fiberTextView.text = "Fiber: ${fiber}g"
        holder.caloriesTextView.text = "Calories: ${adjustedCalories} kcal"
    }


    override fun getItemCount(): Int {
        return macrosList.size
    }

    class MacroViewHolder(itemView: View) : RecyclerView.ViewHolder(itemView) {
        val dateTextView: TextView = itemView.findViewById(R.id.date_text) // new TextView for date
        val proteinTextView: TextView = itemView.findViewById(R.id.protein_text)
        val carbsTextView: TextView = itemView.findViewById(R.id.carbs_text)
        val fatsTextView: TextView = itemView.findViewById(R.id.fats_text)
        val caloriesTextView: TextView = itemView.findViewById(R.id.calories_text)
        val fiberTextView: TextView = itemView.findViewById(R.id.fiber_text)
    }
}


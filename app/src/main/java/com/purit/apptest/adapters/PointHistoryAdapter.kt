package com.purit.apptest.adapters

import android.graphics.Color
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.TextView
import androidx.recyclerview.widget.RecyclerView
import com.purit.apptest.models.PointTransaction

class PointHistoryAdapter(private val list: List<PointTransaction>) :
    RecyclerView.Adapter<PointHistoryAdapter.ViewHolder>() {

    class ViewHolder(view: View) : RecyclerView.ViewHolder(view) {
        val tvDesc: TextView = view.findViewById(android.R.id.text1)
        val tvDate: TextView = view.findViewById(android.R.id.text2)
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ViewHolder {
        val view = LayoutInflater.from(parent.context)
            .inflate(android.R.layout.simple_list_item_2, parent, false)
        return ViewHolder(view)
    }

    override fun onBindViewHolder(holder: ViewHolder, position: Int) {
        val item = list[position]
        val displayTitle = if (!item.source_name.isNullOrBlank() && item.source_name != "-") {
            item.source_name
        } else {
            item.description ?: "รายการทั่วไป"
        }

        val prefix = if (item.points > 0) "+" else ""
        holder.tvDesc.text = "$displayTitle ($prefix${item.points} pts)"

        if (item.points > 0) {
            holder.tvDesc.setTextColor(Color.parseColor("#4CAF50"))
        } else {
            holder.tvDesc.setTextColor(Color.parseColor("#F44336"))
        }
        holder.tvDate.text = item.created_at
    }

    override fun getItemCount(): Int = list.size
}
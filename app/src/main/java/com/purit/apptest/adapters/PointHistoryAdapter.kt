package com.purit.apptest.adapters

import android.graphics.Color
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.TextView
import androidx.recyclerview.widget.RecyclerView
import com.purit.apptest.R
import com.purit.apptest.models.PointTransactionItem

class PointHistoryAdapter(
    private val historyList: MutableList<PointTransactionItem>
) : RecyclerView.Adapter<PointHistoryAdapter.HistoryViewHolder>() {

    class HistoryViewHolder(view: View) : RecyclerView.ViewHolder(view) {
        val tvDesc: TextView = view.findViewById(R.id.tv_item_description)
        val tvDate: TextView = view.findViewById(R.id.tv_item_date)
        val tvPoints: TextView = view.findViewById(R.id.tv_item_points)
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): HistoryViewHolder {
        val view = LayoutInflater.from(parent.context)
            .inflate(R.layout.item_point_history, parent, false)
        return HistoryViewHolder(view)
    }

    override fun onBindViewHolder(holder: HistoryViewHolder, position: Int) {
        val item = historyList[position]

        holder.tvDesc.text = item.description ?: item.source_name
        holder.tvDate.text = item.created_at

        val isEarn = item.type == "reward"

        if (isEarn) {
            holder.tvPoints.text = "+${item.points} pts"
            holder.tvPoints.setTextColor(Color.parseColor("#4CAF50"))
        } else {
            holder.tvPoints.text = "${item.points} pts"
            holder.tvPoints.setTextColor(Color.parseColor("#FF4E50"))
        }
    }

    override fun getItemCount(): Int = historyList.size

    fun updateData(newList: List<PointTransactionItem>) {
        historyList.clear()
        historyList.addAll(newList)
        notifyDataSetChanged()
    }
}

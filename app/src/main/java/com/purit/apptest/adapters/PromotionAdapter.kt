package com.purit.apptest.adapters

import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ImageView
import android.widget.TextView
import androidx.recyclerview.widget.RecyclerView
import com.bumptech.glide.Glide
import com.purit.apptest.R
import com.purit.apptest.models.PromotionResponse

class PromotionAdapter(private val onRedeemClick: (PromotionResponse) -> Unit) :
    RecyclerView.Adapter<PromotionAdapter.PromoViewHolder>() {

    private var items = listOf<PromotionResponse>()

    fun updateList(newList: List<PromotionResponse>) {
        items = newList
        notifyDataSetChanged()
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): PromoViewHolder {
        val view = LayoutInflater.from(parent.context).inflate(R.layout.item_promotion, parent, false)
        return PromoViewHolder(view)
    }

    override fun onBindViewHolder(holder: PromoViewHolder, position: Int) {
        val item = items[position]
        holder.tvTitle.text = item.title
        holder.tvDesc.text = item.description
        holder.tvPoints.text = "${if (item.type == "reward") "+" else ""}${item.points_value} pts"

        // โหลดรูปภาพจาก Laravel Storage
        val imageUrl = "http://172.16.200.33:8000/storage/${item.image}"
        Glide.with(holder.itemView.context)
            .load(imageUrl)
            .placeholder(R.drawable.ic_browser) // ไอคอนที่คุณสร้างไว้ตอนแรก
            .into(holder.ivPromo)

        holder.itemView.setOnClickListener { onRedeemClick(item) }
    }

    override fun getItemCount() = items.size

    class PromoViewHolder(view: View) : RecyclerView.ViewHolder(view) {
        val tvTitle: TextView = view.findViewById(R.id.text_promo_title)
        val tvDesc: TextView = view.findViewById(R.id.text_promo_description)
        val tvPoints: TextView = view.findViewById(R.id.text_promo_points)
        val ivPromo: ImageView = view.findViewById(R.id.iv_promo_image)
    }
}
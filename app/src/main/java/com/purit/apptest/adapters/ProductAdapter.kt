package com.purit.apptest.adapters

import android.graphics.Color
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ImageView
import android.widget.TextView
import androidx.recyclerview.widget.RecyclerView
import com.bumptech.glide.Glide
import com.purit.apptest.R
import com.purit.apptest.models.ProductItem

// เพิ่ม parameter onItemClick เข้าไปใน Constructor
class ProductAdapter(
    private val productList: List<ProductItem>,
    private val onItemClick: (ProductItem) -> Unit
) : RecyclerView.Adapter<ProductAdapter.ProductViewHolder>() {

    inner class ProductViewHolder(view: View) : RecyclerView.ViewHolder(view) {
        val imgDrink: ImageView = view.findViewById(R.id.item_drink_image)
        val tvName: TextView = view.findViewById(R.id.item_drink_name)
        val tvPrice: TextView = view.findViewById(R.id.item_drink_price)
        val tvPointsBadge: TextView = view.findViewById(R.id.item_drink_points_badge)
        val tvRedeemStatus: TextView = view.findViewById(R.id.item_redeem_status)
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ProductViewHolder {
        val view = LayoutInflater.from(parent.context)
            .inflate(R.layout.item_product, parent, false)
        return ProductViewHolder(view)
    }

    override fun onBindViewHolder(holder: ProductViewHolder, position: Int) {
        val product = productList[position]

        // 1. ตั้งค่าข้อมูลพื้นฐาน
        holder.tvName.text = product.name
        holder.tvPrice.text = "${product.price.toInt()} ฿"

        Glide.with(holder.itemView.context)
            .load(product.image)
            .placeholder(R.drawable.iced_latte)
            .into(holder.imgDrink)

        // 2. เงื่อนไขเช็คการแลกแต้ม (Redeemable)
        if (product.redeemable) {
            holder.tvPointsBadge.visibility = View.VISIBLE
            holder.tvPointsBadge.text = "${product.points_required} Pts"
            holder.tvRedeemStatus.text = "เปิดให้แลกด้วยแต้ม"
            holder.tvRedeemStatus.setTextColor(Color.parseColor("#4CAF50"))
        } else {
            holder.tvPointsBadge.visibility = View.GONE
            holder.tvRedeemStatus.text = "ยังไม่เปิดให้แลกด้วยแต้ม"
            holder.tvRedeemStatus.setTextColor(Color.parseColor("#757575"))
        }

        // --- ส่วนสำคัญ: ดักจับการคลิกที่ Item ---
        holder.itemView.setOnClickListener {
            onItemClick(product) // ส่งข้อมูล product กลับไปที่ HomeFragment
        }
    }

    override fun getItemCount(): Int = productList.size
}
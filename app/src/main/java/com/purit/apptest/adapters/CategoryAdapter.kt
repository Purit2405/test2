package com.purit.apptest.adapters

import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ImageView
import android.widget.TextView
import androidx.recyclerview.widget.RecyclerView
import coil.decode.SvgDecoder
import coil.load
import com.purit.apptest.R
import com.purit.apptest.models.CategoryItem

class CategoryAdapter(
    private val categories: List<CategoryItem>,
    private val onItemClick: (CategoryItem) -> Unit
) : RecyclerView.Adapter<CategoryAdapter.ViewHolder>() {

    inner class ViewHolder(view: View) : RecyclerView.ViewHolder(view) {
        // ID ตรงกับ XML (imgCategory และ tvCategoryName)
        val imgCategory: ImageView = view.findViewById(R.id.imgCategory)
        val tvCategoryName: TextView = view.findViewById(R.id.tvCategoryName)
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ViewHolder {
        val view = LayoutInflater.from(parent.context)
            .inflate(R.layout.item_category, parent, false)
        return ViewHolder(view)
    }

    override fun onBindViewHolder(holder: ViewHolder, position: Int) {
        val category = categories[position]
        holder.tvCategoryName.text = category.name

        // 1. ตั้งค่า Base URL (⚠️ แก้ IP ให้ตรงกับเครื่องที่รัน Laravel ของคุณ)
        val baseUrl = "http://192.168.1.100:8000"

        // 2. จัดการ Path รูปภาพ (รวม Base URL เข้ากับ Path จาก API)
        val iconPath = category.icon ?: ""
        val fullUrl = if (iconPath.startsWith("http")) {
            iconPath
        } else {
            // ป้องกันเครื่องหมาย / ซ้ำซ้อน
            val cleanPath = if (iconPath.startsWith("/")) iconPath else "/$iconPath"
            baseUrl + cleanPath
        }

        // 3. ใช้ Coil โหลดรูป (รองรับ SVG ตามที่คุณลง Lib ไว้)
        holder.imgCategory.load(fullUrl) {
            if (fullUrl.endsWith(".svg", ignoreCase = true)) {
                decoderFactory(SvgDecoder.Factory()) // ตัวถอดรหัส SVG
            }
            crossfade(true)
            placeholder(R.drawable.ic_category_placeholder)
            error(android.R.drawable.stat_notify_error)
        }

        holder.itemView.setOnClickListener {
            onItemClick(category)
        }
    }

    override fun getItemCount(): Int = categories.size
}
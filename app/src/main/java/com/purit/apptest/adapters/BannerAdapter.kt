package com.purit.apptest.adapters

import android.content.Intent
import android.net.Uri
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ImageView
import android.widget.Toast
import androidx.recyclerview.widget.RecyclerView
import com.bumptech.glide.Glide
import com.bumptech.glide.load.resource.drawable.DrawableTransitionOptions
import com.purit.apptest.R
import com.purit.apptest.models.BannerItem

class BannerAdapter(private val banners: List<BannerItem>) :
    RecyclerView.Adapter<BannerAdapter.BannerViewHolder>() {

    inner class BannerViewHolder(view: View) : RecyclerView.ViewHolder(view) {
        val imageView: ImageView = view.findViewById(R.id.ivBanner)
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): BannerViewHolder {
        val view = LayoutInflater.from(parent.context)
            .inflate(R.layout.item_banner, parent, false)
        return BannerViewHolder(view)
    }

    override fun onBindViewHolder(holder: BannerViewHolder, position: Int) {
        val banner = banners[position]

        // โหลดรูปจาก URL ที่ส่งมาจาก Laravel
        Glide.with(holder.itemView.context)
            .load(banner.image)
            .transition(DrawableTransitionOptions.withCrossFade())
            .placeholder(R.drawable.login_bg)
            .error(R.drawable.login_bg)
            .into(holder.imageView)

        // ตั้งค่าการคลิกเพื่อเปิดลิงก์
        holder.itemView.setOnClickListener {
            if (!banner.link.isNullOrBlank()) {
                try {
                    val intent = Intent(Intent.ACTION_VIEW, Uri.parse(banner.link))
                    holder.itemView.context.startActivity(intent)
                } catch (e: Exception) {
                    Toast.makeText(holder.itemView.context, "ไม่สามารถเปิดลิงก์ได้", Toast.LENGTH_SHORT).show()
                }
            }
        }
    }

    override fun getItemCount(): Int = banners.size
}
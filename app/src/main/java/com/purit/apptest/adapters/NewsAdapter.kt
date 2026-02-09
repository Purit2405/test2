package com.purit.apptest.adapters

import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ImageView
import android.widget.TextView
import androidx.recyclerview.widget.RecyclerView
import com.bumptech.glide.Glide
import com.purit.apptest.R
import com.purit.apptest.models.NewsItem

class NewsAdapter(private val newsList: List<NewsItem>) :
    RecyclerView.Adapter<NewsAdapter.NewsViewHolder>() {

    inner class NewsViewHolder(view: View) : RecyclerView.ViewHolder(view) {
        val tvNewsDate: TextView = view.findViewById(R.id.tv_news_date)
        val tvNewsTime: TextView = view.findViewById(R.id.tv_news_time)
        val tvNewsTitle: TextView = view.findViewById(R.id.tv_news_title)
        val imageNews: ImageView = view.findViewById(R.id.image_news)
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): NewsViewHolder {
        val view = LayoutInflater.from(parent.context)
            .inflate(R.layout.item_news, parent, false)
        return NewsViewHolder(view)
    }

    override fun onBindViewHolder(holder: NewsViewHolder, position: Int) {
        val news = newsList[position]

        holder.tvNewsTitle.text = news.title

        // จัดการวันที่และเวลาจาก publish_date (สมมติ format: YYYY-MM-DD HH:mm:ss)
        val dateString = news.publishDate ?: ""
        if (dateString.contains(" ")) {
            val parts = dateString.split(" ")
            holder.tvNewsDate.text = parts[0]
            holder.tvNewsTime.text = "${parts[1].substring(0, 5)} น."
        } else {
            holder.tvNewsDate.text = dateString
            holder.tvNewsTime.text = "00:00 น."
        }

        Glide.with(holder.itemView.context)
            .load(news.image)
            .placeholder(R.drawable.news1)
            .error(R.drawable.news1)
            .centerCrop()
            .into(holder.imageNews)

        // ไม่มีการใส่ setOnClickListener ทำให้กดไม่ได้
    }

    override fun getItemCount(): Int = newsList.size
}
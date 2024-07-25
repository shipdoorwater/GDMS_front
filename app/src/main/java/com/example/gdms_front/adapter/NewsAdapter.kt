package com.example.gdms_front.adapter

import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ImageView
import android.widget.TextView
import androidx.recyclerview.widget.RecyclerView
import com.example.gdms_front.R
import com.example.gdms_front.model.NewsArticle
import com.example.gdms_front.news.OnNewsClickListener
import com.squareup.picasso.Picasso

class NewsAdapter(private val newsList: List<NewsArticle>, private val listener: OnNewsClickListener) : RecyclerView.Adapter<NewsAdapter.NewsViewHolder>() {

    //ViewHolder 클래스 : 뉴시 가시의 UI 구성요소를 보유하는데.. 여기에 주소도 보내줘아할지도??
    class NewsViewHolder(itemView: View) : RecyclerView.ViewHolder(itemView) {
        val title: TextView = itemView.findViewById(R.id.news_title)
        val description: TextView = itemView.findViewById(R.id.news_description)
        val image: ImageView = itemView.findViewById(R.id.news_image)
    }

    //아이템 뷰를 위한 뷰홀더 객체 생성
    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): NewsViewHolder {
        val view = LayoutInflater.from(parent.context).inflate(R.layout.item_news, parent, false)
        return NewsViewHolder(view)
    }

    //Viewholder에 데이터를 설정
    override fun onBindViewHolder(holder: NewsViewHolder, position: Int) {
        val newsArticle = newsList[position]
        holder.title.text = newsArticle.title
        holder.description.text = newsArticle.description
        Picasso.get().load(newsArticle.imageUrl).into(holder.image)
        holder.itemView.setOnClickListener {
            listener.onNewsClick(newsArticle)
        }
    }

    // 아이템 수를 반환 ~
    override fun getItemCount() = newsList.size
}
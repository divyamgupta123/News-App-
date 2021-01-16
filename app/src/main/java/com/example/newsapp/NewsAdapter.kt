package com.example.newsapp

import android.content.Context
import android.content.Intent
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Button
import android.widget.ImageView
import android.widget.TextView
import android.widget.Toast
import androidx.recyclerview.widget.RecyclerView
import com.bumptech.glide.Glide
import com.bumptech.glide.load.resource.bitmap.CenterCrop
import com.bumptech.glide.load.resource.bitmap.RoundedCorners

class NewsAdapter(private val context: Context, private val articles: List<Article>) :
    RecyclerView.Adapter<NewsAdapter.ArticleViewHolder>() {
    class ArticleViewHolder(itemView: View) : RecyclerView.ViewHolder(itemView) {
        var newsImage: ImageView = itemView.findViewById(R.id.newsImage)
        var newsTitle: TextView = itemView.findViewById(R.id.newsTitle)
        var newsDescription: TextView = itemView.findViewById(R.id.newsDescription)
        var shareImage: Button = itemView.findViewById(R.id.shareButton)
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ArticleViewHolder {
        val view: View = LayoutInflater.from(context).inflate(R.layout.item_layout, parent, false)
        return ArticleViewHolder(view)
    }

    override fun onBindViewHolder(holder: ArticleViewHolder, position: Int) {
        val article: Article = articles[position]
        holder.newsTitle.text = article.title
        if (article.description == "") {
            holder.newsDescription.text = "Sorry!, News description not available."
        } else {
            holder.newsDescription.text = article.description
        }
        //loading the image using Glide library
        Glide.with(context).load(article.urlToImage).transform(CenterCrop(), RoundedCorners(20))
            .error(R.drawable.imagenf)
            .into(holder.newsImage)
        holder.itemView.setOnClickListener {
            //Opening the source of the news within the app itself
            Toast.makeText(context, "Loading the source...", Toast.LENGTH_SHORT).show()
            val intent = Intent(context, DetailActivity::class.java)
            intent.putExtra("URL", article.url)
            context.startActivity(intent)
        }
        holder.shareImage.setOnClickListener {
            //Intent for sharing the news url with third party applications
            val intent = Intent(Intent.ACTION_SEND)
            intent.type = "text/*"
            intent.putExtra(Intent.EXTRA_TEXT, "Checkout this latest news ${article.url}")
            val chooser = Intent.createChooser(intent, "Share this news using....")
            context.startActivity(chooser)
        }
    }

    override fun getItemCount(): Int {
        return articles.size
    }
}
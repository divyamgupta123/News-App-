package com.example.newsapp

import android.annotation.SuppressLint
import android.os.Bundle
import android.util.Log
import android.view.View
import androidx.appcompat.app.AppCompatActivity
import com.example.newsapp.databinding.ActivityCategoryBinding
import com.littlemango.stacklayoutmanager.StackLayoutManager
import retrofit2.Call
import retrofit2.Callback
import retrofit2.Response

class CategoryActivity : AppCompatActivity() {
    private lateinit var binding: ActivityCategoryBinding
    private lateinit var adapter:NewsAdapter
    private var articles = mutableListOf<Article>()
    var pageNum: Int = 1
    var totalResult: Int = -1
    private var news: Call<News>? = null
    private var category: String? = null


    @SuppressLint("RestrictedApi")
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityCategoryBinding.inflate(layoutInflater)
        setContentView(binding.root)
        adapter = NewsAdapter(this, articles)
        binding.newsList.adapter = adapter
        category = intent.getStringExtra("category")

        binding.appBar.title = "Newsify - ${category?.capitalize()} Headlines"

        val layoutManager = StackLayoutManager(StackLayoutManager.ScrollOrientation.BOTTOM_TO_TOP)
        layoutManager.setPagerMode(true)
        layoutManager.setPagerFlingVelocity(3000)
        layoutManager.setItemChangedListener(
            object : StackLayoutManager.ItemChangedListener {
                override fun onItemChanged(position: Int) {
                    if (totalResult > layoutManager.itemCount && layoutManager.getFirstVisibleItemPosition() >= layoutManager.itemCount - 5) {
                        //next page
                        pageNum++
                        getNews()
                    }
                }
            })
        binding.newsList.layoutManager = layoutManager
        getNews()
    }

    private fun getNews() {
        news = NewsService.newsInstance.getHeadlines("in", pageNum, category!!)
        news?.enqueue(object : Callback<News> {
            override fun onResponse(call: Call<News>, response: Response<News>) {
                val news: News? = response.body()
                if (news != null) {
                    binding.progressBar.visibility = View.GONE
                    binding.newsList.visibility = View.VISIBLE
                    totalResult = news.totalResults
                    articles.addAll(news.articles)
                    adapter.notifyDataSetChanged()
                }
            }

            override fun onFailure(call: Call<News>, t: Throwable) {
                Log.d("Main Activity", "Error:- ", t)
            }
        })
    }
}

package com.example.newsapp

import android.annotation.SuppressLint
import android.content.Context
import android.content.Intent
import android.os.Bundle
import android.util.Log
import android.view.MenuItem
import android.view.View
import androidx.appcompat.app.ActionBarDrawerToggle
import androidx.appcompat.app.AppCompatActivity
import com.example.newsapp.databinding.ActivityMainBinding
import com.littlemango.stacklayoutmanager.StackLayoutManager
import retrofit2.Call
import retrofit2.Callback
import retrofit2.Response
import java.io.File

class MainActivity : AppCompatActivity() {
    private lateinit var binding: ActivityMainBinding
    private lateinit var toggle: ActionBarDrawerToggle
    private lateinit var adapter: NewsAdapter
    private var articles = mutableListOf<Article>()
    private var pageNum: Int = 1
    private var totalResult: Int = -1
    private var news: Call<News>? = null

    @SuppressLint("RestrictedApi")
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        //View Binding
        binding = ActivityMainBinding.inflate(layoutInflater)
        setContentView(binding.root)
        setSupportActionBar(binding.appBar)

        binding.appBar.title = "Newsify - Top Headlines"

        //Setting up Drawer Layout
        toggle = ActionBarDrawerToggle(this, binding.drawerLayout, R.string.open, R.string.close)
        binding.drawerLayout.addDrawerListener(toggle)
        toggle.syncState()

        supportActionBar?.setDefaultDisplayHomeAsUpEnabled(true)
        supportActionBar?.setHomeButtonEnabled(true)

        //Click Event for the navigation menu items
        //Opening an intent for different categories of news
        binding.navView.setNavigationItemSelectedListener {
            when (it.itemId) {
                R.id.business -> {
                    val intent = Intent(this@MainActivity, CategoryActivity::class.java)
                    intent.putExtra("category", "business")
                    startActivity(intent)
                }
                R.id.entertainment -> {
                    val intent = Intent(this@MainActivity, CategoryActivity::class.java)
                    intent.putExtra("category", "entertainment")
                    startActivity(intent)
                }
                R.id.health -> {
                    val intent = Intent(this@MainActivity, CategoryActivity::class.java)
                    intent.putExtra("category", "entertainment")
                    startActivity(intent)
                }
                R.id.science -> {
                    val intent = Intent(this@MainActivity, CategoryActivity::class.java)
                    intent.putExtra("category", "science")
                    startActivity(intent)
                }
                R.id.sports -> {
                    val intent = Intent(this@MainActivity, CategoryActivity::class.java)
                    intent.putExtra("category", "sports")
                    startActivity(intent)
                }
                R.id.technology -> {
                    val intent = Intent(this@MainActivity, CategoryActivity::class.java)
                    intent.putExtra("category", "technology")
                    startActivity(intent)
                }
            }
            true
        }

        adapter = NewsAdapter(this@MainActivity, articles)
        binding.newsList.adapter = adapter

        //Setting the layout manager using StackLayout Library
        val layoutManager = StackLayoutManager(StackLayoutManager.ScrollOrientation.BOTTOM_TO_TOP)
        layoutManager.setPagerMode(true)
        layoutManager.setPagerFlingVelocity(3000)

        //Loading next page from the api before ending the first page
        layoutManager.setItemChangedListener(object : StackLayoutManager.ItemChangedListener {
            override fun onItemChanged(position: Int) {
                if (totalResult > layoutManager.itemCount && layoutManager.getFirstVisibleItemPosition() >= layoutManager.itemCount - 5) {
                    //next page:- load the after 20 news
                    pageNum++
                    getNews()
                }
            }
        })
        binding.newsList.layoutManager = layoutManager
        getNews()
    }


    override fun onDestroy() {
        //For clearing the cache when the app is destroyed
        deleteCache(this)
        super.onDestroy()
    }

    private fun deleteCache(context: Context) {
        try {
            val dir: File = context.cacheDir
            deleteDir(dir)
        } catch (e: Exception) {
            e.printStackTrace()
        }
    }

    @Suppress("NULLABILITY_MISMATCH_BASED_ON_JAVA_ANNOTATIONS")
    private fun deleteDir(dir: File?): Boolean {
        return if (dir != null && dir.isDirectory) {
            val children: Array<String> = dir.list()
            for (i in children.indices) {
                val success = deleteDir(File(dir, children[i]))
                if (!success) {
                    return false
                }
            }
            dir.delete()
        } else if (dir != null && dir.isFile) {
            dir.delete()
        } else {
            false
        }
    }

    override fun onOptionsItemSelected(item: MenuItem): Boolean {
        if (toggle.onOptionsItemSelected(item)) {
            return true
        }
        return super.onOptionsItemSelected(item)
    }

    //Loading the news and binding it with adapter
    private fun getNews() {
        news = NewsService.newsInstance.getHeadlines("in", pageNum, "")
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
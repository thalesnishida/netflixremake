package com.example.netflixremake

import android.annotation.SuppressLint
import android.graphics.Bitmap
import android.graphics.drawable.BitmapDrawable
import android.graphics.drawable.LayerDrawable
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.view.MenuItem
import android.view.View
import android.widget.ImageView
import android.widget.ProgressBar
import android.widget.TextView
import android.widget.Toast
import androidx.appcompat.widget.Toolbar
import androidx.core.content.ContextCompat
import androidx.recyclerview.widget.GridLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.example.netflixremake.model.Movie
import com.example.netflixremake.model.MovieDetail
import com.example.netflixremake.util.DownloadImageTask
import com.example.netflixremake.util.MovieTask
import java.lang.IllegalStateException

class MovieActivity : AppCompatActivity(), MovieTask.Callback {
    private lateinit var textTitle: TextView
    private lateinit var textDesc: TextView
    private lateinit var textCast: TextView
    private lateinit var progress: ProgressBar
    private lateinit var adapter: MovieAdpater

    private val movies = mutableListOf<Movie>()

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_movie)


        textTitle = findViewById(R.id.movie_text_title)
        textDesc = findViewById(R.id.movie_text_desc)
        textCast = findViewById(R.id.movie_text_cast)
        progress = findViewById(R.id.progress_movie)
        val rv = findViewById<RecyclerView>(R.id.movie_rv_similar)


        val id = intent?.getIntExtra("id", 0 ) ?: IllegalStateException("Id não encontrado!")
        val url = ""
        MovieTask(this).execute(url)


        adapter = MovieAdpater(movies, R.layout.movie_item_similar)
        rv.layoutManager = GridLayoutManager(this, 3)
        rv.adapter = adapter

        val toolbar: Toolbar = findViewById(R.id.movie_toolbar)
        setSupportActionBar(toolbar)

        supportActionBar?.setHomeAsUpIndicator(R.drawable.baseline_arrow_back_24)
        supportActionBar?.setDisplayHomeAsUpEnabled(true)
        supportActionBar?.title = null

    }

    override fun onPreExecute() {
        progress.visibility = View.VISIBLE
    }

    override fun onFailure(message: String) {
        progress.visibility = View.GONE
        Toast.makeText(this, message, Toast.LENGTH_SHORT).show()
        finish()
    }

    @SuppressLint("NotifyDataSetChanged")
    override fun onResult(details: MovieDetail) {
        progress.visibility = View.GONE

        textTitle.text = details.movie.title
        textDesc.text = details.movie.desc
        textCast.text = getString(R.string.cast, details.movie.cast)

        movies.clear()
        movies.addAll(details.similarMovie)
        adapter.notifyDataSetChanged()

        DownloadImageTask(object : DownloadImageTask.Callback {
            override fun onResult(bitmap: Bitmap) {
                val layerDrawable: LayerDrawable = ContextCompat.getDrawable(this@MovieActivity, R.drawable.shadows) as LayerDrawable
                val imgMovie = BitmapDrawable(resources, bitmap)
                layerDrawable.setDrawableByLayerId(R.id.cover_drawable, imgMovie)
                val coverImg: ImageView = findViewById(R.id.image_movie)
                coverImg.setImageDrawable(layerDrawable)
            }
        }).execute(details.movie.coverUrl)
    }
    override fun onOptionsItemSelected(item: MenuItem): Boolean {
        if(item.itemId == android.R.id.home){
            finish()
        }

        return super.onOptionsItemSelected(item)
    }
}
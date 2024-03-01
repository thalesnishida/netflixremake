package com.example.netflixremake.util

import android.os.Handler
import android.os.Looper
import android.util.Log
import com.example.netflixremake.model.Category
import com.example.netflixremake.model.Movie
import org.json.JSONObject
import java.io.BufferedInputStream
import java.io.ByteArrayOutputStream
import java.io.IOException
import java.io.InputStream
import java.net.URL
import java.util.concurrent.Executors
import javax.net.ssl.HttpsURLConnection

class CategoryTask(private val callback: Callback  ) {

    interface Callback {
        fun onPreExecute()
        fun onResume(categories: List<Category>)
        fun onFailure(message: String)
    }


    fun execute(url: String) {
        callback.onPreExecute()

        val executor = Executors.newSingleThreadExecutor()

        val handle = Handler(Looper.getMainLooper())

        executor.execute {

            var urlConnection: HttpsURLConnection? = null
            var buffer: BufferedInputStream? = null
            var stream: InputStream? = null

            try {
                val requestUrl = URL(url)
                urlConnection = requestUrl.openConnection() as HttpsURLConnection
                urlConnection.readTimeout = 2000
                urlConnection.connectTimeout = 2000

                val statusCode = urlConnection.responseCode
                if (statusCode > 400) {
                    throw IOException("Algo deu errado no servidor!")
                }

                stream = urlConnection.inputStream
//                 Primeira forma
//                val toAsString = stream.bufferedReader().use { it.readText() }

//                 Segunda forma
                buffer = BufferedInputStream(stream)
                val jsonAsString = toString(buffer)

                val categories = toCategories(jsonAsString)

                handle.post {
                    callback.onResume(categories)
                }

            } catch (e: IOException) {
                val message = e.message ?: "Erro desconhecido"
                Log.e("Teste", message , e)

                handle.post {
                    callback.onFailure(message)
                }
            }finally {
                urlConnection?.disconnect()
                buffer?.close()
                stream?.close()
            }
        }

    }

    private fun toCategories(jsonAsString: String): List<Category> {
        val categories = mutableListOf<Category>()

        val jsonRoot = JSONObject(jsonAsString)
        val jsonCategory = jsonRoot.getJSONArray("category")

        for (i in 0 until jsonCategory.length()) {
            val jsonObjec = jsonCategory.getJSONObject(i)

            val title = jsonObjec.getString("title")
            val jsonMovies = jsonObjec.getJSONArray("movie")

            val movies = mutableListOf<Movie>()
            for (j in 0 until jsonMovies.length()) {
                val jsonMovie = jsonMovies.getJSONObject(j)
                val id = jsonMovie.getInt("id")
                val coverUrl = jsonMovie.getString("cover_url")

                movies.add(Movie(id, coverUrl))
            }

            categories.add(Category(title, movies))
        }

        return categories
    }

    private fun toString(stream: InputStream): String {
        val bytes = ByteArray(1024)
        val baos = ByteArrayOutputStream()
        var read: Int
        while (true) {
            read = stream.read(bytes)
            if (read <= 0) {
                break
            }
            baos.write(bytes, 0, read)
        }
        return String(baos.toByteArray())
    }

}
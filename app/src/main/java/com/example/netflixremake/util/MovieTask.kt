package com.example.netflixremake.util

import android.os.Handler
import android.os.Looper
import com.example.netflixremake.model.Movie
import com.example.netflixremake.model.MovieDetail
import org.json.JSONObject
import java.io.BufferedInputStream
import java.io.ByteArrayOutputStream
import java.io.IOException
import java.io.InputStream
import java.net.URL
import java.util.concurrent.Executors
import javax.net.ssl.HttpsURLConnection

class MovieTask(private val callback: Callback) {

    interface Callback {
        fun onPreExecute()
        fun onFailure(message: String)
        fun onResult(details: MovieDetail)
    }

    fun execute(url: String){
        callback.onPreExecute()

        val executor = Executors.newSingleThreadExecutor()
        val handler = Handler(Looper.getMainLooper())

        executor.execute{
            var urlConnection: HttpsURLConnection? = null
            var stream: InputStream? = null
            var buffer: BufferedInputStream? = null

            try {
                val requestUrl = URL(url)
                urlConnection = requestUrl.openConnection() as HttpsURLConnection
                urlConnection.readTimeout = 2000
                urlConnection.connectTimeout = 2000

                val statusCode = urlConnection.responseCode
                if(statusCode == 400){
                    stream = urlConnection.errorStream
                    buffer = BufferedInputStream(stream)

                    val toString = toStrings(buffer)

                    val json = JSONObject(toString)
                    val message = json.getString("message")

                    throw IOException(message)
                } else if(statusCode > 400){
                    throw IOException("Erro no servidor!")
                }

                stream = urlConnection.inputStream
                buffer = BufferedInputStream(stream)

                val jsonAsString = toStrings(buffer)
                val movieDetails = toDetails(jsonAsString)

                handler.post {
                    callback.onResult(movieDetails)
                }


            }catch (e: IOException){
                val message = e.message ?: "Erro desconhecido!"

                handler.post {
                    callback.onFailure(message)
                }
            }finally {
                urlConnection?.disconnect()
                buffer?.close()
                stream?.close()
            }
        }
    }

    private fun toDetails(toAsString: String) : MovieDetail{
        val json = JSONObject(toAsString)

        val id = json.getInt("id")
        val title = json.getString("title")
        val desc = json.getString("desc")
        val cast = json.getString("cast")
        val coverUrl = json.getString("cover_url")
        val jsonMovies = json.getJSONArray("movie")

        val similar = mutableListOf<Movie>()

        for(i in 0 until jsonMovies.length()){
            val jsonMovie = jsonMovies.getJSONObject(i)

            val similarId = jsonMovie.getInt("id")
            val similarCoverUrl = jsonMovie.getString("cover_url")

            val m = Movie(similarId, similarCoverUrl)
            similar.add(m)
        }

        val movie = Movie(id,coverUrl, title, desc, cast)

        return MovieDetail(movie, similar)
    }
    private fun toStrings(stream: InputStream) : String {
        val bytes = ByteArray(1024)
        var read: Int
        val baos = ByteArrayOutputStream()
        while (true){
            read = stream.read(bytes)
            if(read <= 0){
                break
            }
            baos.write(bytes,0, read)
        }

        return String(baos.toByteArray())
    }
}
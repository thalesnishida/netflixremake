package com.example.netflixremake.util

import android.graphics.Bitmap
import android.graphics.BitmapFactory
import android.os.Handler
import android.os.Looper
import android.util.Log
import java.io.IOException
import java.io.InputStream
import java.net.URL
import java.util.concurrent.Executors
import javax.net.ssl.HttpsURLConnection

class DownloadImageTask(private val callback: Callback) {

    interface Callback {
        fun onResult(bitmap: Bitmap)
    }

     fun execute(url: String){
         val executor = Executors.newSingleThreadExecutor()
         val handler = Handler(Looper.getMainLooper())

         executor.execute{
             var urlConnection: HttpsURLConnection? = null
             var stream: InputStream? = null

             try {
                 val requestUrl = URL(url)
                 urlConnection = requestUrl.openConnection() as HttpsURLConnection
                 urlConnection.readTimeout = 2000
                 urlConnection.connectTimeout = 2000


                 val statusCode = urlConnection.responseCode

                 if(statusCode > 400) {
                     throw IOException("Erro no servidor!")
                 }

                 stream = urlConnection.inputStream
                 val bitmap = BitmapFactory.decodeStream(stream)

                 handler.post {
                    callback.onResult(bitmap)
                 }


             }catch (e: IOException){
                 val message = e.message ?: "Erro Desconhecido"
                 Log.i("Erro", message, e)
             }finally {
                 urlConnection?.disconnect()
                 stream?.close()
             }

         }

     }

}

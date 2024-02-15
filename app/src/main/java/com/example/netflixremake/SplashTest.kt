package com.example.netflixremake

import android.annotation.SuppressLint
import android.content.Intent
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.widget.ImageView
import androidx.constraintlayout.widget.ConstraintLayout

@Suppress("DEPRECATION")
class SplashTest : AppCompatActivity() {
    @SuppressLint("MissingInflatedId")
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_splash_test)

        val iv: ImageView = findViewById(R.id.iv_note)
        iv.alpha = 0f
        iv.animate().setDuration(1500).alpha(1f).withEndAction(){
            val i = Intent(this, MovieActivity::class.java)
            startActivity(i)
            overridePendingTransition(android.R.anim.fade_in,android.R.anim.fade_out)
            finish()
        }

    }
}
package com.example.shopnest

import android.content.Intent
import android.os.Bundle
import android.os.Handler
import android.view.WindowManager.LayoutParams.*
import androidx.appcompat.app.AppCompatActivity
@Suppress("DEPRECATION")
class splashactivity : AppCompatActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_splashactivity)
        window.setFlags(
            FLAG_FULLSCREEN,
            FLAG_FULLSCREEN


        )

        Handler().postDelayed({
            startActivity(Intent(this@splashactivity,Loginactivity::class.java))
            finish()




        },2500
        )
    }
}

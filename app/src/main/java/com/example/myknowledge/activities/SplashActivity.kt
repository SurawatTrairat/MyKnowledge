package com.example.myknowledge.activities

import android.content.Intent
import android.graphics.Typeface
import android.os.Build
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.os.Handler
import android.os.Looper
import android.view.Window
import android.view.WindowManager
import com.example.myknowledge.R
import com.example.myknowledge.firebase.FireStoreClass
import kotlinx.android.synthetic.main.activity_splash.*

class SplashActivity : AppCompatActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_splash)
        val typeface: Typeface = Typeface.createFromAsset(assets, "carbon bl.ttf")
        tv_app_name_below.typeface = typeface
        Handler(Looper.getMainLooper()).postDelayed({
            val currentUserID = FireStoreClass().getCurrentUserID()
            if(currentUserID.isNotEmpty()){
                startActivity(Intent(this@SplashActivity, MainActivity::class.java))
            }
            else{
                startActivity(Intent(this@SplashActivity, IntroActivity::class.java))
            }
        }, 2000)
    }
}
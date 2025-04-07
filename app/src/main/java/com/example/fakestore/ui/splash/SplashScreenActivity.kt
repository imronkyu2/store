package com.example.fakestore.ui.splash

import android.content.Intent
import android.os.Bundle
import androidx.appcompat.app.AppCompatActivity
import com.example.fakestore.R
import com.example.fakestore.data.local.TokenManager
import com.example.fakestore.ui.home.MainActivity
import com.example.fakestore.ui.login.LoginActivity
import com.google.gson.Gson

class SplashScreenActivity : AppCompatActivity() {

    private lateinit var tokenManager: TokenManager

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_splash_screen)

        tokenManager = TokenManager(applicationContext, Gson())

        Thread {
            Thread.sleep(4000)
            runOnUiThread {
                val token = tokenManager.getToken()
                if (token != null) {
                    startActivity(Intent(this, MainActivity::class.java))
                } else {
                    startActivity(Intent(this, LoginActivity::class.java))
                }
                finish()
            }
        }.start()
    }
}

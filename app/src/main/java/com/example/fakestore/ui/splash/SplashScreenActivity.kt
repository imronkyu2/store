package com.example.fakestore.ui.splash

import android.content.Intent
import android.os.Bundle
import androidx.appcompat.app.AppCompatActivity
import com.example.fakestore.R
import com.example.fakestore.data.local.TokenManager
import com.example.fakestore.ui.login.LoginActivity
import com.example.fakestore.ui.home.MainActivity

class SplashScreenActivity : AppCompatActivity() {

    private lateinit var tokenManager: TokenManager

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_splash_screen)

        tokenManager = TokenManager(applicationContext)

        // Delay to show splash screen and then check token
        Thread {
            Thread.sleep(2000) // Splash screen delay
            runOnUiThread {
                val token = tokenManager.getToken()
                if (token != null) {
                    // If token exists, navigate to MainActivity
                    startActivity(Intent(this, MainActivity::class.java))
                } else {
                    // If no token, navigate to LoginActivity
                    startActivity(Intent(this, LoginActivity::class.java))
                }
                finish() // Finish SplashScreenActivity so it doesn't stay in the back stack
            }
        }.start()
    }
}

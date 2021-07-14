package com.example.pomodorotimer

import android.content.Intent
import android.os.Build
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.os.Handler
import android.os.Looper
import android.view.WindowInsets
import android.view.WindowManager
import android.view.animation.Animation
import android.view.animation.AnimationUtils
import com.example.pomodorotimer.databinding.ActivitySplashScreenBinding

class SplashScreen : AppCompatActivity() {

    //Variables
    private lateinit var binding: ActivitySplashScreenBinding
    private val endAnim: Animation by lazy {AnimationUtils.loadAnimation(this, R.anim.end_animation)}
    private val bottomAnim: Animation by lazy {AnimationUtils.loadAnimation(this, R.anim.bottom_animation)}

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivitySplashScreenBinding.inflate(layoutInflater)
        setContentView(binding.root)
        setFullScreen()

        //Hooks
        binding.apply {
            logoApplication.animation = endAnim
            nameApplication.animation = bottomAnim
        }

        //TransitionToMainScreen
        Handler(Looper.getMainLooper()).postDelayed(Runnable {
                val intent = Intent(this, MainActivity::class.java)
                startActivity(intent)
                finish()
            }, SPLASH_SCREEN.toLong())

    }


    private fun setFullScreen() {
        @Suppress("DEPRECATION")
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.R) {
            window.insetsController?.hide(WindowInsets.Type.statusBars())
        } else {
            window.setFlags(
                WindowManager.LayoutParams.FLAG_FULLSCREEN,
                WindowManager.LayoutParams.FLAG_FULLSCREEN
            )
        }
    }

    companion object{
        private val SPLASH_SCREEN = 3000
    }
}
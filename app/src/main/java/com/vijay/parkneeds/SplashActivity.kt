package com.vijay.parkneeds

import android.content.Intent
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.os.Handler
import android.view.animation.AnimationUtils
import com.google.gson.Gson
import com.google.gson.reflect.TypeToken
import com.vijay.parkneeds.Models.UserModel
import com.vijay.parkneeds.databinding.ActivitySplashBinding
import java.lang.reflect.Type

class SplashActivity : AppCompatActivity() {

    // View Binding
    private lateinit var binding: ActivitySplashBinding

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivitySplashBinding.inflate(layoutInflater)
        setContentView(binding.root)

        val alphaAnim = AnimationUtils.loadAnimation(this, R.anim.alpha_animation)
        binding.llSplash.startAnimation(alphaAnim)

        val sp = getSharedPreferences("my_sp", MODE_PRIVATE)
        val gson = Gson()

        Handler().postDelayed({
            if (sp.contains("user_model")) {
                val json = sp?.getString("user_model", null)
                val type: Type = object : TypeToken<UserModel>() {}.type
                val userModel = gson.fromJson<Any>(json, type) as UserModel

                if (userModel.userType == "Park Spot Finder") {
                    startActivity(Intent(this, FinderHomeActivity::class.java))
                } else {
                    startActivity(Intent(this, OwnerHomeActivity::class.java))
                }
            } else {
                startActivity(Intent(this, MainActivity::class.java))
            }
            finish()
        }, 3000)
    }
}
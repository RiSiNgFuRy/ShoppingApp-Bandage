package com.example.shopping_app_bandage.Activities

import android.app.Activity
import android.content.Intent
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.util.Log
import android.view.animation.AnimationUtils
import android.widget.TextView
import androidx.lifecycle.lifecycleScope
import com.example.shopping_app_bandage.Helpers.SharedPrefs
import com.example.shopping_app_bandage.Models.LoginTokenVerificationModel
import com.example.shopping_app_bandage.R
import com.example.shopping_app_bandage.Services.RetrofitClient
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.withContext
import java.util.concurrent.Executors
import java.util.concurrent.TimeUnit

class MainActivity : AppCompatActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)

        val appLogo = findViewById<TextView>(R.id.splashLogo)

        appLogo.startAnimation(AnimationUtils.loadAnimation(this, R.anim.zoom_in_rotating))
        Executors.newSingleThreadScheduledExecutor().schedule({
            lifecycleScope.launchWhenCreated {
                checkUserAlreadyExists(this@MainActivity)
                finish()
            }
        }, 2500, TimeUnit.MILLISECONDS)
    }
}

suspend fun checkUserAlreadyExists(activity: Activity){
    try{
        val token = SharedPrefs(activity, activity.getString(R.string.preference_file_key)).getToken()
        if(token != null) {
            val tokenModel = LoginTokenVerificationModel(token)
            val response = RetrofitClient.buildService().verifyToken(tokenModel)
            if (response.isSuccessful && response.body()?.status == "Success") {
                Cart.setCartItems(SharedPrefs(activity, activity.getString(R.string.preference_file_key)).getUserId()!!)
                activity.startActivity(Intent(activity, MainList::class.java))
            }
            else
                activity.startActivity(Intent(activity, LoginAndSignUpActivity::class.java))
        }
        else
            activity.startActivity(Intent(activity, LoginAndSignUpActivity::class.java))

    }
    catch (e: Exception){
//        Log.d("ErrorFetchingToken", e.localizedMessage)
    }
}


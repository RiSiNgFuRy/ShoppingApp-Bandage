package com.example.shopping_app_bandage.Activities

import android.content.Context
import android.content.Intent
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.util.Log
import com.example.shopping_app_bandage.Fragments.LoginFragment
import com.example.shopping_app_bandage.R

class LoginAndSignUpActivity : AppCompatActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_login_and_sign_up)
        supportFragmentManager
            .beginTransaction()
            .replace(R.id.loginAndSignUpFrame, LoginFragment())
            .commit()
    }
}
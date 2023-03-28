package com.example.shopping_app_bandage.Fragments

import android.app.Activity
import android.content.Context
import android.content.Intent
import android.os.Bundle
import android.util.Log
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.EditText
import android.widget.TextView
import android.widget.Toast
import androidx.fragment.app.FragmentActivity
import com.example.shopping_app_bandage.Activities.Cart
import com.example.shopping_app_bandage.Activities.CartActivity
import com.example.shopping_app_bandage.Activities.MainList
import com.example.shopping_app_bandage.Helpers.SharedPrefs
import com.example.shopping_app_bandage.Models.UserLoginModel
import com.example.shopping_app_bandage.R
import com.example.shopping_app_bandage.Services.RetrofitClient
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext

class LoginFragment : Fragment() {



    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        val v = inflater.inflate(R.layout.fragment_login, container, false)
        val loginEmail = v.findViewById<EditText>(R.id.loginEmail)
        val loginPassword = v.findViewById<EditText>(R.id.loginPassword)
        val loginBtn = v.findViewById<TextView>(R.id.loginBtn)
        val loginToRegisterBtn = v.findViewById<TextView>(R.id.loginToRegisterBtn)

        loginBtn?.setOnClickListener {
           try {
               saveCredentials(activity, loginEmail.text.toString(), loginPassword.text.toString())
           }
           catch (e: Exception) {
               Toast.makeText(context, e.localizedMessage, Toast.LENGTH_SHORT).show()
           }
        }

        loginToRegisterBtn.setOnClickListener {
            activity?.supportFragmentManager
                ?.beginTransaction()
                ?.replace(R.id.loginAndSignUpFrame, SignUpFragment())
                ?.addToBackStack(null)
                ?.commit()
        }
        return v
    }

    fun saveCredentials(activity: FragmentActivity?, email: String, password: String){
        val sharedPref = activity?.getSharedPreferences(getString(R.string.preference_file_key),Context.MODE_PRIVATE)
        val loginUserCredentials = UserLoginModel(email, password)
        CoroutineScope(Dispatchers.IO).launch {
            val response = RetrofitClient.buildService().verifyLoginCredentials(loginUserCredentials)
            if(response.isSuccessful){
                Cart.setCartItems(response.body()?.userId.toString())
                sharedPref?.edit()
                    ?.putString("userId", response.body()?.userId.toString())
                    ?.putString("userName", response.body()?.username.toString())
                    ?.putString("token", response.body()?.token)
                    ?.putString("userEmail", response.body()?.email)
                    ?.commit()
                withContext(Dispatchers.Main) {
                    startActivity(Intent(context, MainList::class.java))
                    activity?.finish()
                }
            }
            else
                CoroutineScope(Dispatchers.Main).launch {
                    Toast.makeText(context, "User Not Found", Toast.LENGTH_SHORT).show()
                }
        }
    }
}

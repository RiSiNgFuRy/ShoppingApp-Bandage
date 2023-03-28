package com.example.shopping_app_bandage.Fragments

import android.content.Context
import android.os.Bundle
import android.util.Patterns
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.EditText
import android.widget.TextView
import android.widget.Toast
import androidx.core.text.toHtml
import androidx.lifecycle.lifecycleScope
import com.example.shopping_app_bandage.Models.UserRegisterModel
import com.example.shopping_app_bandage.R
import com.example.shopping_app_bandage.Services.RetrofitClient

class SignUpFragment : Fragment() {


    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

    }

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        val v = inflater.inflate(R.layout.fragment_sign_up, container, false)
        val registerEmail = v.findViewById<EditText>(R.id.registerEmail)
        val registerUsername = v.findViewById<EditText>(R.id.registerUsername)
        val registerPassword = v.findViewById<EditText>(R.id.registerPassword)
        val cnfRegisterPassword = v.findViewById<EditText>(R.id.cnfRegisterPassword)
        val registerBtn = v.findViewById<TextView>(R.id.registerBtn)
        val registerToLoginBtn = v.findViewById<TextView>(R.id.registerToLoginBtn)


        registerBtn.setOnClickListener {
            if(registerEmail.text.isEmpty()||registerUsername.text.isEmpty()||registerPassword.text.isEmpty()
                ||cnfRegisterPassword.text.isEmpty()){
                Toast.makeText(context,"Fields cannot be empty", Toast.LENGTH_SHORT)
            }
            else if(!Patterns.EMAIL_ADDRESS.matcher(registerEmail.text).matches()){
                registerEmail.error = "Invalid Email"
            }
            else if(cnfRegisterPassword.text.equals(registerPassword.text)){
                registerPassword.text.clear()
                cnfRegisterPassword.text.clear()
                Toast.makeText(context, "Password doesn't match", Toast.LENGTH_SHORT)
            }
            else {
                lifecycleScope.launchWhenCreated {
                    try {
                        registerUser(registerEmail.text.toString(), registerUsername.text.toString(), registerPassword.text.toString())
                    }
                    catch (e: Exception) {
                        Toast.makeText(context, e.localizedMessage, Toast.LENGTH_SHORT).show()
                    }
                }
            }
        }

        registerToLoginBtn.setOnClickListener {
            activity?.supportFragmentManager
                ?.beginTransaction()
                ?.replace(R.id.loginAndSignUpFrame, LoginFragment())
                ?.commit()
        }

        return v
    }

    private suspend fun registerUser(userEmail: String, userName: String, userPassword: String) {
        val registerModel = UserRegisterModel(userEmail, userName, userPassword)
        val response = RetrofitClient.buildService().registerUser(registerModel)
        if(response.isSuccessful){
            Toast.makeText(context, "User Registered", Toast.LENGTH_SHORT).show()
            activity?.supportFragmentManager
                ?.beginTransaction()
                ?.replace(R.id.loginAndSignUpFrame, LoginFragment())
                ?.commit()
        }
        else {
            Toast.makeText(context, "User Not Registered, Retry", Toast.LENGTH_SHORT).show()
        }
    }

}


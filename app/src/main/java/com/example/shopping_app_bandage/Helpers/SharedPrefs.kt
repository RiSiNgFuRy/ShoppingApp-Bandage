package com.example.shopping_app_bandage.Helpers

import android.content.Context

class SharedPrefs(var context: Context, var key: String) {
    private val sharedPrefs = context.getSharedPreferences(key, Context.MODE_PRIVATE)

    fun getUserName(): String? {
        return sharedPrefs.getString("userName", null)
    }

    fun getUserId(): String? {
        return sharedPrefs.getString("userId", null)
    }

    fun getUserEmail(): String? {
        return sharedPrefs.getString("userEmail", null)
    }

    fun getToken(): String? {
        return sharedPrefs.getString("token", null)
    }

    fun clearSharedPrefs() {
        sharedPrefs
            .edit()
            .clear()
            .commit()
    }
}
package com.example.shopping_app_bandage.Activities

import android.content.Intent
import android.os.Bundle
import android.view.View
import android.widget.TextView
import androidx.activity.result.contract.ActivityResultContracts
import androidx.appcompat.app.AppCompatActivity
import com.example.shopping_app_bandage.Helpers.SharedPrefs
import com.example.shopping_app_bandage.R

class ProfileActivity : AppCompatActivity() {

    private var deletedItems = arrayListOf<String>()

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_profile)

        val profileUserName = findViewById<TextView>(R.id.profileUserName)
        val profileUserEmail = findViewById<TextView>(R.id.profileUserEmail)
        val profileLogOutBtn = findViewById<TextView>(R.id.profileLogoutBtn)
        val profileBackBtn = findViewById<View>(R.id.profileBackBtn)
        val profileTotalCartItems = findViewById<TextView>(R.id.profileTotalCartItems)
        val profileCartBtn = findViewById<View>(R.id.profileCartBtn)

        val activityResultLauncher = registerForActivityResult(ActivityResultContracts.StartActivityForResult()){ result ->
            if(result.resultCode==1 && result.data!=null){
                checkCart(profileTotalCartItems)
                deletedItems = result.data!!.getStringArrayListExtra(getString(R.string.deletedItemList))!!
            }
        }


        profileUserName.text = SharedPrefs(this, getString(R.string.preference_file_key)).getUserName()
        profileUserEmail.text = SharedPrefs(this, getString(R.string.preference_file_key)).getUserEmail()

        profileBackBtn.setOnClickListener {
            onBackPressed()
        }

        profileLogOutBtn.setOnClickListener {
            SharedPrefs(this, getString(R.string.preference_file_key)).clearSharedPrefs()
            val i = Intent(this, LoginAndSignUpActivity::class.java)
            i.flags = Intent.FLAG_ACTIVITY_CLEAR_TASK or Intent.FLAG_ACTIVITY_NEW_TASK
            startActivity(i)
        }

        profileCartBtn.setOnClickListener {
            activityResultLauncher.launch(Intent(this, CartActivity::class.java))
        }

        checkCart(profileTotalCartItems)
    }

    override fun onBackPressed() {
        val i = Intent(this, MainList::class.java)
        i.putExtra(getString(R.string.deletedItemList), deletedItems)
        setResult(1, i)
        startActivity(i)
        finish()
    }
}


private fun checkCart(v: TextView){
    val cartSize = Cart.cartTotalQuantity()
    if(cartSize>0){
        v.visibility = View.VISIBLE
        v.text = cartSize.toString()
    }else{
        v.visibility = View.GONE
    }
}
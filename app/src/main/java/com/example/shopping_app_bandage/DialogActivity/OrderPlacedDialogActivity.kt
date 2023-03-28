package com.example.shopping_app_bandage.DialogActivity

import android.content.Intent
import android.graphics.Color
import android.graphics.drawable.ColorDrawable
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.util.Log
import android.view.Gravity
import android.view.View
import android.view.Window
import android.widget.TextView
import androidx.lifecycle.lifecycleScope
import com.example.shopping_app_bandage.Activities.Cart
import com.example.shopping_app_bandage.Activities.CartActivity
import com.example.shopping_app_bandage.Helpers.SharedPrefs
import com.example.shopping_app_bandage.Models.OrderDetailsSettingRequestModel
import com.example.shopping_app_bandage.Models.Products
import com.example.shopping_app_bandage.R
import com.example.shopping_app_bandage.Services.RetrofitClient
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.withContext

class OrderPlacedDialogActivity : AppCompatActivity() {

    private lateinit var orderTransactionNumber: TextView

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        this.supportRequestWindowFeature(Window.FEATURE_NO_TITLE)
        setContentView(R.layout.activity_order_placed_dialog)

        this.setFinishOnTouchOutside(false)
        this.window.setBackgroundDrawable(ColorDrawable(Color.TRANSPARENT))
        this.window.setGravity(Gravity.CENTER)

        orderTransactionNumber = findViewById<TextView>(R.id.orderTransactionNumber)
        val orderBackToCartBtn = findViewById<TextView>(R.id.orderBackToCartBtn)
        val orderBackToCartBtnFailed = findViewById<TextView>(R.id.orderBackToCartBtnFailed)
        val loadingDialog = findViewById<View>(R.id.loadingDialog)
        val successDialogBox = findViewById<View>(R.id.successDialog)
        val failedDialogBox = findViewById<View>(R.id.failedDialog)


        orderTransactionNumber.text = ""
        lifecycleScope.launchWhenCreated {
            sendOrderDetails(orderTransactionNumber, loadingDialog, successDialogBox, failedDialogBox)
        }

        orderBackToCartBtn.setOnClickListener {
            onBackPressed()
        }

        orderBackToCartBtnFailed.setOnClickListener {
            onBackPressed()
        }
    }

    override fun onBackPressed() {
        val i = Intent(this, CartActivity::class.java)
        i.putExtra(getString(R.string.isOrderPlaced), orderTransactionNumber.text.isNotEmpty())
        setResult(2, i)
        this.finish()
    }

    private suspend fun sendOrderDetails(orderNumber: TextView, loadingDialog: View, successDialog: View, failedDialog: View){
        try{
            val userId = SharedPrefs(this, getString(R.string.preference_file_key)).getUserId()
            var orderDetailModel = arrayListOf<Products>()
            for (items in Cart.getCartList())
                orderDetailModel.add(Products(items.product.id, items.quantity!!))
            val response = RetrofitClient.buildService().setOrderDetails(userId!!, OrderDetailsSettingRequestModel(orderDetailModel))
            if(response.isSuccessful){
                withContext(Dispatchers.Main){
                    loadingDialog.visibility = View.GONE
                    orderNumber.text = response.body()?.transactionNumber
                    successDialog.visibility = View.VISIBLE
                }
            }
        }catch (e: Exception){
           withContext(Dispatchers.Main){
               loadingDialog.visibility = View.GONE
               failedDialog.visibility = View.VISIBLE
           }
        }
    }
}
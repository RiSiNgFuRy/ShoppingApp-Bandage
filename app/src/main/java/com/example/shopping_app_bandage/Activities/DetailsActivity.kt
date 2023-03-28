package com.example.shopping_app_bandage.Activities

import android.content.Intent
import android.content.SharedPreferences
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.util.Log
import android.view.View
import android.widget.ImageView
import android.widget.TextView
import android.widget.Toast
import androidx.activity.result.ActivityResultLauncher
import androidx.activity.result.contract.ActivityResultContracts
import androidx.lifecycle.lifecycleScope
import com.bumptech.glide.Glide
import com.example.shopping_app_bandage.Helpers.SharedPrefs
import com.example.shopping_app_bandage.Models.ProductModel
import com.example.shopping_app_bandage.R
import com.example.shopping_app_bandage.Services.RetrofitClient
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext

class DetailsActivity : AppCompatActivity() {


    private lateinit var activityResultLauncher: ActivityResultLauncher<Intent>
    private var pId: String = ""
    private var deletedItemsList = arrayListOf<String>()
    private var product: ProductModel? = null

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_details)


        val detailAddToCartBtn = findViewById<TextView>(R.id.detailAddToCartBtn)
        val detailTotalCartItems = findViewById<TextView>(R.id.detailTotalCartItems)

        val productId = intent.getStringExtra("productId")

        activityResultLauncher = registerForActivityResult(ActivityResultContracts.StartActivityForResult()){ result ->
            if (result.resultCode == 1 && result.data != null ){
                checkCart(detailTotalCartItems)
                deletedItemsList = result.data!!.getStringArrayListExtra(getString(R.string.deletedItemList))!!
                if(deletedItemsList.contains(productId)) {
                    checkItemInCart(detailAddToCartBtn, product!!, detailTotalCartItems)
                }
            }
        }

        checkCart(detailTotalCartItems)

        lifecycleScope.launchWhenCreated {
            fetchProduct(productId!!)
        }

    }

    override fun onBackPressed() {
        val i1 = Intent(this, MainList::class.java)
        i1.putStringArrayListExtra(getString(R.string.deletedItemList), deletedItemsList)
        i1.putExtra(getString(R.string.productId), pId)
        Log.d("detailListofDeleted", i1.extras.toString())
        setResult(1,i1)
        finish()
    }

    private fun initializeValues(product: ProductModel){
        val detailItemImg = findViewById<ImageView>(R.id.detailItemImg)
        val detailItemName = findViewById<TextView>(R.id.detailItemName)
        val detailItemPrice = findViewById<TextView>(R.id.detailItemPrice)
        val detailItemInfo = findViewById<TextView>(R.id.detailItemInfo)
        val detailCartBtn = findViewById<ImageView>(R.id.detailCartBtn)
        val detailBackBtn = findViewById<ImageView>(R.id.detailBackBtn)
        val detailAddToCartBtn = findViewById<TextView>(R.id.detailAddToCartBtn)
        val detailBeforeImg = findViewById<TextView>(R.id.detailImgBefore)
        val detailAfterImg = findViewById<TextView>(R.id.detailImgAfter)
        val detailTotalCartItems = findViewById<TextView>(R.id.detailTotalCartItems)

        Log.d("ProductID", product.id.toString())

        detailItemName.text = product.name
        detailItemPrice.text = "$ " + product.sellingPrice.toString()
        detailItemInfo.text = product.details


        if (product.imageUrl.size <= 1) {
            detailBeforeImg.visibility = View.GONE
            detailAfterImg.visibility = View.GONE
        }

        glideImg(detailItemImg, product.imageUrl[0].toString())
        glideImgArray(detailItemImg, product.imageUrl, detailBeforeImg, detailAfterImg)


        detailBackBtn.setOnClickListener {
            onBackPressed()
        }

        checkItemInCart(detailAddToCartBtn, product, detailTotalCartItems)

        detailCartBtn.setOnClickListener {
            activityResultLauncher.launch(Intent(this, CartActivity::class.java))
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

    private suspend fun fetchProduct(productId: String){
        try {
            val response = RetrofitClient.buildService().getProductById(productId)
            if(response.isSuccessful){
                product = response.body()!![0]
                initializeValues(product!!)
            }
            else{
                Toast.makeText(this, "Not able to fetch product", Toast.LENGTH_SHORT).show()
            }
        }catch (e: Exception){
            Log.e("ErrorFetchingProduct", e.localizedMessage)
        }
    }

    private fun checkItemInCart(v: TextView, p: ProductModel, totalItemsInCart: TextView){
        if(Cart.findElementInCart(p))
            convertGoToCartBtn(v, activityResultLauncher, totalItemsInCart)
        else {
            convertAddToCartBtn(v, p, activityResultLauncher, totalItemsInCart)
        }
    }

    private fun convertGoToCartBtn(v: TextView, activityResultLauncher: ActivityResultLauncher<Intent>, totalItemsInCart: TextView){
        v.text = resources.getString(R.string.goToCart)
        v.setOnClickListener {
            activityResultLauncher.launch(Intent(this, CartActivity::class.java))
        }
    }

    private fun convertAddToCartBtn(v: TextView, p: ProductModel, activityResultLauncher: ActivityResultLauncher<Intent>, totalItemsInCart: TextView){
        v.text = resources.getString(R.string.addToCart)
        v.setOnClickListener {
            pId = p.id
            CoroutineScope(Dispatchers.IO).launch {
                Cart.addItemToCart(
                    p,
                    SharedPrefs(
                        this@DetailsActivity,
                        getString(R.string.preference_file_key)
                    ).getUserId()!!
                )
                withContext(Dispatchers.Main){
                    checkCart(totalItemsInCart)
                }
            }
            convertGoToCartBtn(v, activityResultLauncher, totalItemsInCart)
        }
    }

    private fun glideImgArray(imgView: ImageView, imgArrayList: ArrayList<String>, beforeBtn: TextView, afterBtn: TextView){
        var idxOfImg =0
        beforeBtn.visibility = View.GONE

        beforeBtn.setOnClickListener {
            if(idxOfImg>0) {
                idxOfImg--
                glideImg(imgView, imgArrayList[idxOfImg])
            }
            if(idxOfImg==0)
                beforeBtn.visibility = View.GONE
            if(idxOfImg<imgArrayList.size-1)
                afterBtn.visibility = View.VISIBLE
        }

        afterBtn.setOnClickListener {
            if(idxOfImg<imgArrayList.size-1){
                idxOfImg++
                glideImg(imgView, imgArrayList[idxOfImg])
            }
            if(idxOfImg==imgArrayList.size-1)
                afterBtn.visibility = View.GONE
            if(idxOfImg>0)
                beforeBtn.visibility = View.VISIBLE
        }
    }

    private fun glideImg(imgView: ImageView, imgUrl: String){
        Glide.with(this)
            .load(imgUrl)
            .placeholder(R.drawable.ic_launcher_foreground)
            .centerCrop()
            .into(imgView)
    }
}
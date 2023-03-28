package com.example.shopping_app_bandage.Activities

import android.content.Context
import android.content.Intent
import android.content.res.ColorStateList
import android.graphics.Color
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.util.Log
import android.view.*
import android.widget.*
import androidx.activity.result.ActivityResultLauncher
import androidx.activity.result.contract.ActivityResultContracts
import androidx.lifecycle.lifecycleScope
import androidx.recyclerview.widget.GridLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.bumptech.glide.Glide
import com.example.shopping_app_bandage.Helpers.SharedPrefs
import com.example.shopping_app_bandage.Models.ProductModel
import com.example.shopping_app_bandage.R
import com.example.shopping_app_bandage.Services.RetrofitClient
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext
import kotlin.random.Random


class MainList : AppCompatActivity() {

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main_list)

        val mainCartBtn = findViewById<ImageView>(R.id.mainCartBtn)
        val mainList = findViewById<RecyclerView>(R.id.mainList)
        val mainListProgressBar = findViewById<ProgressBar>(R.id.mainListProgressBar)
        val mainListProfileBtn = findViewById<View>(R.id.mainListProfileBtn)
        val mainTotalCartItems = findViewById<TextView>(R.id.mainTotalCartItems)

        val activityResultLauncher = registerForActivityResult(ActivityResultContracts.StartActivityForResult()){ result ->
            if (result.resultCode == 1 && result.data != null ){
                checkCart(mainTotalCartItems)
                val itemIds = result.data!!.getStringArrayListExtra(getString(R.string.deletedItemList))
                for(p in itemIds!!) {
                    mainList!!.adapter!!.notifyItemChanged(ProductList.getPositionById(p))
                }
                val itemId = result.data!!.getStringExtra(getString(R.string.productId))
                if(itemId?.length != 0)
                    mainList!!.adapter!!.notifyItemChanged(ProductList.getPositionById(itemId))
            }
        }

        checkCart(mainTotalCartItems)

        lifecycleScope.launchWhenCreated {
            getMainProductList(mainList, activityResultLauncher, mainListProgressBar, mainTotalCartItems)
        }

        mainCartBtn.setOnClickListener{
            activityResultLauncher!!.launch(Intent(this, CartActivity::class.java))
        }

        mainListProfileBtn.setOnClickListener {
            startActivity(Intent(this, ProfileActivity::class.java))
        }
    }

    private suspend fun getMainProductList(mainList: RecyclerView, activityResultLauncher: ActivityResultLauncher<Intent>, progressBar: ProgressBar, totalItemsInCart: TextView) {
        try{
            val response = RetrofitClient.buildService().getAllProducts()
            if (response.isSuccessful){
                progressBar.visibility = View.GONE
                var list: ArrayList<ProductModel>? = response.body()
                ProductList.addAllToList(list!!)
                mainList.layoutManager = GridLayoutManager(this, 2)
                mainList.adapter = ProductAdapter(this, list, activityResultLauncher, totalItemsInCart)
            }
        }catch (e: Exception){
            Toast.makeText(this, e.localizedMessage, Toast.LENGTH_SHORT).show()
        }
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


class ProductAdapter(val context: Context, val prodList: ArrayList<ProductModel>, val activityResultLauncher: ActivityResultLauncher<Intent>,val totalItemsInCart: TextView) :
        RecyclerView.Adapter<ProductAdapter.ProductViewHolder>() {

            class ProductViewHolder(itemView: View) : RecyclerView.ViewHolder(itemView) {
                val itemImg = itemView.findViewById<ImageView>(R.id.itemImg)
                val itemName = itemView.findViewById<TextView>(R.id.itemName)
                val itemType = itemView.findViewById<TextView>(R.id.itemType)
                val itemOrignalPrice = itemView.findViewById<TextView>(R.id.itemOrignalPrice)
                val itemPrice = itemView.findViewById<TextView>(R.id.itemPrice)
                val mainItemCard = itemView.findViewById<View>(R.id.mainItemCard)
                val addToCartBtn = itemView.findViewById<TextView>(R.id.addToCartBtn)
                val itemColorPallet = itemView.findViewById<LinearLayout>(R.id.itemColorPallet)
            }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ProductViewHolder {
        val view = LayoutInflater.from(context).inflate(R.layout.card_layout_main, parent, false)
        return ProductViewHolder(view)
    }

    override fun getItemCount(): Int {
        return prodList.size
    }

    override fun onBindViewHolder(holder: ProductViewHolder, position: Int) {
        val listModel = prodList.get(position)
        holder.itemName.text = listModel.name
        holder.itemType.text = listModel.brand
        holder.itemOrignalPrice.text = "$ "+listModel.costPrice.toString()
        holder.itemPrice.text = "$ "+listModel.sellingPrice.toString()
        Glide.with(context).load(listModel.imageUrl?.get(0)).placeholder(R.drawable.ic_launcher_foreground).fitCenter().into(holder.itemImg)

        if(Cart.findElementInCart(listModel))
            convertGoToCartBtn(holder.addToCartBtn)
        else
            convertAddToCartBtn(holder.addToCartBtn, listModel)

        holder.mainItemCard.setOnClickListener {
            val i = Intent(context, DetailsActivity::class.java)
            i.putExtra("productId", listModel.id)
            activityResultLauncher.launch(i)
        }

//        holder.itemColorPallet.apply {
//            val n = Random.nextInt(0,4)
//            for(i in 0..n)
//                addView(makeColoredCircle(Random.nextInt(1,255),Random.nextInt(1,255),Random.nextInt(1,255)))
//        }
//
    }

    fun makeColoredCircle(r: Int, g: Int, b: Int): ImageView{
        return ImageView(context).apply {
            background = resources.getDrawable(R.drawable.circular_color)
            backgroundTintList = ColorStateList.valueOf(Color.rgb(r,g,b))
            adjustViewBounds = true
            clipToOutline = true
            layoutParams = LinearLayout.LayoutParams(
                50,
                50)
        }
    }

    private fun convertGoToCartBtn(v: TextView){
        v.text = context.resources.getString(R.string.goToCart)
        v.setTextColor(Color.WHITE)
        v.setOnClickListener {
            activityResultLauncher!!.launch(Intent(context, CartActivity::class.java))
        }
    }

    private fun convertAddToCartBtn(v: TextView, p: ProductModel){
        v.text = context.getString(R.string.addToCart)
        v.setOnClickListener {
            CoroutineScope(Dispatchers.IO).launch {
                Cart.addItemToCart(
                    p,
                    SharedPrefs(context,context.getString(R.string.preference_file_key)).getUserId()!!)
                withContext(Dispatchers.Main){
                    checkCart(totalItemsInCart)
                }
            }
            convertGoToCartBtn(v)
        }
    }
}

object ProductList {
    private val list = arrayListOf<ProductModel>()

    fun addProduct(p: ProductModel){
        list.add(p)
    }

    fun addAllToList(productList: ArrayList<ProductModel>){
        list.addAll(productList)
    }

    fun getList(): ArrayList<ProductModel> {
        return list
    }

    fun getPositionById(i: String?) : Int{
        for(idx in 0 until list.size) {
            if (list[idx].id == i) {
                return idx
            }
        }
        return -1
    }
}

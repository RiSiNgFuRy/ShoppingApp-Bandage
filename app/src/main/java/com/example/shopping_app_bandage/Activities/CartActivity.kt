package com.example.shopping_app_bandage.Activities

import android.content.Context
import android.content.Intent
import android.graphics.Color
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.util.Log
import android.view.Gravity
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.*
import androidx.activity.result.contract.ActivityResultContracts
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.bumptech.glide.Glide
import com.example.shopping_app_bandage.DialogActivity.OrderPlacedDialogActivity
import com.example.shopping_app_bandage.Helpers.SharedPrefs
import com.example.shopping_app_bandage.Models.*
import com.example.shopping_app_bandage.R
import com.example.shopping_app_bandage.Services.RetrofitClient
import kotlinx.coroutines.*


private var deletedItems = arrayListOf<String>()
private lateinit var cartList: RecyclerView

class CartActivity : AppCompatActivity() {

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_cart)

        cartList = findViewById<RecyclerView>(R.id.cartList)
        val cartBackBtn = findViewById<ImageView>(R.id.cartBackBtn)
        totalValue = findViewById<TextView>(R.id.totalValue)
        qtyValue = findViewById<TextView>(R.id.qtyValue)
        cartOrderSummary = findViewById<View>(R.id.cartOrderSummary)
        cartEmptyMsg = findViewById<View>(R.id.cartEmptyMsg)
        val emptyCartImg = findViewById<ImageView>(R.id.emptyCartImg)
        val placeOrderBtn = findViewById<TextView>(R.id.placeOrderBtn)


        cartEmpty();

        deletedItems.clear()

        cartList.layoutManager = LinearLayoutManager(this)
        cartList.adapter = CartAdapter(this, Cart.getCartList())

        val activityResultLauncher = registerForActivityResult(ActivityResultContracts.StartActivityForResult()){ result ->
            if (result.resultCode == 2 && result.data?.getBooleanExtra(getString(R.string.isOrderPlaced), false) == true){
                for(item in Cart.getCartList())
                    deletedItems.add(item.product.id)
                Cart.clearCart()
                cartList!!.adapter!!.notifyDataSetChanged()
                cartEmpty()
            }
        }

        placeOrderBtn.setOnClickListener {
            activityResultLauncher.launch(Intent(this, OrderPlacedDialogActivity::class.java))
        }

        cartBackBtn.setOnClickListener{
            onBackPressed()
        }

        Glide
            .with(this)
            .asGif()
            .load("https://media.tenor.com/8klrp86awEYAAAAC/cart.gif")
            .into(emptyCartImg)

    }

    companion object {
        private lateinit var totalValue: TextView
        private lateinit var qtyValue: TextView
        private lateinit var cartOrderSummary: View
        private lateinit var cartEmptyMsg: View

        fun setTotalValue(p: Double){
            totalValue.text = String.format("%.2f", p)
        }

        fun setQtyValue(p: Double){
            qtyValue.text = String.format("%.2f", p)
        }

        fun cartEmpty(){
            if(Cart.isEmpty()) {
                cartOrderSummary.visibility = View.GONE
                cartEmptyMsg.visibility = View.VISIBLE
            }
        }
    }

    override fun onBackPressed() {
        val i = Intent()
        i.putStringArrayListExtra(getString(R.string.deletedItemList), deletedItems)
        setResult(1, i)
        finish()
    }
}

class CartAdapter(private val context: Context, private val list: ArrayList<CartItems>) :
        RecyclerView.Adapter<CartAdapter.ViewHolder>(){

    private var pos = 0
    class ViewHolder(itemView: View) : RecyclerView.ViewHolder(itemView){
        val cartItemImg = itemView.findViewById<ImageView>(R.id.cartItemImg)
        val cartItemName = itemView.findViewById<TextView>(R.id.cartItemName)
        val cartItemType = itemView.findViewById<TextView>(R.id.cartItemType)
        val cartItemPrice = itemView.findViewById<TextView>(R.id.cartItemPrice)
        val cartDeleteBtn = itemView.findViewById<TextView>(R.id.cartDeleteBtn)
        val cartQty = itemView.findViewById<Spinner>(R.id.cartQty)
        val cartCard = itemView.findViewById<View>(R.id.cartCard)
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ViewHolder {
        val view = LayoutInflater.from(context).inflate(R.layout.card_layout_cart, parent, false)
        return ViewHolder(view)
    }

    override fun getItemCount(): Int {
        return list.size
    }

    override fun onBindViewHolder(holder: ViewHolder, position: Int) {
        val listModel = list.get(position)
        holder.cartItemName.text = listModel.product.name
        holder.cartItemType.text = listModel.product.brand
        holder.cartItemPrice.text = "$ " + setTotalSellingPrice(listModel)
        val userId = SharedPrefs(context, context.getString(R.string.preference_file_key)).getUserId()

        Glide
            .with(context)
            .load(listModel.product.imageUrl[0])
            .centerCrop()
            .placeholder(R.drawable.ic_launcher_foreground)
            .into(holder.cartItemImg)

        holder.cartDeleteBtn.setOnClickListener {
            deletedItems.add(listModel.product.id)
            holder.cartDeleteBtn.isClickable = false
            GlobalScope.launch {
                Cart.deleteCartItem(list, userId!!, listModel.product.id, position)
                withContext(Dispatchers.Main){
                    holder.cartDeleteBtn.isClickable = true
                    list.removeAt(position)
                    notifyItemRemoved(position)
                    CartActivity.cartEmpty()
                }
            }
        }

        ArrayAdapter.createFromResource(context, R.array.qtyOfItems, android.R.layout.simple_spinner_item)
            .also { adapter ->
                adapter.setDropDownViewResource(android.R.layout.simple_spinner_item)
                holder.cartQty.adapter = adapter
                holder.cartQty.setSelection(adapter.getPosition(listModel.quantity.toString()))
            }

        holder.cartQty.onItemSelectedListener = object : AdapterView.OnItemSelectedListener{
            override fun onItemSelected(parent: AdapterView<*>?, view: View?, position: Int, id: Long) {
                var selectedQuantity = Integer.parseInt(parent?.getItemAtPosition(position).toString())
                val userId = SharedPrefs(context, context.getString(R.string.preference_file_key)).getUserId()
                CoroutineScope(Dispatchers.IO).launch {
                    changeProductQuantity(listModel.product.id, userId!!, selectedQuantity)
                    withContext(Dispatchers.Main) {
                        CartActivity.setTotalValue(Cart.getTotalAmount())
                        CartActivity.setQtyValue(Cart.getTotalAmount())
                    }
                }
            }

            override fun onNothingSelected(p0: AdapterView<*>?) {
                TODO("Not yet implemented")
            }
        }
    }
}

private suspend fun changeProductQuantity(productId: String, userId: String, selectedQuantity: Int) {
    try{
        val changeQtyModel = ItemChangedQuantityCartRequestModel(productId, selectedQuantity)
        val response = RetrofitClient.buildService().changeQuantityOfCartItem(userId, changeQtyModel)
        if(response.isSuccessful){
            Cart.setCartItems(userId)
        }
    }catch (e: Exception){
        Log.e("ErrorChangingQty", e.localizedMessage)
    }
}

private fun setTotalSellingPrice(listModel: CartItems): String{
    var price = listModel.product.sellingPrice!!
    return String.format("%.2f", price)
}

object Cart{
    private val cartItems= arrayListOf<CartItems>()
    private var totalAmount = 0.0

    suspend fun addItemToCart(product: ProductModel, userId: String){
        try {
            val reqModel = ItemAddToCartRequestModel(product.id!!)
            val response = RetrofitClient.buildService().addToCart(userId, reqModel)
            if (response.isSuccessful && response.body()?.status == "Success") {
                cartItems.add(CartItems(product, 1))
            }
        }catch (e: Exception){
            Log.e("ErrorAddingInCart", e.localizedMessage)
        }
    }

    suspend fun setCartItems(userId: String){
        try{
            val response = RetrofitClient.buildService().getUserCartItems(userId)
            if(response.isSuccessful){
                val cartItemsList: ArrayList<CartItems> = response.body()?.cartItems!!
                totalAmount = response.body()?.totalAmount!!
                cartItems.clear()
                cartItems.addAll(cartItemsList)
            }
        }catch (e: Exception){
            Log.e("ErrorFetchingCart", e.localizedMessage)
        }
    }

    suspend fun deleteCartItem(list: ArrayList<CartItems>, userId: String, productId: String, position: Int) {
        try {
            Log.d("deleteCartItemCheck", "InDispatcherIO")
            val response = RetrofitClient.buildService().deleteItemFromCart(userId, productId)
            if(response.isSuccessful && response.body()?.status == "Success"){
                deletedItems.add(productId)
            }
        }catch (e: Exception){
            Log.e("ErrorDeletingItem", e.localizedMessage)
        }
    }

    fun findElementInCart(c: ProductModel): Boolean{
        for(items in cartItems)
            if(items.product==c)
                return true
        return false
    }

    fun cartTotalQuantity(): Int{
        var  t =0
        for(items in cartItems){
            t += items.quantity!!
        }
        return t
    }

    fun getCartList(): ArrayList<CartItems>{
        return cartItems
    }

    fun getTotalAmount(): Double{
        return totalAmount
    }

    fun isEmpty(): Boolean{
        return cartItems.size==0;
    }

    fun clearCart(){
        cartItems.clear()
    }
}

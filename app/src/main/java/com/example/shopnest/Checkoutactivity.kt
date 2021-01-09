package com.example.shopnest

import Cartitemlistadapter
import android.content.Intent
import android.os.Bundle
import android.view.View
import android.widget.Toast
import androidx.recyclerview.widget.LinearLayoutManager
import kotlinx.android.synthetic.main.activity_checkoutactivity.*

class Checkoutactivity : baseactivityt() {
    private var mAddressDetails: Address? = null
    private lateinit var mProductsList: ArrayList<Product>
    private var mSubTotal: Double = 0.0
    private lateinit var mCartItemsList: ArrayList<Cart>

    // A global variable for the Total Amount.
    private var mTotalAmount: Double = 0.0
    // END

    // TODO Step 12: Global variable for cart items list.
    // START

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_checkoutactivity)
        setupActionBar()
        if (intent.hasExtra(Constants.EXTRA_SELECTED_ADDRESS)) {
            mAddressDetails =
                intent.getParcelableExtra<Address>(Constants.EXTRA_SELECTED_ADDRESS)!!
        }
        if (mAddressDetails != null) {
            tv_checkout_address_type.text = mAddressDetails?.type
            tv_checkout_full_name.text = mAddressDetails?.name
            tv_checkout_address.text = "${mAddressDetails!!.address}, ${mAddressDetails!!.zipCode}"
            tv_checkout_additional_note.text = mAddressDetails?.additionalNote

            if (mAddressDetails?.otherDetails!!.isNotEmpty()) {
                tv_checkout_other_details.text = mAddressDetails?.otherDetails
            }
            tv_checkout_mobile_number.text = mAddressDetails?.mobileNumber
        }
        btn_place_order.setOnClickListener {
            placeAnOrder()
        }
          getProductList()

    }
    private fun setupActionBar() {

        setSupportActionBar(toolbar_checkout_activity)

        val actionBar = supportActionBar
        if (actionBar != null) {
            actionBar.setDisplayHomeAsUpEnabled(true)
            actionBar.setHomeAsUpIndicator(R.drawable.ic_baseline_arrow_back_ios_24)
        }

        toolbar_checkout_activity.setNavigationOnClickListener { onBackPressed() }
    }
    private fun getProductList() {

        // Show the progress dialog.
        showprogressdialog(resources.getString(R.string.pleasewait))

        Firestoreclass().getAllProductsList(this@Checkoutactivity)
    }
    fun successProductsListFromFireStore(productsList: ArrayList<Product>) {

        // TODO Step 8: Initialize the global variable of all product list.
        // START
        mProductsList = productsList
        // END

        // TODO Step 10: Call the function to get the latest cart items.
        // START
        getCartItemsList()
        // END
    }
    private fun getCartItemsList() {

        Firestoreclass().getCartList(this@Checkoutactivity)
    }
    fun successCartItemsList(cartList: ArrayList<Cart>) {

        // Hide progress dialog.
        hideProgressDialog()

        // TODO Step 13: Initialize the cart list.
        // START

        for (product in mProductsList) {
            for (cart in cartList) {
                if (product.product_id == cart.product_id) {
                    cart.stock_quantity = product.stock_quantity
                }
            }
        }
        // END

        mCartItemsList = cartList

        // TODO Step 2: Populate the cart items in the UI.
        // START
        rv_cart_list_items.layoutManager = LinearLayoutManager(this@Checkoutactivity)
        rv_cart_list_items.setHasFixedSize(true)

        // TODO Step 5: Pass the required param.
        val cartListAdapter =Cartitemlistadapter(this@Checkoutactivity, mCartItemsList, false)
        rv_cart_list_items.adapter = cartListAdapter
        // END

        // TODO Step 9: Calculate the subtotal and Total Amount.
        // START
        var subTotal: Double = 0.0

        for (item in mCartItemsList) {

            val availableQuantity = item.stock_quantity.toInt()

            if (availableQuantity > 0) {
                val price = item.price.toDouble()
                val quantity = item.cart_quantity.toInt()

                subTotal += (price * quantity)
            }
        }

        tv_checkout_sub_total.text = "$$mSubTotal"
        // Here we have kept Shipping Charge is fixed as $10 but in your case it may cary. Also, it depends on the location and total amount.
        tv_checkout_shipping_charge.text = "$10.0"

        if (subTotal > 0) {
            ll_checkout_place_order.visibility = View.VISIBLE

            mTotalAmount = mSubTotal + 10.0
            tv_checkout_total_amount.text = "$$mTotalAmount"
        } else {
            ll_checkout_place_order.visibility = View.GONE
        }
        // END
    }
    private fun placeAnOrder() {

        // Show the progress dialog.
        showprogressdialog(resources.getString(R.string.pleasewait))

        // TODO Step 5: Now prepare the order details based on all the required details.
        // START
        val order = Orders(
                Firestoreclass().getCurrentUserID(),
                mCartItemsList,
                mAddressDetails!!,
                "My order ${System.currentTimeMillis()}",
                mCartItemsList[0].image,
                mSubTotal.toString(),
                "10.0", // The Shipping Charge is fixed as $10 for now in our case.
                mTotalAmount.toString()
        )
        // END

        // TODO Step 10: Call the function to place the order in the cloud firestore.
        // START
        Firestoreclass().placeOrder(this@Checkoutactivity, order)
        // END
    }
    fun orderPlacedSuccess() {

        // Hide the progress dialog.

        Firestoreclass().updateAllDetails(this@Checkoutactivity, mCartItemsList)

    }
    fun allDetailsUpdatedSuccessfully() {

        // TODO Step 6: Move the piece of code from OrderPlaceSuccess to here.
        // START
        // Hide the progress dialog.
        hideProgressDialog()

        Toast.makeText(this@Checkoutactivity, "Your order placed successfully.", Toast.LENGTH_SHORT)
                .show()

        val intent = Intent(this@Checkoutactivity, DashboardActivity::class.java)
        intent.flags = Intent.FLAG_ACTIVITY_NEW_TASK or Intent.FLAG_ACTIVITY_CLEAR_TASK
        startActivity(intent)
        finish()
        // END
    }

    // E
}
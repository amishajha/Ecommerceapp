package com.example.shopnest


import android.content.Intent
import android.os.Bundle
import android.util.Log
import android.view.View
import android.widget.Toast
import androidx.core.content.ContextCompat
import com.example.shopnest.R.layout.product_details
import kotlinx.android.synthetic.main.product_details.*

class ProductDetailsActivity:baseactivityt(), View.OnClickListener {
    private var mProductId: String = ""
    private lateinit var mProductDetails: Product

    /**
     * This function is auto created by Android when the Activity Class is created.
     */
    override fun onCreate(savedInstanceState: Bundle?) {
        //This call the parent constructor
        super.onCreate(savedInstanceState)
        // This is used to align the xml view to this class
        setContentView(product_details)

        if (intent.hasExtra(Constants.EXTRA_PRODUCT_ID)) {
            mProductId =
                    intent.getStringExtra(Constants.EXTRA_PRODUCT_ID)!!
            Log.i("Product Id", mProductId)
        }
        var productOwnerId: String = ""

        if (intent.hasExtra(Constants.EXTRA_PRODUCT_OWNER_ID)) {
            productOwnerId =
                    intent.getStringExtra(Constants.EXTRA_PRODUCT_OWNER_ID)!!
        }
        setupActionBar()
        if (Firestoreclass().getCurrentUserID() == productOwnerId) {
            btn_add_to_cart.visibility = View.GONE
            btn_go_to_cart.visibility = View.GONE
        } else {

            btn_add_to_cart.visibility = View.VISIBLE
        }
        btn_add_to_cart.setOnClickListener(this)
        btn_go_to_cart.setOnClickListener(this)
        getProductDetails()
    }

    /**
     * A function for actionBar Setup.
     */
    private fun setupActionBar() {

        setSupportActionBar(toolbar_product_details_activity)

        val actionBar = supportActionBar
        if (actionBar != null) {
            actionBar.setDisplayHomeAsUpEnabled(true)
            actionBar.setHomeAsUpIndicator(R.drawable.ic_baseline_arrow_back_ios_24)
        }

        toolbar_product_details_activity.setNavigationOnClickListener { onBackPressed() }
    }

    /**
     * A function to call the firestore class function that will get the product details from cloud firestore based on the product id.
     */
    private fun getProductDetails() {

        // Show the product dialog
        showprogressdialog(resources.getString(R.string.pleasewait))

        // Call the function of FirestoreClass to get the product details.
        Firestoreclass().getProductDetails(this@ProductDetailsActivity, mProductId)
    }

    /**
     * A function to notify the success result of the product details based on the product id.
     *
     * @param product A model class with product details.
     */
    fun productDetailsSuccess(product: Product) {
        mProductDetails = product
        // Hide Progress dialog.
        hideProgressDialog()

        // Populate the product details in the UI.
        GlideLoader(this@ProductDetailsActivity).loadProductPicture(
                product.image,
                iv_product_detail_image
        )

        tv_product_details_title.text = product.title
        tv_product_details_price.text = "$${product.price}"
        tv_product_details_description.text = product.description
        tv_product_details_stock_quantity.text = product.stock_quantity
        // TODO Step 8: Update the UI if the stock quantity is 0.
        // START
        if(product.stock_quantity.toInt() == 0){

            // Hide Progress dialog.
            hideProgressDialog()

            // Hide the AddToCart button if the item is already in the cart.
            btn_add_to_cart.visibility = View.GONE

            tv_product_details_stock_quantity.text =
                    resources.getString(R.string.lbl_out_of_stock)

            tv_product_details_stock_quantity.setTextColor(
                    ContextCompat.getColor(
                            this@ProductDetailsActivity,
                            R.color.colorSnackBarError
                    )
            )
        }else{

            // There is no need to check the cart list if the product owner himself is seeing the product details.
            if (Firestoreclass().getCurrentUserID() == product.user_id) {
                // Hide Progress dialog.
                hideProgressDialog()
            } else {
                Firestoreclass().checkIfItemExistInCart(this@ProductDetailsActivity, mProductId)
            }
        }
    }

    override fun onClick(v: View?) {
        // START
        if (v != null) {
            when (v.id) {

                R.id.btn_add_to_cart -> {
                 addToCart()
                }
                R.id.btn_go_to_cart->{
                    startActivity(Intent(this@ProductDetailsActivity, Cartlistactivity::class.java))
                }
            }
        }
    }

    private fun addToCart() {

        val addToCart = Cart(
                Firestoreclass().getCurrentUserID(),
                mProductId,
                mProductDetails.title,
                mProductDetails.price,
                mProductDetails.image,
                Constants.DEFAULT_CART_QUANTITY
        )
      showprogressdialog(resources.getString(R.string.pleasewait))

        Firestoreclass().addCartItems(this@ProductDetailsActivity, addToCart)
    }
    fun productExistsInCart() {

        // Hide the progress dialog.
        hideProgressDialog()

        // Hide the AddToCart button if the item is already in the cart.
        btn_add_to_cart.visibility = View.GONE
        // Show the GoToCart button if the item is already in the cart. User can update the quantity from the cart list screen if he wants.
        btn_go_to_cart.visibility = View.VISIBLE
    }
    fun addToCartSuccess() {
        // Hide the progress dialog.
        hideProgressDialog()

        Toast.makeText(
                this@ProductDetailsActivity,
                resources.getString(R.string.success_message_item_added_to_cart),
                Toast.LENGTH_SHORT
        ).show()
        btn_add_to_cart.visibility = View.GONE
        // Show the GoToCart button if the item is already in the cart. User can update the quantity from the cart list screen if he wants.
        btn_go_to_cart.visibility = View.VISIBLE
    }
}
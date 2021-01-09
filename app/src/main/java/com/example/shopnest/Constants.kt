package com.example.shopnest

import android.app.Activity
import android.content.Intent
import android.net.Uri
import android.provider.MediaStore
import android.webkit.MimeTypeMap

object Constants {

    // Firebase Constants
    // This  is used for the collection name for USERS.
    const val USERS: String = "users"
    const val PRODUCTS: String = "products"
    const val SHOPNEST_PREFERENCES:String ="myshopnestprefs"
    const val LOGGED_IN_USERNAME:String="logged_in_username"
    const val EXTRA_USER_DETAILS: String = "extra_user_details"
    const val READ_STORAGE_PERMISSION_CODE = 2
    const val MALE: String = "Male"
    const val FEMALE: String = "Female"
    const val ORDERS: String = "orders"
    const val USER_ID: String = "user_id"
    const val PRODUCT_ID: String = "product_id"
    const val MOBILE: String = "mobile"
    const val IMAGE: String = "image"
    const val CART_ITEMS: String = "cart_items"
    const val DEFAULT_CART_QUANTITY: String = "1"
    const val EXTRA_PRODUCT_OWNER_ID: String = "extra_product_owner_id"

    const val EXTRA_PRODUCT_ID: String = "extra_product_id"

    const val COMPLETE_PROFILE:String="profileCompleted"
    const val GENDER: String = "gender"
    const val USER_PROFILE_IMAGE:String = "User_Profile_Image"
    const val PICK_IMAGE_REQUEST_CODE = 2
    const val PRODUCT_IMAGE: String = "Product_Image"
    const val CART_QUANTITY: String = "cart_quantity"
    const val HOME: String = "Home"
    const val OFFICE: String = "Office"
    const val OTHER: String = "Other"
    const val ADDRESSES: String = "addresses"
    const val EXTRA_ADDRESS_DETAILS: String = "AddressDetails"
    const val EXTRA_SELECT_ADDRESS: String = "extra_select_address"
    const val EXTRA_SELECTED_ADDRESS: String = "extra_selected_address"
    const val STOCK_QUANTITY: String = "stock_quantity"
      const val ADD_ADDRESS_REQUEST_CODE: Int = 121


    fun showImageChooser(activity: Activity) {
        // An intent for launching the image selection of phone storage.
        val galleryIntent = Intent(
            Intent.ACTION_PICK,
            MediaStore.Images.Media.EXTERNAL_CONTENT_URI
        )
        // Launches the image selection of phone storage using the constant code.
        activity.startActivityForResult(galleryIntent, PICK_IMAGE_REQUEST_CODE)
    }
    fun getFileExtension(activity:Activity,uri: Uri?):String ?{


        return MimeTypeMap.getSingleton().getExtensionFromMimeType(activity.contentResolver.getType(uri!!))
    }
}

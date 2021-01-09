package com.example.shopnest



import android.app.Activity
import android.content.Context
import android.content.SharedPreferences
import android.net.Uri
import android.util.Log
import androidx.fragment.app.Fragment
import com.example.shopnest.ui.dashboard.DashboardFragment
import com.example.shopnest.ui.home.ProductFragment
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.firestore.FirebaseFirestore
import com.google.firebase.firestore.SetOptions
import com.google.firebase.storage.FirebaseStorage
import com.google.firebase.storage.StorageReference

class Firestoreclass {
   private val mFireStore = FirebaseFirestore.getInstance()

    // TODO Step 7: Create a function to access the Cloud Firestore and create a collection.
    fun registerUser(activity: Registeractivity, userInfo: User) {

        // TODO Step 3: Replace the hard coded string with constant value which is added in the Constants object.
        // The "users" is collection name. If the collection is already created then it will not create the same one again.
        mFireStore.collection(Constants.USERS)
                // Document ID for users fields. Here the document it is the User ID.
                .document(userInfo.id)
                // Here the userInfo are Field and the SetOption is set to merge. It is for if we wants to merge later on instead of replacing the fields.
                .set(userInfo, SetOptions.merge())
                .addOnSuccessListener {

                    // Here call a function of base activity for transferring the result to it.
                    activity.userRegistrationSuccess()
                }
                .addOnFailureListener { e ->
                    activity.hideProgressDialog()
                    Log.e(
                            activity.javaClass.simpleName,
                            "Error while registering the user.",
                            e
                    )
                }
    }

    // TODO Step 1: Create a function to get the user id of the current logged in user.
    // START
    /**
     * A function to get the user id of current logged user.
     */
    fun getCurrentUserID(): String {
        // An Instance of currentUser using FirebaseAuth
        val currentUser = FirebaseAuth.getInstance().currentUser

        // A variable to assign the currentUserId if it is not null or else it will be blank.
        var currentUserID = ""
        if (currentUser != null) {
            currentUserID = currentUser.uid
        }

        return currentUserID
    }
    // END

    // TODO Step 4: Create a function to get the logged user details from Cloud Firestore.
    // START
    /**
     * A function to get the logged user details from from FireStore Database.
     */
    fun getUserDetails(activity: Activity) {

        // Here we pass the collection name from which we wants the data.
        mFireStore.collection(Constants.USERS)
                // The document id to get the Fields of user.
                .document(getCurrentUserID())
                .get()
                .addOnSuccessListener { document ->

                    Log.i(activity.javaClass.simpleName, document.toString())

                    // Here we have received the document snapshot which is converted into the User Data model object.
                    val user = document.toObject(User::class.java)!!

                    val sharedPreferences = activity.getSharedPreferences(
                            Constants.SHOPNEST_PREFERENCES,
                            Context.MODE_PRIVATE
                    )
                    val editor: SharedPreferences.Editor = sharedPreferences.edit()
                    editor.putString(
                            Constants.LOGGED_IN_USERNAME, "${user.firstName} ${user.lastName}"
                    )
                    editor.apply()
                    // TODO Step 6: Pass the result to the Login Activity.
                    // START
                    when (activity) {
                        is Loginactivity -> {
                            // Call a function of base activity for transferring the result to it.
                            activity.userLoggedInSuccess(user)
                        }
                        is SettingActivity -> {
                            activity.userDetailsSuccess(user)
                        }
                    }
                    // END
                }
                .addOnFailureListener { e ->
                    // Hide the progress dialog if there is any error. And print the error in log.
                    when (activity) {
                        is Loginactivity -> {
                            activity.hideProgressDialog()
                        }
                        is SettingActivity -> {
                            activity.hideProgressDialog()
                        }
                    }

                    Log.e(
                            activity.javaClass.simpleName,
                            "Error while getting user details.",
                            e
                    )
                }


    }

    fun updateUserProfileData(activity: Activity, userHashMap: HashMap<String, Any>) {
        // Collection Name
        mFireStore.collection(Constants.USERS)
                // Document ID against which the data to be updated. Here the document id is the current logged in user id.
                .document(getCurrentUserID())
                // A HashMap of fields which are to be updated.
                .update(userHashMap)
                .addOnSuccessListener {

                    // TODO Step 9: Notify the success result to the base activity.
                    // START
                    // Notify the success result.
                    when (activity) {
                        is Userprofileactivity -> {
                            // Call a function of base activity for transferring the result to it.
                            activity.userProfileUpdateSuccess()
                        }
                    }
                    // END
                }
                .addOnFailureListener { e ->

                    when (activity) {
                        is Userprofileactivity -> {
                            // Hide the progress dialog if there is any error. And print the error in log.
                            activity.hideProgressDialog()
                        }
                    }

                    Log.e(
                            activity.javaClass.simpleName,
                            "Error while updating the user details.",
                            e
                    )
                }


    }

    fun uploadImageToCloudStorage(activity: Activity, imageFileURI: Uri?, imageType: String) {

        //getting the storage reference
        val sRef: StorageReference = FirebaseStorage.getInstance().reference.child(
                Constants.USER_PROFILE_IMAGE + System.currentTimeMillis() + "."
                        + Constants.getFileExtension(
                        activity,
                        imageFileURI
                )
        )

        //adding the file to reference
        sRef.putFile(imageFileURI!!)
                .addOnSuccessListener { taskSnapshot ->
                    // The image upload is success
                    Log.e(
                            "Firebase Image URL",
                            taskSnapshot.metadata!!.reference!!.downloadUrl.toString()
                    )

                    // Get the downloadable url from the task snapshot
                    taskSnapshot.metadata!!.reference!!.downloadUrl
                            .addOnSuccessListener { uri ->
                                Log.e("Downloadable Image URL", uri.toString())

                                // TODO Step 8: Pass the success result to base class.
                                // START
                                // Here call a function of base activity for transferring the result to it.
                                when (activity) {
                                    is Userprofileactivity -> {
                                        activity.imageUploadSuccess(uri.toString())
                                    }
                                    is Addproductactivity -> {
                                        activity.imageUploadSuccess(uri.toString())
                                    }
                                }
                                // END
                            }
                }
                .addOnFailureListener { exception ->

                    // Hide the progress dialog if there is any error. And print the error in log.
                    when (activity) {
                        is Userprofileactivity -> {
                            activity.hideProgressDialog()
                        }
                        is Addproductactivity -> {
                            activity.hideProgressDialog()
                        }
                    }

                    Log.e(
                            activity.javaClass.simpleName,
                            exception.message,
                            exception
                    )
                }
    }   // TODO Step 6: Create a function to upload the image to the Cloud Stora

    fun uploadProductDetails(activity: Addproductactivity, productInfo: Product) {

        mFireStore.collection(Constants.PRODUCTS)
                .document()
                // Here the userInfo are Field and the SetOption is set to merge. It is for if we wants to merge
                .set(productInfo, SetOptions.merge())
                .addOnSuccessListener {

                    // Here call a function of base activity for transferring the result to it.
                    activity.productUploadSuccess()
                }
                .addOnFailureListener { e ->

                    activity.hideProgressDialog()

                    Log.e(
                            activity.javaClass.simpleName,
                            "Error while uploading the product details.",
                            e
                    )
                }
    }

    fun getProductsList(fragment: Fragment) {
        // The collection name for PRODUCTS
        mFireStore.collection(Constants.PRODUCTS)
                .whereEqualTo(Constants.USER_ID, getCurrentUserID())
                .get() // Will get the documents snapshots.
                .addOnSuccessListener { document ->

                    // Here we get the list of boards in the form of documents.
                    Log.e("Products List", document.documents.toString())

                    // Here we have created a new instance for Products ArrayList.
                    val productsList: ArrayList<Product> = ArrayList()

                    // A for loop as per the list of documents to convert them into Products ArrayList.
                    for (i in document.documents) {

                        val product = i.toObject(Product::class.java)
                        product!!.product_id = i.id

                        productsList.add(product)
                    }

                    when (fragment) {
                        is ProductFragment -> {
                            fragment.successProductsListFromFireStore(productsList)
                        }
                    }
                }
                .addOnFailureListener { e ->
                    // Hide the progress dialog if there is any error based on the base class instance.
                    when (fragment) {
                        is ProductFragment -> {
                            fragment.hideProgressDialog()
                        }
                    }
                    Log.e("Get Product List", "Error while getting product list.", e)
                }
    }

    /**
     * A function to get the dashboard items list. The list will be an overall items list, not based on the user's id.
     */
    fun getDashboardItemsList(fragment: DashboardFragment) {
        // The collection name for PRODUCTS
        mFireStore.collection(Constants.PRODUCTS)
                .get() // Will get the documents snapshots.
                .addOnSuccessListener { document ->

                    // Here we get the list of boards in the form of documents.
                    Log.e(fragment.javaClass.simpleName, document.documents.toString())

                    // Here we have created a new instance for Products ArrayList.
                    val productsList: ArrayList<Product> = ArrayList()

                    // A for loop as per the list of documents to convert them into Products ArrayList.
                    for (i in document.documents) {

                        val product = i.toObject(Product::class.java)!!
                        product.product_id = i.id
                        productsList.add(product)
                    }

                    // Pass the success result to the base fragment.
                    fragment.successDashboardItemsList(productsList)
                }
                .addOnFailureListener { e ->
                    // Hide the progress dialog if there is any error which getting the dashboard items list.
                    fragment.hideProgressDialog()
                    Log.e(fragment.javaClass.simpleName, "Error while getting dashboard items list.", e)
                }
    }
    fun getAllProductsList(activity: Activity) {
        // The collection name for PRODUCTS
        mFireStore.collection(Constants.PRODUCTS)
                .get() // Will get the documents snapshots.
                .addOnSuccessListener { document ->

                    // Here we get the list of boards in the form of documents.
                    Log.e("Products List", document.documents.toString())

                    // Here we have created a new instance for Products ArrayList.
                    val productsList: ArrayList<Product> = ArrayList()

                    // A for loop as per the list of documents to convert them into Products ArrayList.
                    for (i in document.documents) {

                        val product = i.toObject(Product::class.java)
                        product!!.product_id = i.id

                        productsList.add(product)
                    }

                    // TODO Step 3: Pass the success result of the product list to the cart list activity.
                    // START
                    when (activity) {
                        is Cartlistactivity -> {
                            activity.successProductsListFromFireStore(productsList)
                        }

                        // TODO Step 5: Notify the success result to the base class.
                        // START
                        is Checkoutactivity -> {
                            activity.successProductsListFromFireStore(productsList)
                        }
                        // END
                    }
                    // END
                }
                .addOnFailureListener { e ->
                    // Hide the progress dialog if there is any error based on the base class instance.

                    when (activity) {
                        is Cartlistactivity -> {
                            activity.hideProgressDialog()
                        }

                        // TODO Step 6: Hide the progress dialog.
                        is Checkoutactivity -> {
                            activity.hideProgressDialog()
                        }
                    }

                    Log.e("Get Product List", "Error while getting all product list.", e)


                }
    }

    // TODO Step 1: Create a function to delete the product from the cloud firestore.
    /**
     * A function to delete the product from the cloud firestore.
     */
    fun deleteProduct(fragment: ProductFragment, productId: String) {

        mFireStore.collection(Constants.PRODUCTS)
                .document(productId)
                .delete()
                .addOnSuccessListener {

                    // TODO Step 4: Notify the success result to the base class.
                    // START
                    // Notify the success result to the base class.
                    fragment.productDeleteSuccess()
                    // END
                }
                .addOnFailureListener { e ->

                    // Hide the progress dialog if there is an error.
                    fragment.hideProgressDialog()

                    Log.e(
                            fragment.requireActivity().javaClass.simpleName,
                            "Error while deleting the product.",
                            e
                    )
                }
    }


    // END
    fun getProductDetails(activity: ProductDetailsActivity, productId: String) {

        // The collection name for PRODUCTS
        mFireStore.collection(Constants.PRODUCTS)
                .document(productId)
                .get() // Will get the document snapshots.
                .addOnSuccessListener { document ->

                    // Here we get the product details in the form of document.
                    Log.e(activity.javaClass.simpleName, document.toString())

                    // Convert the snapshot to the object of Product data model class.
                    val product = document.toObject(Product::class.java)!!

                    activity.productDetailsSuccess(product)
                }
                .addOnFailureListener { e ->

                    // Hide the progress dialog if there is an error.
                    activity.hideProgressDialog()

                    Log.e(activity.javaClass.simpleName, "Error while getting the product details.", e)
                }
    }

    fun addCartItems(activity: ProductDetailsActivity, addToCart: Cart) {

        mFireStore.collection(Constants.CART_ITEMS)
                .document()
                // Here the userInfo are Field and the SetOption is set to merge. It is for if we wants to merge
                .set(addToCart, SetOptions.merge())
                .addOnSuccessListener {

                    // Here call a function of base activity for transferring the result to it.
                    activity.addToCartSuccess()
                }
                .addOnFailureListener { e ->

                    activity.hideProgressDialog()

                    Log.e(
                            activity.javaClass.simpleName,
                            "Error while creating the document for cart item.",
                            e
                    )
                }
    }

    fun checkIfItemExistInCart(activity: ProductDetailsActivity, productId: String) {

        mFireStore.collection(Constants.CART_ITEMS)
                .whereEqualTo(Constants.USER_ID, getCurrentUserID())
                .whereEqualTo(Constants.PRODUCT_ID, productId)
                .get()
                .addOnSuccessListener { document ->

                    Log.e(activity.javaClass.simpleName, document.documents.toString())

                    // TODO Step 8: Notify the success result to the base class.
                    // START
                    // If the document size is greater than 1 it means the product is already added to the cart.
                    if (document.documents.size > 0) {
                        activity.productExistsInCart()
                    } else {
                        activity.hideProgressDialog()
                    }
                    // END
                }
                .addOnFailureListener { e ->
                    // Hide the progress dialog if there is an error.
                    activity.hideProgressDialog()

                    Log.e(
                            activity.javaClass.simpleName,
                            "Error while checking the existing cart list.",
                            e
                    )
                }
    }

    fun getCartList(activity: Activity) {
        // The collection name for PRODUCTS
        mFireStore.collection(Constants.CART_ITEMS)
                .whereEqualTo(Constants.USER_ID, getCurrentUserID())
                .get() // Will get the documents snapshots.
                .addOnSuccessListener { document ->

                    // Here we get the list of cart items in the form of documents.
                    Log.e(activity.javaClass.simpleName, document.documents.toString())

                    // Here we have created a new instance for Cart Items ArrayList.
                    val list: ArrayList<Cart> = ArrayList()

                    // A for loop as per the list of documents to convert them into Cart Items ArrayList.
                    for (i in document.documents) {

                        val cartItem = i.toObject(Cart::class.java)!!
                        cartItem.id = i.id

                        list.add(cartItem)
                    }

                    when (activity) {
                        is Cartlistactivity -> {

                            activity.successCartItemsList(list)

                        }
                        is Checkoutactivity -> {
                            activity.successCartItemsList(list)
                        }
                    }
                }
                .addOnFailureListener { e ->
                    // Hide the progress dialog if there is an error based on the activity instance.
                    when (activity) {
                        is Cartlistactivity -> {
                            activity.hideProgressDialog()
                        }

                        is Checkoutactivity -> {
                            activity.hideProgressDialog()
                        }
                    }

                    Log.e(activity.javaClass.simpleName, "Error while getting the cart list items.", e)
                }
    }




    fun removeItemFromCart(context: Context, cart_id: String) {

        // Cart items collection name
        mFireStore.collection(Constants.CART_ITEMS)
                .document(cart_id) // cart id
                .delete()
                .addOnSuccessListener {

                    // TODO Step 6: Notify the success result of the removed cart item from the list to the base class.
                    // START
                    // Notify the success result of the removed cart item from the list to the base class.
                    when (context) {
                        is Cartlistactivity -> {
                            context.itemRemovedSuccess()
                        }
                    }
                    // END
                }
                .addOnFailureListener { e ->

                    // Hide the progress dialog if there is any error.
                    when (context) {
                        is Cartlistactivity -> {
                            context.hideProgressDialog()
                        }
                    }
                    Log.e(
                            context.javaClass.simpleName,
                            "Error while removing the item from the cart list.",
                            e
                    )
                }
    }


    fun updateMyCart(context: Context, cart_id: String, itemHashMap: HashMap<String, Any>) {

        // Cart items collection name
        mFireStore.collection(Constants.CART_ITEMS)
                .document(cart_id) // cart id
                .update(itemHashMap) // A HashMap of fields which are to be updated.
                .addOnSuccessListener {

                    // TODO Step 4: Notify the success result of the updated cart items list to the base class.
                    // START
                    // Notify the success result of the updated cart items list to the base class.
                    when (context) {
                        is Cartlistactivity -> {
                            context.itemUpdateSuccess()
                        }
                    }
                    // END
                }
                .addOnFailureListener { e ->

                    // Hide the progress dialog if there is any error.
                    when (context) {
                        is Cartlistactivity -> {
                            context.hideProgressDialog()
                        }
                    }

                    Log.e(
                            context.javaClass.simpleName,
                            "Error while updating the cart item.",
                            e
                    )
                }
    }
    fun addAddress(activity: AddressEditactivity, addressInfo: Address) {

        // Collection name address.
        mFireStore.collection(Constants.ADDRESSES)
            .document()
            // Here the userInfo are Field and the SetOption is set to merge. It is for if we wants to merge
            .set(addressInfo, SetOptions.merge())
            .addOnSuccessListener {

                // TODO Step 5: Notify the success result to the base class.
                // START
                // Here call a function of base activity for transferring the result to it.
                activity.addUpdateAddressSuccess()
                // END
            }
            .addOnFailureListener { e ->
                activity.hideProgressDialog()
                Log.e(
                    activity.javaClass.simpleName,
                    "Error while adding the address.",
                    e
                )
            }
    }
    fun getAddressesList(activity: AddressListActivity) {
        // The collection name for PRODUCTS
        mFireStore.collection(Constants.ADDRESSES)
            .whereEqualTo(Constants.USER_ID, getCurrentUserID())
            .get() // Will get the documents snapshots.
            .addOnSuccessListener { document ->
                // Here we get the list of boards in the form of documents.
                Log.e(activity.javaClass.simpleName, document.documents.toString())
                // Here we have created a new instance for address ArrayList.
                val addressList: ArrayList<Address> = ArrayList()

                // A for loop as per the list of documents to convert them into Boards ArrayList.
                for (i in document.documents) {

                    val address = i.toObject(Address::class.java)!!
                    address.id = i.id

                    addressList.add(address)
                }

                // TODO Step 4: Notify the success result to the base class.
                // START
                activity.successAddressListFromFirestore(addressList)
                // END
            }
            .addOnFailureListener { e ->
                // Here call a function of base activity for transferring the result to it.

                activity.hideProgressDialog()

                Log.e(activity.javaClass.simpleName, "Error while getting the address list.", e)
            }

    }

    fun updateAddress(activity: AddressEditactivity, addressInfo: Address, addressId: String) {

        mFireStore.collection(Constants.ADDRESSES)
            .document(addressId)
            // Here the userInfo are Field and the SetOption is set to merge. It is for if we wants to merge
            .set(addressInfo, SetOptions.merge())
            .addOnSuccessListener {

                // Here call a function of base activity for transferring the result to it.
                activity.addUpdateAddressSuccess()
            }
            .addOnFailureListener { e ->
                activity.hideProgressDialog()
                Log.e(
                    activity.javaClass.simpleName,
                    "Error while updating the Address.",
                    e
                )
            }
    }
    fun deleteAddress(activity: AddressListActivity, addressId: String) {

        mFireStore.collection(Constants.ADDRESSES)
            .document(addressId)
            .delete()
            .addOnSuccessListener {

                // TODO Step 8: Notify the success result.
                // START
                // Here call a function of base activity for transferring the result to it.
                activity.deleteAddressSuccess()
                // END
            }
            .addOnFailureListener { e ->
                activity.hideProgressDialog()
                Log.e(
                    activity.javaClass.simpleName,
                    "Error while deleting the address.",
                    e
                )
            }
    }
    fun placeOrder(activity: Checkoutactivity, order: Orders) {

        mFireStore.collection(Constants.ORDERS)
                .document()
                // Here the userInfo are Field and the SetOption is set to merge. It is for if we wants to merge
                .set(order, SetOptions.merge())
                .addOnSuccessListener {

                    // TODO Step 9: Notify the success result.
                    // START
                    // Here call a function of base activity for transferring the result to it.
                    activity.orderPlacedSuccess()
                    // END
                }
                .addOnFailureListener { e ->

                    // Hide the progress dialog if there is any error.
                    activity.hideProgressDialog()
                    Log.e(
                            activity.javaClass.simpleName,
                            "Error while placing an order.",
                            e
                    )
                }
    }

    fun updateAllDetails(activity: Checkoutactivity, cartList: ArrayList<Cart>) {

        val writeBatch = mFireStore.batch()

        // Here we will update the product stock in the products collection based to cart quantity.
        for (cart in cartList) {

            val productHashMap = HashMap<String, Any>()

            productHashMap[Constants.STOCK_QUANTITY] =
                    (cart.stock_quantity.toInt() - cart.cart_quantity.toInt()).toString()

            val documentReference = mFireStore.collection(Constants.PRODUCTS)
                    .document(cart.product_id)

            writeBatch.update(documentReference, productHashMap)
        }

        // Delete the list of cart items
        for (cart in cartList) {

            val documentReference = mFireStore.collection(Constants.CART_ITEMS)
                    .document(cart.id)
            writeBatch.delete(documentReference)
        }

        writeBatch.commit().addOnSuccessListener {

            // TODO Step 4: Finally after performing all the operation notify the user with the success result.
            // START
            activity.allDetailsUpdatedSuccessfully()
            // END

        }.addOnFailureListener { e ->
            // Here call a function of base activity for transferring the result to it.
            activity.hideProgressDialog()

            Log.e(activity.javaClass.simpleName, "Error while updating all the details after order placed.", e)
        }
    }


    }









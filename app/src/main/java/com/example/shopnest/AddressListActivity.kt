package com.example.shopnest

import android.app.Activity
import android.content.Intent
import android.os.Bundle
import android.util.Log
import android.view.View
import android.widget.Toast
import androidx.recyclerview.widget.ItemTouchHelper
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import kotlinx.android.synthetic.main.activity_address_list.*

class AddressListActivity : baseactivityt() {
    private var mSelectAddress: Boolean = false
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_address_list)
        setupActionBar()
        if (intent.hasExtra(Constants.EXTRA_SELECT_ADDRESS)) {
            mSelectAddress =
                intent.getBooleanExtra(Constants.EXTRA_SELECT_ADDRESS, false)
        }
        if (mSelectAddress) {
            tv_title.text = resources.getString(R.string.title_select_address)
        }
        tv_add_address.setOnClickListener {
            val intent = Intent(this@AddressListActivity, AddressEditactivity::class.java)
            startActivityForResult(intent,Constants.ADD_ADDRESS_REQUEST_CODE)
        }
        getAddressList()

    }
    public override fun onActivityResult(requestCode: Int, resultCode: Int, data: Intent?) {
        super.onActivityResult(requestCode, resultCode, data)
        if (resultCode == Activity.RESULT_OK) {
            if (requestCode == Constants.ADD_ADDRESS_REQUEST_CODE) {

                getAddressList()
            }
        } else if (resultCode == Activity.RESULT_CANCELED) {
            // A log is printed when user close or cancel the image selection.
            Log.e("Request Cancelled", "To add the address.")
        }
    }
    private fun setupActionBar() {

        setSupportActionBar(toolbar_address_list_activity)

        val actionBar = supportActionBar
        if (actionBar != null) {
            actionBar.setDisplayHomeAsUpEnabled(true)
            actionBar.setHomeAsUpIndicator(R.drawable.ic_baseline_arrow_back_ios_24)
        }

        toolbar_address_list_activity.setNavigationOnClickListener { onBackPressed() }
    }


    private fun getAddressList() {

        // Show the progress dialog.
       showprogressdialog(resources.getString(R.string.pleasewait))

        Firestoreclass().getAddressesList(this@AddressListActivity)
    }
    fun successAddressListFromFirestore(addressList: ArrayList<Address>) {

        // Hide the progress dialog
        hideProgressDialog()


        // Print all the list of addresses in the log with name.
        for (i in addressList) {

            Log.i("Name and Address", "${i.name} ::  ${i.address}")
        }
        if (addressList.size > 0) {

            rv_address_list.visibility = View.VISIBLE
            tv_no_address_found.visibility = View.GONE

            rv_address_list.layoutManager = LinearLayoutManager(this@AddressListActivity)
            rv_address_list.setHasFixedSize(true)

            val addressAdapter = AddressListadapter(this@AddressListActivity, addressList,mSelectAddress)
            rv_address_list.adapter = addressAdapter
            if (!mSelectAddress) {
                val editSwipeHandler = object : SwipeToEditCallback(this) {
                    override fun onSwiped(viewHolder: RecyclerView.ViewHolder, direction: Int) {

                        // TODO Step 7: Call the notifyEditItem function of the adapter class.
                        // START
                        val adapter = rv_address_list.adapter as AddressListadapter
                        adapter.notifyEditItem(
                            this@AddressListActivity,
                            viewHolder.adapterPosition
                        )
                        // END
                    }
                }
                val editItemTouchHelper = ItemTouchHelper(editSwipeHandler)
                editItemTouchHelper.attachToRecyclerView(rv_address_list)
                val deleteSwipeHandler = object : SwipeToDeleteCallback(this) {
                    override fun onSwiped(viewHolder: RecyclerView.ViewHolder, direction: Int) {

                        // Show the progress dialog.
                        showprogressdialog(resources.getString(R.string.pleasewait))

                        // TODO Step 5: Call the function to delete the address from cloud firetore.
                        // START
                        Firestoreclass().deleteAddress(
                            this@AddressListActivity,
                            addressList[viewHolder.adapterPosition].id
                        )
                        // END
                    }
                }
                val deleteItemTouchHelper = ItemTouchHelper(deleteSwipeHandler)
                deleteItemTouchHelper.attachToRecyclerView(rv_address_list)

            }


        } else {
            rv_address_list.visibility = View.GONE
            tv_no_address_found.visibility = View.VISIBLE
        }
    }
    fun deleteAddressSuccess() {

        // Hide progress dialog.
        hideProgressDialog()

        Toast.makeText(
            this@AddressListActivity,
            resources.getString(R.string.err_your_address_deleted_successfully),
            Toast.LENGTH_SHORT
        ).show()

        getAddressList()
    }
    // END
}
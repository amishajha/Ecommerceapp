package com.example.shopnest

import android.content.Intent
import android.os.Bundle
import android.view.View
import com.google.firebase.auth.FirebaseAuth
import kotlinx.android.synthetic.main.settingactivity.*

class SettingActivity :baseactivityt(), View.OnClickListener {
    private lateinit var mUserDetails: User
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.settingactivity)
        setupActionBar()
        tv_edit.setOnClickListener(this@SettingActivity)
        btn_logout.setOnClickListener(this@SettingActivity)
       ll_address.setOnClickListener(this@SettingActivity)

    }
    override fun onResume() {
        super.onResume()
        getUserDetails()
    }
    override fun onClick(v: View?) {
        if (v != null) {
            when (v.id) {


                R.id.tv_edit -> {
                    val intent = Intent(this@SettingActivity, Userprofileactivity::class.java)
                    intent.putExtra(Constants.EXTRA_USER_DETAILS,mUserDetails)
                    startActivity(intent)
                }

                R.id.btn_logout -> {

                    FirebaseAuth.getInstance().signOut()

                    val intent = Intent(this@SettingActivity, Loginactivity::class.java)
                    intent.flags = Intent.FLAG_ACTIVITY_NEW_TASK or Intent.FLAG_ACTIVITY_CLEAR_TASK
                    startActivity(intent)
                    finish()
                }
                R.id.ll_address->{
                    val intent=Intent(this@SettingActivity,AddressListActivity::class.java)
                    startActivity(intent)
                }
                // END
            }
        }
    }
    private fun setupActionBar() {

        setSupportActionBar(toolbar_settings_activity)

        val actionBar = supportActionBar
        if (actionBar != null) {
            actionBar.setDisplayHomeAsUpEnabled(true)
            actionBar.setHomeAsUpIndicator(R.drawable.ic_baseline_arrow_back_ios_24)
        }

        toolbar_settings_activity.setNavigationOnClickListener { onBackPressed() }
    }


    private fun getUserDetails(){
         showprogressdialog("Please wait")
        Firestoreclass().getUserDetails(this@SettingActivity)

    }

    fun userDetailsSuccess(user: User) {

        mUserDetails = user

        hideProgressDialog()

        // Load the image using the Glide Loader class.
        GlideLoader(this@SettingActivity).loadUserPicture(user.image, iv_user_photo)

        tv_name.text = "${user.firstName} ${user.lastName}"
        tv_gender.text = user.gender
        tv_email.text = user.email
        tv_mobile_number.text = "${user.mobile}"
        // END
    }
}
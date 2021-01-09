package com.example.shopnest

import android.Manifest
import android.app.Activity
import android.content.Intent
import android.content.pm.PackageManager
import android.net.Uri
import android.os.Bundle
import android.text.TextUtils
import android.util.Log
import android.view.View
import android.widget.Toast
import androidx.core.app.ActivityCompat
import androidx.core.content.ContextCompat
import kotlinx.android.synthetic.main.activity_userprofileactivity.*
import java.io.IOException

class Userprofileactivity:baseactivityt(), View.OnClickListener {


        // Instance of User data model class. We will initialize it later on.
        private lateinit var mUserDetails: User

        // Add a global variable for URI of a selected image from phone storage.
        private var mSelectedImageFileUri: Uri? = null

        // TODO Step 1: Create a global variable for uploaded image URL.
        // START
        private var mUserProfileImageURL: String = ""
        // END

        /**
         * This function is auto created by Android when the Activity Class is created.
         */
        override fun onCreate(savedInstanceState: Bundle?) {
            //This call the parent constructor
            super.onCreate(savedInstanceState)
            // This is used to align the xml view to this class
            setContentView(R.layout.activity_userprofileactivity)

            if (intent.hasExtra(Constants.EXTRA_USER_DETAILS)) {
                // Get the user details from intent as a ParcelableExtra.
                mUserDetails = intent.getParcelableExtra(Constants.EXTRA_USER_DETAILS)!!
            }
            // If the profile is incomplete then user is from login screen and wants to complete the profile.
            if (mUserDetails.profileCompleted == 0) {
                // Update the title of the screen to complete profile.
                tv_title.text = resources.getString(R.string.title_complete_profile)

                // Here, the some of the edittext components are disabled because it is added at a time of Registration.
                et_first_name.isEnabled = false
                et_first_name.setText(mUserDetails.firstName)

                et_last_name.isEnabled = false
                et_last_name.setText(mUserDetails.lastName)

                et_email.isEnabled = false
                et_email.setText(mUserDetails.email)
            } else {

                // Call the setup action bar function.


                // Update the title of the screen to edit profile.
                tv_title.text = resources.getString(R.string.title_edit_profile)

                // Load the image using the GlideLoader class with the use of Glide Library.
                GlideLoader(this@Userprofileactivity).loadUserPicture(mUserDetails.image, iv_user_photo)

                // Set the existing values to the UI and allow user to edit except the Email ID.
                et_first_name.setText(mUserDetails.firstName)
                et_last_name.setText(mUserDetails.lastName)

                et_email.isEnabled = false
                et_email.setText(mUserDetails.email)

                if (mUserDetails.mobile != 0L) {
                    et_mobile_number.setText(mUserDetails.mobile.toString())
                }
                if (mUserDetails.gender == Constants.MALE) {
                    rb_male.isChecked = true
                } else {
                    rb_female.isChecked = true
                }
            }
            iv_user_photo.setOnClickListener(this@Userprofileactivity)
            // Assign the on click event to the SAVE button.
            btn_save.setOnClickListener(this@Userprofileactivity)
        }

        override fun onClick(v: View?) {
            if (v != null) {
                when (v.id) {

                    R.id.iv_user_photo -> {

                        if (ContextCompat.checkSelfPermission(
                                this,
                                Manifest.permission.READ_EXTERNAL_STORAGE
                            )
                            == PackageManager.PERMISSION_GRANTED
                        ) {
                            Constants.showImageChooser(this@Userprofileactivity)
                        } else {
                            /*Requests permissions to be granted to this application. These permissions
                             must be requested in your manifest, they should not be granted to your app,
                             and they should have protection level*/
                            ActivityCompat.requestPermissions(
                                this,
                                arrayOf(Manifest.permission.READ_EXTERNAL_STORAGE),
                                Constants.READ_STORAGE_PERMISSION_CODE
                            )
                        }
                    }

                    R.id.btn_save -> {

                        // TODO Step 3: Uncomment the code and use the image URL global variable to update the image URL to Firestore. Make the necessary changes.

                        if (validateUserProfileDetails()) {

                            // TODO Step 12: Make it common for the both cases.
                            // START
                            // Show the progress dialog.
                            showprogressdialog(resources.getString(R.string.pleasewait))
                            // END

                            if (mSelectedImageFileUri != null) {

                                Firestoreclass().uploadImageToCloudStorage(
                                    this@Userprofileactivity,
                                    mSelectedImageFileUri, Constants.USER_PROFILE_IMAGE
                                )
                            } else {

                                // TODO Step 4: Move this piece of code to the separate function as the profile image is optional. So, if the image is uploaded then we will update the image URL in the firestore.
                                // START
                                /*val userHashMap = HashMap<String, Any>()

                                // Here the field which are not editable needs no update. So, we will update user Mobile Number and Gender for now.

                                // Here we get the text from editText and trim the space
                                val mobileNumber = et_mobile_number.text.toString().trim { it <= ' ' }

                                val gender = if (rb_male.isChecked) {
                                    Constants.MALE
                                } else {
                                    Constants.FEMALE
                                }

                                if (mobileNumber.isNotEmpty()) {
                                    userHashMap[Constants.MOBILE] = mobileNumber.toLong()
                                }

                                userHashMap[Constants.GENDER] = gender

                                // Show the progress dialog.
                                showProgressDialog(resources.getString(R.string.please_wait))

                                // call the registerUser function of FireStore class to make an entry in the database.
                                FirestoreClass().updateUserProfileData(
                                    this@UserProfileActivity,
                                    userHashMap
                                )*/

                                // END

                                // TODO Step 8: Call the user update details function.
                                // START
                                updateUserProfileDetails()
                                // END
                            }
                        }
                        // END
                    }
                }
            }
        }

        /**
         * This function will identify the result of runtime permission after the user allows or deny permission based on the unique code.
         *
         * @param requestCode
         * @param permissions
         * @param grantResults
         */
        override fun onRequestPermissionsResult(
            requestCode: Int,
            permissions: Array<out String>,
            grantResults: IntArray
        ) {
            super.onRequestPermissionsResult(requestCode, permissions, grantResults)
            if (requestCode == Constants.READ_STORAGE_PERMISSION_CODE) {
                //If permission is granted
                if (grantResults.isNotEmpty() && grantResults[0] == PackageManager.PERMISSION_GRANTED) {
                    Constants.showImageChooser(this@Userprofileactivity)
                } else {
                    //Displaying another toast if permission is not granted
                    Toast.makeText(
                        this,
                        resources.getString(R.string.read_storage_permission_denied),
                        Toast.LENGTH_LONG
                    ).show()
                }
            }
        }

        /**
         * Receive the result from a previous call to
         * {@link #startActivityForResult(Intent, int)}.  This follows the
         * related Activity API as described there in
         * {@link Activity#onActivityResult(int, int, Intent)}.
         *
         * @param requestCode The integer request code originally supplied to
         *                    startActivityForResult(), allowing you to identify who this
         *                    result came from.
         * @param resultCode The integer result code returned by the child activity
         *                   through its setResult().
         * @param data An Intent, which can return result data to the caller
         *               (various data can be attached to Intent "extras").
         */
        public override fun onActivityResult(requestCode: Int, resultCode: Int, data: Intent?) {
            super.onActivityResult(requestCode, resultCode, data)
            if (resultCode == Activity.RESULT_OK) {
                if (requestCode == Constants.PICK_IMAGE_REQUEST_CODE) {
                    if (data != null) {
                        try {

                            // The uri of selected image from phone storage.
                            mSelectedImageFileUri = data.data!!

                            GlideLoader(this@Userprofileactivity).loadUserPicture(
                                mSelectedImageFileUri!!,
                                iv_user_photo
                            )
                        } catch (e: IOException) {
                            e.printStackTrace()
                            Toast.makeText(
                                this@Userprofileactivity,
                                resources.getString(R.string.image_selection_failed),
                                Toast.LENGTH_SHORT
                            )
                                .show()
                        }
                    }
                }
            } else if (resultCode == Activity.RESULT_CANCELED) {
                // A log is printed when user close or cancel the image selection.
                Log.e("Request Cancelled", "Image selection cancelled")
            }
        }

        /**
         * A function to validate the input entries for profile details.
         */
        private fun validateUserProfileDetails(): Boolean {
            return when {

                // We have kept the user profile picture is optional.
                // The FirstName, LastName, and Email Id are not editable when they come from the login screen.
                // The Radio button for Gender always has the default selected value.

                // Check if the mobile number is not empty as it is mandatory to enter.
                TextUtils.isEmpty(et_mobile_number.text.toString().trim { it <= ' ' }) -> {
                    showErrorSnackBar(resources.getString(R.string.err_msg_enter_mobile_number), true)
                    false
                }
                else -> {
                    true
                }
            }
        }

        // TODO Step 5: Create a function to update the user profile details to firestore.
        // START
        /**
         * A function to update user profile details to the firestore.
         */
        private fun updateUserProfileDetails() {

            val userHashMap = HashMap<String, Any>()

            // Here the field which are not editable needs no update. So, we will update user Mobile Number and Gender for now.

            // Here we get the text from editText and trim the space
            val mobileNumber = et_mobile_number.text.toString().trim { it <= ' ' }

            val gender = if (rb_male.isChecked) {
                Constants.MALE
            } else {
                Constants.FEMALE
            }

            // TODO Step 7: Now update the profile image field if the image URL is not empty.
            // START
            if (mUserProfileImageURL.isNotEmpty()) {
                userHashMap[Constants.IMAGE] = mUserProfileImageURL
            }
            // END

            if (mobileNumber.isNotEmpty()) {
                userHashMap[Constants.MOBILE] = mobileNumber.toLong()
            }

            userHashMap[Constants.GENDER] = gender
            userHashMap[Constants.COMPLETE_PROFILE] = 1

            // TODO 11 : Remove the show progress dialog piece of code from here to avoid the jerk hiding and showing it at the same time.
            // START
            // Show the progress dialog.
            /*showProgressDialog(resources.getString(R.string.please_wait))*/
            // END

            // call the registerUser function of FireStore class to make an entry in the database.
            Firestoreclass().updateUserProfileData(
                this@Userprofileactivity,
                userHashMap
            )
        }
        // END

        /**
         * A function to notify the success result and proceed further accordingly after updating the user details.
         */
        fun userProfileUpdateSuccess() {

            // Hide the progress dialog
            hideProgressDialog()

            Toast.makeText(
                this@Userprofileactivity,
                resources.getString(R.string.msg_profile_update_success),
                Toast.LENGTH_SHORT
            ).show()


            // Redirect to the Main Screen after profile completion.
            startActivity(Intent(this@Userprofileactivity, DashboardActivity::class.java))
            finish()
        }

        /**
         * A function to notify the success result of image upload to the Cloud Storage.
         *
         * @param imageURL After successful upload the Firebase Cloud returns the URL.
         */
        fun imageUploadSuccess(imageURL: String) {

            // TODO Step 10: Remove the hide progress dialog code
            // START
            // Hide the progress dialog
            /*hideProgressDialog()*/
            // END

            // TODO Step 2: Remove the Toast message and assign the value to the global variable.
            // START
            mUserProfileImageURL = imageURL
            // END

            // TODO Step 9: Call the user update details function.
            // START
            updateUserProfileDetails()
            // END
        }
    }




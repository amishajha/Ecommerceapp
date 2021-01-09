package com.example.shopnest


import android.content.Intent
import android.os.Bundle
import android.text.TextUtils
import android.view.View
import android.view.WindowManager
import com.example.shopnest.ui.dashboard.DashboardFragment
import com.google.firebase.auth.FirebaseAuth
import kotlinx.android.synthetic.main.activity_loginactivity.*


@Suppress("DEPRECATION")
class Loginactivity : baseactivityt(),View.OnClickListener {

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_loginactivity)
        window.setFlags(
                WindowManager.LayoutParams.FLAG_FULLSCREEN,
                WindowManager.LayoutParams.FLAG_FULLSCREEN


        )

        btn_login.setOnClickListener(this)
        tv_forgot_password.setOnClickListener(this)
        registeraccount.setOnClickListener(this)

    }

    override fun onClick(v: View?) {
        if (v != null) {
            when (v.id) {

                R.id.tv_forgot_password -> {
                    // TODO Step 8: Launch the Forgot Password Activity when user clicks on forgot password text.
                    // START
                    // Launch the forgot password screen when the user clicks on the forgot password text.
                    val intent = Intent(this@Loginactivity, Forgotpasswordactivity::class.java)
                    startActivity(intent)
                    // END
                }

                R.id.btn_login -> {

                    logInRegisteredUser()
                }

                R.id.registeraccount -> {
                    // Launch the register screen when the user clicks on the text.
                    val intent = Intent(this@Loginactivity, Registeractivity::class.java)
                    startActivity(intent)
                }
            }
        }
    }

    /**
     * A function to validate the login entries of a user.
     */
    private fun validateLoginDetails(): Boolean {
        return when {
            TextUtils.isEmpty(et_email.text.toString().trim { it <= ' ' }) -> {
                showErrorSnackBar(resources.getString(R.string.err_msg_enter_email), true)
                false
            }
            TextUtils.isEmpty(et_password.text.toString().trim { it <= ' ' }) -> {
                showErrorSnackBar(resources.getString(R.string.err_msg_enter_password), true)
                false
            }
            else -> {
                true
            }
        }
    }

    /**
     * A function to Log-In. The user will be able to log in using the registered email and password with Firebase Authentication.
     */
    private fun logInRegisteredUser() {

        if (validateLoginDetails()) {

            // Show the progress dialog.
            showprogressdialog(resources.getString(R.string.pleasewait))

            // Get the text from editText and trim the space
            val email = et_email.text.toString().trim { it <= ' ' }
            val password = et_password.text.toString().trim { it <= ' ' }

            // Log-In using FirebaseAuth
            FirebaseAuth.getInstance().signInWithEmailAndPassword(email, password)
                    .addOnCompleteListener { task ->

                        // Hide the progress dialog
                        hideProgressDialog()

                        if (task.isSuccessful) {
                            Firestoreclass().getUserDetails(this@Loginactivity)
                           // showErrorSnackBar("You are logged in successfully.", false)
                        } else {
                            showErrorSnackBar(task.exception!!.message.toString(), true)
                        }
                    }
        }
    }
    fun userLoggedInSuccess(user: User) {

        // Hide the progress dialog.
        hideProgressDialog()



        // Redirect the user to Main Screen after log in.
        if (user.profileCompleted==0) {
            // If the user profile is incomplete then launch the UserProfileActivity.
            val intent = Intent(this@Loginactivity, Userprofileactivity::class.java)
            intent.putExtra(Constants.EXTRA_USER_DETAILS, user)
            startActivity(intent)
        } else {
            // Redirect the user to Main Screen after log in.
            startActivity(Intent(this@Loginactivity, DashboardActivity::class.java))
        }
        finish()

    }
}


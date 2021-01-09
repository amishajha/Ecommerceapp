package com.example.shopnest

import android.app.Dialog
import android.os.Handler
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import androidx.core.content.ContextCompat
import com.google.android.material.snackbar.Snackbar
import kotlinx.android.synthetic.main.progress_dialog.*

open class baseactivityt:AppCompatActivity() {
    private lateinit var mProgressDialog: Dialog
    private var doubleBackToExitPressedOnce = false
    fun showErrorSnackBar(message: String, errorMessage: Boolean) {
        val snackBar =
                Snackbar.make(findViewById(android.R.id.content), message, Snackbar.LENGTH_LONG)
        val snackBarView = snackBar.view

        if (errorMessage) {
            snackBarView.setBackgroundColor(
                    ContextCompat.getColor(
                            this@baseactivityt,
                            R.color.colorSnackBarError
                    )
            )
        }else{
            snackBarView.setBackgroundColor(
                    ContextCompat.getColor(
                            this@baseactivityt,
                            R.color.colorSnackBarSuccess
                    )
            )
        }
        snackBar.show()
    }
    fun showprogressdialog(text:String){
        mProgressDialog = Dialog(this)

        /*Set the screen content from a layout resource.
        The resource will be inflated, adding all top-level views to the screen.*/
        mProgressDialog.setContentView(R.layout.progress_dialog)

        mProgressDialog.progresstext.text = text

        mProgressDialog.setCancelable(false)
        mProgressDialog.setCanceledOnTouchOutside(false)

        //Start the dialog and display it on screen.
        mProgressDialog.show()

    }
    fun hideProgressDialog()
    {
       mProgressDialog.dismiss()
    }
    fun doubleBackToExit() {

        if (doubleBackToExitPressedOnce) {
            super.onBackPressed()
            return
        }

        this.doubleBackToExitPressedOnce = true

        Toast.makeText(
            this,
            resources.getString(R.string.please_click_back_again_to_exit),
            Toast.LENGTH_SHORT
        ).show()

        @Suppress("DEPRECATION")
        Handler().postDelayed({ doubleBackToExitPressedOnce = false }, 2000)
    }
}

package com.example.shopnest

import android.os.Bundle
import android.widget.Toast
import com.google.firebase.auth.FirebaseAuth
import kotlinx.android.synthetic.main.forgotpasswordactivity.*

class Forgotpasswordactivity:baseactivityt() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.forgotpasswordactivity)
        submit.setOnClickListener {
            val email:String=et_forgot_email.text.toString().trim{
                it<=' ' }
            if(email.isEmpty()){
                showErrorSnackBar(resources.getString(R.string.err_msg_enter_email),true)

            }else{
                showprogressdialog(resources.getString(R.string.pleasewait))
                FirebaseAuth.getInstance().sendPasswordResetEmail(email).addOnCompleteListener {
                    task ->
                    hideProgressDialog()
                    if(task.isSuccessful){
                        Toast.makeText(this@Forgotpasswordactivity,"Email sent successfully to reset your password",Toast.LENGTH_SHORT).show()
                        finish()
                    }
                    else{
                        Toast.makeText(this@Forgotpasswordactivity,task.exception!!.message.toString(),Toast.LENGTH_LONG).show()
                    }
                }
            }
        }

    }
}
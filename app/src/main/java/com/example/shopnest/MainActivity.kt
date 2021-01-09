package com.example.shopnest

import android.Manifest
import android.app.Activity
import android.content.Intent
import android.content.pm.PackageManager
import android.net.Uri
import android.os.Bundle
import android.provider.MediaStore
import android.util.Log
import android.webkit.MimeTypeMap
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import androidx.core.app.ActivityCompat
import androidx.core.content.ContextCompat
import com.bumptech.glide.Glide
import com.google.firebase.storage.FirebaseStorage
import com.google.firebase.storage.StorageReference
import kotlinx.android.synthetic.main.mainactivity.*
import java.io.IOException

class MainActivity : AppCompatActivity() {
    private var mSelectedImageFileUri: Uri? = null
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.mainactivity)
        btn_select_image.setOnClickListener {
            if (ContextCompat.checkSelfPermission(
                            this,
                            Manifest.permission.READ_EXTERNAL_STORAGE
                    )
                    == PackageManager.PERMISSION_GRANTED
            ) {
                // TODO Step 6: Now after the permission is granted write the code to select the image.
                // START
                // An intent for launching the image selection of phone storage.
                val galleryIntent = Intent(
                        Intent.ACTION_PICK,
                        MediaStore.Images.Media.EXTERNAL_CONTENT_URI
                )
                // Launches the image selection of phone storage using the constant code.
                startActivityForResult(galleryIntent, 222)
                // END
            } else {
                /*Requests permissions to be granted to this application. These permissions
                 must be requested in your manifest, they should not be granted to your app,
                 and they should have protection level*/
                ActivityCompat.requestPermissions(
                        this,
                        arrayOf(Manifest.permission.READ_EXTERNAL_STORAGE),
                        121
                )
            }
        }
        btn_upload_image.setOnClickListener {

            if (mSelectedImageFileUri != null) {

                // Get the image extension.
                /*MimeTypeMap: Two-way map that maps MIME-types to file extensions and vice versa.
        getSingleton(): Get the singleton instance of MimeTypeMap.
        getExtensionFromMimeType: Return the registered extension for the given MIME type.
        contentResolver.getType: Return the MIME type of the given content URL.*/
                val imageExtension = MimeTypeMap.getSingleton()
                        .getExtensionFromMimeType(contentResolver.getType(mSelectedImageFileUri!!))

                //getting the storage reference
                val sRef: StorageReference = FirebaseStorage.getInstance().reference.child(
                        "Image" + System.currentTimeMillis() + "."
                                + imageExtension
                )

                //adding the file to reference
                sRef.putFile(mSelectedImageFileUri!!)
                        .addOnSuccessListener { taskSnapshot ->
                            // The image upload is success
                            Log.e(
                                    "Firebase Image URL",
                                    taskSnapshot.metadata!!.reference!!.downloadUrl.toString()
                            )

                            // Get the downloadable url from the task snapshot
                            taskSnapshot.metadata!!.reference!!.downloadUrl
                                    .addOnSuccessListener { url ->
                                        Log.e("Downloadable Image URL", url.toString())

                                        btn_upload_image.text =
                                                "Your image uploaded successfully :: $url"
                                        Glide.with(this).load(url).placeholder(R.mipmap.ic_launcher).into(image_view)
                                    }
                                    .addOnFailureListener { exception ->
                                        Toast.makeText(
                                                this,
                                                exception.message,
                                                Toast.LENGTH_LONG
                                        ).show()
                                        Log.e(javaClass.simpleName, exception.message, exception)
                                    }
                        }
            } else {

                Toast.makeText(
                        this,
                        "Please select the image to upload.",
                        Toast.LENGTH_LONG
                ).show()
            }
        }
        // E


    }


    override fun onRequestPermissionsResult(
            requestCode: Int,
            permissions: Array<out String>,
            grantResults: IntArray
    ) {
        super.onRequestPermissionsResult(requestCode, permissions, grantResults)
        if (requestCode == 121) {
            //If permission is granted
            if (grantResults.isNotEmpty() && grantResults[0] == PackageManager.PERMISSION_GRANTED) {
                // TODO Step 7: Now after the permission is granted write the code to select the image.
                // START
                // An intent for launching the image selection of phone storage.
                val galleryIntent = Intent(
                        Intent.ACTION_PICK,
                        MediaStore.Images.Media.EXTERNAL_CONTENT_URI
                )
                // Launches the image selection of phone storage using the constant code.
                startActivityForResult(galleryIntent, 222)
                // END
            } else {
                //Displaying another toast if permission is not granted
                Toast.makeText(
                        this,
                        "Oops, you just denied the permission for storage. You can also allow it from settings.",
                        Toast.LENGTH_LONG
                ).show()
            }
        }
    }




    public override fun onActivityResult(requestCode: Int, resultCode: Int, data: Intent?) {
        super.onActivityResult(requestCode, resultCode, data)
        if (resultCode == Activity.RESULT_OK) {
            if (requestCode == 222) {
                if (data != null) {
                    try {

                        // TODO Step 10: Initialize the global variable for URI and set the image to the ImageView.
                        // START
                        // The uri of selected image from phone storage.
                        mSelectedImageFileUri = data.data!!

                        Glide.with(this).load(mSelectedImageFileUri).into(image_view)
                    } catch (e: IOException) {
                        e.printStackTrace()
                        Toast.makeText(
                                this@MainActivity,
                                "Image selection Failed!",
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
    // END
}
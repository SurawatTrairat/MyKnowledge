package com.example.myknowledge.activities

import android.Manifest
import android.app.Activity
import android.app.AlertDialog
import android.content.ActivityNotFoundException
import android.content.Intent
import android.content.pm.PackageManager
import android.net.Uri
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.provider.Settings
import android.util.Log
import android.widget.Toast
import androidx.core.app.ActivityCompat
import androidx.core.content.ContextCompat
import com.bumptech.glide.Glide
import com.example.myknowledge.Constants
import com.example.myknowledge.R
import com.example.myknowledge.firebase.FireStoreClass
import com.example.myknowledge.models.User
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.storage.FirebaseStorage
import com.google.firebase.storage.StorageReference
import com.karumi.dexter.Dexter
import com.karumi.dexter.DexterBuilder
import com.karumi.dexter.MultiplePermissionsReport
import com.karumi.dexter.PermissionToken
import com.karumi.dexter.listener.PermissionDeniedResponse
import com.karumi.dexter.listener.PermissionGrantedResponse
import com.karumi.dexter.listener.PermissionRequest
import com.karumi.dexter.listener.multi.MultiplePermissionsListener
import com.karumi.dexter.listener.single.PermissionListener
import kotlinx.android.synthetic.main.activity_history.*
import kotlinx.android.synthetic.main.activity_profile_upload.*
import java.io.IOException

class ProfileUploadActivity : BaseActivity() {

    private var mSelectedProfileImageUri: Uri? = null
    private var mProfileImageUri: String = ""
    private lateinit var mUserDetails: User

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_profile_upload)

        setupActionBar()

        civ_my_profile_image_selector.setOnClickListener {
            if(ContextCompat.checkSelfPermission(
                            this@ProfileUploadActivity, Manifest.permission.READ_EXTERNAL_STORAGE) == PackageManager.PERMISSION_GRANTED){
                Constants.showImageChooser(this@ProfileUploadActivity)
            }else{
                Dexter.withContext(this@ProfileUploadActivity)
                        .withPermission(Manifest.permission.READ_EXTERNAL_STORAGE)
                        .withListener(
                                object: PermissionListener{
                                    override fun onPermissionGranted(p0: PermissionGrantedResponse?) {
                                        Constants.showImageChooser(this@ProfileUploadActivity)
                                    }
                                    override fun onPermissionDenied(p0: PermissionDeniedResponse?) {
                                        showErrorSnackBar(resources.getString(R.string.permission_denied))
                                    }
                                    override fun onPermissionRationaleShouldBeShown(p0: PermissionRequest?, p1: PermissionToken?) {
                                        showRationalDialogForPermission()
                                    }
                                }
                        ).onSameThread().check()
            }
        }
        btn_upload.setOnClickListener{
            if(mSelectedProfileImageUri != null){
                showProgressDialog(resources.getString(R.string.please_wait))
                uploadProfileImage()
                hideProgressDialog()
            }else{
                showErrorSnackBar(resources.getString(R.string.please_select_photo_for_profile_upload))
            }
        }
    }

    private fun uploadProfileImage(){
        showProgressDialog(resources.getString(R.string.please_wait))
        var sRef: StorageReference = FirebaseStorage.getInstance().reference.child(
            resources.getString(R.string.profile_image) +
                    System.currentTimeMillis() +
                    "." +
                    Constants.getFileExtension(this@ProfileUploadActivity, mSelectedProfileImageUri)
        )
        sRef.putFile(mSelectedProfileImageUri!!).addOnSuccessListener {
            taskSnapshot ->
            showProgressDialog("Successfully uploaded profile image")
            taskSnapshot.metadata!!.reference!!.downloadUrl.addOnSuccessListener{
                uri ->
                mProfileImageUri = uri.toString()
                val userHashMap = HashMap<String, Any>()
                userHashMap[Constants.IMAGE] = mProfileImageUri
                FireStoreClass().updateProfileImageUrl(this@ProfileUploadActivity, userHashMap)
            }
        }.addOnFailureListener{
            Log.e("Error::", it.toString())
        }
    }
    fun profileUploadSuccess(){
        Toast.makeText(this,
                resources.getString(R.string.sign_up_success), Toast.LENGTH_LONG).show()
        hideProgressDialog()
        FirebaseAuth.getInstance().signOut()
        startActivity(Intent(this@ProfileUploadActivity, SignInActivity::class.java))
    }

    //private fun updateUserProfileImageData(){
      //  val userHashMap = HashMap<String, Any>()
        //if(mProfileImageUri.isNotEmpty()  && mProfileImageUri != )
    //}

    override fun onActivityResult(requestCode: Int, resultCode: Int, data: Intent?) {
        super.onActivityResult(requestCode, resultCode, data)
        if(data!!.data!=null &&
                requestCode == Constants.PICK_IMAGE_REQUEST_CODE &&
                resultCode == Activity.RESULT_OK){
            mSelectedProfileImageUri = data!!.data
            showProgressDialog(resources.getString(R.string.please_wait))
            try{
                Glide
                        .with(this@ProfileUploadActivity)
                        .load(mSelectedProfileImageUri)
                        .centerCrop()
                        .placeholder(R.drawable.ic_launcher_background)
                        .into(civ_my_profile_image_selector)
                hideProgressDialog()
            }catch (e: IOException){
                e.printStackTrace()
            }
        }
    }

    private fun setupActionBar(){
        setSupportActionBar(toolbar_history_activity)
        val actionBar = supportActionBar
        if(actionBar!=null){
            actionBar.setDisplayHomeAsUpEnabled(true)
            actionBar.setHomeAsUpIndicator(R.drawable.ic_black_color_back_24dp)
        }
        toolbar_history_activity.setNavigationOnClickListener {
            onBackPressed()
        }
    }

    private fun showRationalDialogForPermission(){
        AlertDialog.Builder(this)
                .setMessage("Please enable READ_STORAGE permission")
                .setPositiveButton("GO TO SETTINGS")
                { _,_ ->
                    try{
                        val intent = Intent(Settings.ACTION_APPLICATION_DETAILS_SETTINGS)
                        val uri = Uri.fromParts("package", packageName, null)
                        intent.data = uri
                        startActivity(intent)
                    }catch (e: ActivityNotFoundException){
                        e.printStackTrace()
                    }
                }.setNegativeButton("CANCEL")
                { dialog,_ ->
                    dialog.dismiss()
                }.show()
    }
}
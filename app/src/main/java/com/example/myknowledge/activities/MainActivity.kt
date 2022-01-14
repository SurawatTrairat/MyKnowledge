package com.example.myknowledge.activities

import android.Manifest
import android.content.Intent
import android.content.pm.PackageManager
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.util.Log
import androidx.appcompat.app.AlertDialog
import androidx.core.app.ActivityCompat
import androidx.core.content.ContextCompat
import com.bumptech.glide.Glide
import com.example.myknowledge.Constants
import com.example.myknowledge.R
import com.example.myknowledge.firebase.FireStoreClass
import com.example.myknowledge.models.User
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.firestore.DocumentSnapshot
import com.google.firebase.firestore.FirebaseFirestore
import kotlinx.android.synthetic.main.activity_main.*
import java.io.IOException

private val currentUser = FirebaseFirestore.getInstance()

class MainActivity : BaseActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)
        setupActionBar()
        if(isUserLoggedIn()){
            displayCurrentUserName()
            displayCurrentUserProfile()
        }
        btn_weather.setOnClickListener{
            startActivity(Intent(this@MainActivity, LocationRequest::class.java))
        }
        btn_history.setOnClickListener {
            startActivity(Intent(this@MainActivity, HistoryActivity::class.java))
        }
        civ_my_profile.setOnClickListener {
            if(ContextCompat.checkSelfPermission(
                            this, Manifest.permission.READ_EXTERNAL_STORAGE) == PackageManager.PERMISSION_GRANTED){
                Constants.showImageChooser(this@MainActivity)
            }else{
                ActivityCompat.requestPermissions(this@MainActivity,
                        arrayOf(Manifest.permission.READ_EXTERNAL_STORAGE), Constants.READ_STORAGE_REQUEST_CODE)
            }
        }
    }

    private fun setupActionBar(){
        setSupportActionBar(toolbar_main_activity)
        val actionBar = supportActionBar
        if(actionBar!=null){
            actionBar.setDisplayHomeAsUpEnabled(true)
            actionBar.setHomeAsUpIndicator(R.drawable.ic_power_sign_out_24)
        }
        toolbar_main_activity.setNavigationOnClickListener {
            val alertDialog = AlertDialog.Builder(this)
            alertDialog.setMessage(R.string.sign_out_disclosure)
                    .setCancelable(false)
                    .setPositiveButton(R.string.yes){ _,_ ->
                        FirebaseAuth.getInstance().signOut()
                        startActivity(Intent(this@MainActivity, IntroActivity::class.java))
                    }
                    .setNegativeButton(R.string.cancel){ _,_ ->
                        finish()
                    }
            val alert = alertDialog.create()
            alert.setTitle(R.string.sign_out)
            alert.show()
        }
    }

    private fun isUserLoggedIn(): Boolean{
        return currentUser != null
    }

    private fun displayCurrentUserName(){
        val userName = getCurrentUserName()
        tv_welcome_user.text = "${resources.getString(R.string.welcome)} $userName"
    }

    private fun displayCurrentUserProfile(){
        currentUser.collection(Constants.USER)
                .document(getCurrentUserID())
                .get().addOnSuccessListener { document ->
                    val profileImageUri = document.get(Constants.IMAGE)
                    try{
                        Glide
                                .with(this@MainActivity)
                                .load(profileImageUri)
                                .centerCrop()
                                .placeholder(R.drawable.ic_launcher_background          )
                                .into(civ_my_profile)
                    }catch (e: IOException){
                        e.printStackTrace()
                    }
                }
    }
}
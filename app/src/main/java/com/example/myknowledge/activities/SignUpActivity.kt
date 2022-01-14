package com.example.myknowledge.activities

import android.Manifest
import android.app.Activity
import android.content.Intent
import android.content.pm.PackageManager
import android.net.Uri
import android.os.Bundle
import android.util.Log
import android.widget.Toast
import androidx.core.app.ActivityCompat
import androidx.core.content.ContextCompat
import com.bumptech.glide.Glide
import com.example.myknowledge.Constants
import com.example.myknowledge.R
import com.example.myknowledge.firebase.FireStoreClass
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.auth.FirebaseUser
import com.google.firebase.storage.FirebaseStorage
import com.google.firebase.storage.StorageReference
import kotlinx.android.synthetic.main.activity_sign_up.*
import java.io.IOException

class SignUpActivity : BaseActivity() {

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_sign_up)
        setupActionBar()
        btn_sign_up_process.setOnClickListener {
            registerUser()
        }
    }

    fun userRegisteredSuccess(){
        Toast.makeText(this,
            resources.getString(R.string.sign_up_success), Toast.LENGTH_LONG).show()
        hideProgressDialog()
        startActivity(Intent(this@SignUpActivity, ProfileUploadActivity::class.java))
    }

    private fun registerUser(){
        val name = et_name_signUp.text.toString().trim{ it <= ' '}
        val email = et_email_signUp.text.toString().trim{ it <= ' '}
        val password = et_password_signUp.text.toString().trim{ it <= ' '}
        if(validateForm(name, email, password)){
            showProgressDialog(resources.getString(R.string.please_wait))
            FirebaseAuth.getInstance()
                .createUserWithEmailAndPassword(email, password).addOnCompleteListener {
                    task ->
                    hideProgressDialog()
                    if(task.isSuccessful){
                        showProgressDialog(resources.getString(R.string.please_wait))
                        val firebaseUser: FirebaseUser = task.result!!.user!!
                        val registeredEmail = firebaseUser.email!!
                        val user = com.example.myknowledge.models.User(
                            firebaseUser.uid, name, registeredEmail, "")
                        FireStoreClass().registerUser(this, user)
                    }else{
                        Toast.makeText(this,
                                "Registration Failed",
                                Toast.LENGTH_LONG).show()
                    }
                }.addOnFailureListener{
                        exception ->
                        Log.e("Sign Up Error::", exception.toString())
                    }
        }
    }

    private fun setupActionBar(){
        setSupportActionBar(toolbar_sign_up_activity)
        val actionBar = supportActionBar
        if(actionBar!=null){
            actionBar.setDisplayHomeAsUpEnabled(true)
            actionBar.setHomeAsUpIndicator(R.drawable.ic_black_color_back_24dp)
        }
        toolbar_sign_up_activity.setNavigationOnClickListener {
            onBackPressed()
        }
    }

}
package com.example.myknowledge.activities

import android.content.Intent
import android.os.Bundle
import android.util.Log
import android.widget.Toast
import com.example.myknowledge.R
import com.google.firebase.auth.FirebaseAuth
import kotlinx.android.synthetic.main.activity_sign_in.*

class SignInActivity : BaseActivity() {

    private lateinit var auth: FirebaseAuth

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_sign_in)
        setupActionBar()
        auth = FirebaseAuth.getInstance()
        btn_sign_in_authentication.setOnClickListener {
        signInRegisteredUser()
        }
    }

    private fun signInRegisteredUser(){
        val email: String = et_email_signIn.text.toString().trim{it <= ' '}
        val password: String = et_password_signIn.text.toString().trim{it <= ' '}
        if(validateForm(email, password)){
            showProgressDialog(resources.getString(R.string.please_wait))
            auth.signInWithEmailAndPassword(email, password).addOnCompleteListener(this){
                task ->
                if(task.isSuccessful){
                    Toast.makeText(this,
                            resources.getString(R.string.sign_in_success),
                            Toast.LENGTH_LONG).show()
                    startActivity(Intent(this, MainActivity::class.java))
                }else{
                    Toast.makeText(this,
                            "Authentication Failed",
                            Toast.LENGTH_LONG).show()
                }
            }.addOnFailureListener {
                exception ->
                Log.e("Sign In Error::", exception.toString())
            }
        }
    }

    private fun setupActionBar(){
        setSupportActionBar(toolbar_sign_in_activity)
        val actionBar = supportActionBar
        if(actionBar!=null){
            actionBar.setDisplayHomeAsUpEnabled(true)
            actionBar.setHomeAsUpIndicator(R.drawable.ic_black_color_back_24dp)
        }
        toolbar_sign_in_activity.setNavigationOnClickListener {
            onBackPressed()
        }
    }

}
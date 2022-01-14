package com.example.myknowledge.activities

import android.app.Dialog
import android.text.TextUtils
import androidx.appcompat.app.AppCompatActivity
import androidx.core.content.ContextCompat
import com.example.myknowledge.Constants
import com.example.myknowledge.R
import com.google.android.material.snackbar.Snackbar
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.firestore.FirebaseFirestore
import kotlinx.android.synthetic.main.dialog_progress.*

open class BaseActivity: AppCompatActivity() {

    private lateinit var  mProgressDialog: Dialog

    private var currentUser = FirebaseFirestore.getInstance()

    fun showProgressDialog(text: String){
        mProgressDialog = Dialog(this)
        mProgressDialog.setContentView(R.layout.dialog_progress)
        mProgressDialog.tv_progess_dialog.text = text
        mProgressDialog.show()
    }

    fun hideProgressDialog(){
        mProgressDialog.dismiss()
    }

    fun validateForm(name: String, email: String, password: String): Boolean{
        return when{
            TextUtils.isEmpty(name) -> {
                showErrorSnackBar(R.string.error_name.toString())
                false
            }
            TextUtils.isEmpty(email) -> {
                showErrorSnackBar(R.string.error_email.toString())
                false
            }
            TextUtils.isEmpty(password) -> {
                showErrorSnackBar(R.string.error_password.toString())
                false
            }
            else -> return true
        }
    }

    fun validateForm(email: String, password: String): Boolean{
        return when{
            TextUtils.isEmpty(email) -> {
                showErrorSnackBar(R.string.error_email.toString())
                false
            }
            TextUtils.isEmpty(password) -> {
                showErrorSnackBar(R.string.error_password.toString())
                false
            }
            else -> return true
        }
    }

    fun showErrorSnackBar(message: String){
        val snackBar = Snackbar.make(findViewById(android.R.id.content),
        message,Snackbar.LENGTH_LONG)
        val snackBarView = snackBar.view
        snackBarView.setBackgroundColor(
            ContextCompat.getColor(this, R.color.snack_bar_error_color)
        )
        snackBar.show()
    }


    fun getCurrentUserName():String{
        var userName:String = ""
        currentUser.collection(Constants.USER)
                .document(getCurrentUserID())
                .get().addOnSuccessListener { document ->
                    userName = document.get(Constants.NAME).toString()
                }
        return userName
    }

    fun getCurrentUserID(): String{
        val currentUser = FirebaseAuth.getInstance().currentUser
        if(currentUser!=null){
            return currentUser.uid
        }
        return ""
    }

}
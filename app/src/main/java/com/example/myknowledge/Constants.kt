package com.example.myknowledge

import android.annotation.SuppressLint
import android.app.Activity
import android.content.Context
import android.content.Intent
import android.hardware.camera2.params.Capability
import android.net.ConnectivityManager
import android.net.NetworkCapabilities
import android.net.Uri
import android.os.Build
import android.provider.MediaStore
import android.webkit.MimeTypeMap

object Constants {

    const val USER: String = "user"
    const val WEATHER_HISTORY = "weatherHistory"
    const val NAME: String = "name"
    const val EMAIL: String = "email"
    const val IMAGE: String = "image"

    const val APP_ID: String = "82748114234b867232916b018d9987f1"
    const val BASE_URL: String = "https://api.openweathermap.org/data/"
    const val METRIC_UNIT: String = "metric"

    const val READ_STORAGE_REQUEST_CODE = 1
    const val PICK_IMAGE_REQUEST_CODE = 2

    fun showImageChooser(activity: Activity){
        val galleryIntent = Intent(Intent.ACTION_PICK, MediaStore.Images.Media.EXTERNAL_CONTENT_URI)
        activity.startActivityForResult(galleryIntent, PICK_IMAGE_REQUEST_CODE)
    }

    fun getFileExtension(activity: Activity, uri: Uri?): String?{
        return MimeTypeMap.getSingleton()
                .getExtensionFromMimeType(activity.contentResolver.getType(uri!!))
    }

    @SuppressLint("ServiceCast")
    fun isNetworkAvailable(context: Context): Boolean{
        val connectivityManager =
                context.getSystemService(Context.CONNECTIVITY_SERVICE) as ConnectivityManager
        if(Build.VERSION.SDK_INT >= Build.VERSION_CODES.M){ //check version
            val network = connectivityManager.activeNetwork ?: return false //if any active network else false
            val activeNetwork = connectivityManager.getNetworkCapabilities(network) ?: return false //if networkcap is empty return false
            return when{
                activeNetwork.hasTransport(NetworkCapabilities.TRANSPORT_WIFI) -> true //if we have wifi connection and else below
                activeNetwork.hasTransport(NetworkCapabilities.TRANSPORT_CELLULAR) -> true
                activeNetwork.hasTransport(NetworkCapabilities.TRANSPORT_ETHERNET) -> true
                else -> false
            }
        }else{ //for older versions
            val netWorkInfo = connectivityManager.activeNetworkInfo
            return netWorkInfo != null && netWorkInfo.isConnectedOrConnecting
        }
    }
}
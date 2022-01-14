package com.example.myknowledge.firebase

import android.net.Uri
import android.util.Log
import android.widget.Toast
import com.example.myknowledge.Constants
import com.example.myknowledge.R
import com.example.myknowledge.activities.*
import com.example.myknowledge.models.User
import com.example.myknowledge.models.WeatherHistory
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.firestore.FirebaseFirestore
import com.google.firebase.firestore.SetOptions

class FireStoreClass: BaseActivity() {

    private lateinit var mFireStore: FirebaseFirestore

    fun registerUser(activity: SignUpActivity, userInfo: User){
        mFireStore = FirebaseFirestore.getInstance()
        mFireStore.collection(Constants.USER)
            .document(getCurrentUserID())
            .set(userInfo, SetOptions.merge())
            .addOnSuccessListener {
                activity.userRegisteredSuccess()
            }.addOnFailureListener {
                Toast.makeText(activity,
                    "Registration Failed", Toast.LENGTH_LONG)
                    .show()
            }
    }

    fun getWeatherHistoryList(activity: HistoryActivity){
        //val weatherHistoryList: ArrayList<WeatherHistory> = ArrayList()
        mFireStore = FirebaseFirestore.getInstance()
        mFireStore
                .collection((Constants.WEATHER_HISTORY))
                .get()
                .addOnCompleteListener {

                    val weatherHistoryList: ArrayList<WeatherHistory> = ArrayList()

                    if(it.isSuccessful){
                        for(document in it.result!!){

                            val weatherHistory = document.toObject(WeatherHistory::class.java)
                            Log.i("WWHH", weatherHistory.toString())
                            weatherHistoryList.add(weatherHistory)

                        }
                        activity.populateWeatherHistoryIntoRecyclerView(weatherHistoryList)
                    }
                }
    }

    fun saveUserWeatherDetail(activity: LocationRequest, weatherHistory: WeatherHistory){
        mFireStore = FirebaseFirestore.getInstance()
        mFireStore.collection(Constants.WEATHER_HISTORY)
                .document(getCurrentUserID())
                .set(weatherHistory, SetOptions.merge())
                .addOnSuccessListener {
                    activity.weatherSavedSuccess()
                }.addOnFailureListener { Log.e("WeatherSavedError::", it.toString()) }
    }

    fun updateProfileImageUrl(activity: ProfileUploadActivity ,userHashMap: HashMap<String,Any>){
        mFireStore = FirebaseFirestore.getInstance()
        mFireStore.collection(Constants.USER)
                .document(getCurrentUserID())
                .update(userHashMap)
                .addOnSuccessListener {
                    Log.i("Uploaded::", "profileImageUri")
                    activity.profileUploadSuccess()
                }
                .addOnFailureListener { Log.e("ProfileUploadError::", it.toString()) }
    }

}
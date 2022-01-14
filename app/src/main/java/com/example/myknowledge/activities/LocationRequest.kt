package com.example.myknowledge.activities

import android.Manifest
import android.annotation.SuppressLint
import android.app.AlertDialog
import android.content.ActivityNotFoundException
import android.content.Context
import android.content.Intent
import android.location.Location
import android.os.Bundle
import android.location.LocationManager
import android.net.Uri
import android.os.Looper
import android.provider.Settings
import android.util.Log
import android.view.View
import android.widget.Toast
import com.example.myknowledge.Constants
import com.example.myknowledge.R
import com.example.myknowledge.firebase.FireStoreClass
import com.example.myknowledge.models.WeatherHistory
import com.example.myknowledge.models.WeatherResponse
import com.example.myknowledge.network.WeatherService
import com.google.android.gms.location.*
import com.google.android.gms.location.LocationRequest
import com.google.firebase.firestore.FirebaseFirestore
import com.karumi.dexter.Dexter
import com.karumi.dexter.MultiplePermissionsReport
import com.karumi.dexter.PermissionToken
import com.karumi.dexter.listener.PermissionRequest
import com.karumi.dexter.listener.multi.MultiplePermissionsListener
import kotlinx.android.synthetic.main.activity_location_request.*
import kotlinx.android.synthetic.main.item_weather_detail.view.*
import retrofit2.Call
import retrofit2.Callback
import retrofit2.Response
import retrofit2.Retrofit
import retrofit2.converter.gson.GsonConverterFactory
import java.text.SimpleDateFormat
import java.time.LocalDateTime
import java.time.format.DateTimeFormatter
import java.util.*

class LocationRequest : BaseActivity() {

    private lateinit var mFusedLocationClient: FusedLocationProviderClient //to use services to read lat and long into geographic location
    private lateinit var weatherList: WeatherResponse

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_location_request)

        mFusedLocationClient = LocationServices.getFusedLocationProviderClient(this)

        btn_display.setOnClickListener {
            if (!isLocationEnabled()){
                startActivity(Intent(Settings.ACTION_LOCATION_SOURCE_SETTINGS))
            }else{
                Dexter.withContext(this)
                        .withPermissions(
                                Manifest.permission.ACCESS_FINE_LOCATION,
                                Manifest.permission.ACCESS_COARSE_LOCATION
                        ).withListener(
                                object : MultiplePermissionsListener{
                                    override fun onPermissionsChecked(report: MultiplePermissionsReport?) {
                                        if(report!!.areAllPermissionsGranted()){
                                            requestLocationData()
                                            btn_save.isEnabled = true
                                        }
                                        if(report.isAnyPermissionPermanentlyDenied){
                                            Toast.makeText(this@LocationRequest, "Permission Denied, Please Request", Toast.LENGTH_LONG).show()
                                        }
                                    }

                                    override fun onPermissionRationaleShouldBeShown(
                                            p0: MutableList<PermissionRequest>?,
                                            p1: PermissionToken?
                                    ) {
                                        showRationalDialogForPermissions()
                                    }

                                }
                        ).onSameThread()
                        .check()
            }
        }

        btn_save.setOnClickListener {
            saveWeatherDetails()
        }

    }

    private fun getLocationWeatherDetails(latitude: Double, longitude: Double){
        if(Constants.isNetworkAvailable(this@LocationRequest)){
            val retrofit: Retrofit = Retrofit.Builder()
                    .baseUrl(Constants.BASE_URL)
                    .addConverterFactory(GsonConverterFactory.create())
                    .build()
            val service: WeatherService = retrofit
                    .create(WeatherService::class.java)
            val listCall: Call<WeatherResponse> = service.getWeather(
                    latitude, longitude, Constants.METRIC_UNIT, Constants.APP_ID)
            listCall.enqueue(object : Callback<WeatherResponse> {
                override fun onResponse(call: Call<WeatherResponse>, response: Response<WeatherResponse>) {
                    if (response.isSuccessful) {
                        weatherList = response.body()!!
                        displayWeatherRecyclerView()
                        Log.i("Response Result::", "$weatherList")
                    } else {
                        val rc = response.code()
                        Log.i("Response Code::", "$rc")
                    }
                }
                override fun onFailure(call: Call<WeatherResponse>, t: Throwable) {
                    Log.i("ERROR", "$t")
                }
            })
        }else{
            Toast.makeText(this@LocationRequest, "No Internet Access", Toast.LENGTH_LONG).show()
        }
    }

    @SuppressLint("MissingPermission")
    private fun requestLocationData(){
        val mLocationRequest = LocationRequest()
        mLocationRequest.priority = LocationRequest.PRIORITY_HIGH_ACCURACY
        mFusedLocationClient.requestLocationUpdates(
                mLocationRequest, mLocationCallback, Looper.myLooper()
        )
    }

    private val mLocationCallback = object: LocationCallback(){
        override fun onLocationResult(locationResult: LocationResult?) {
            val mLastLocation: Location = locationResult!!.lastLocation
            val mlatitude = mLastLocation.latitude
            val mlongitude = mLastLocation.longitude
            getLocationWeatherDetails(mlatitude, mlongitude)
        }
    }

    private fun showRationalDialogForPermissions(){
        AlertDialog.Builder(this)
                .setMessage("Please enable location permission")
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

    private fun displayWeatherRecyclerView(){
        tv_no_weather_details_available.visibility =  View.GONE
        ll_single_weather_detail.visibility = View.VISIBLE
        tv_location.text = weatherList.sys.country
        tv_temp_current.text = weatherList.main.temp.toString()
        tv_temp_max.text = weatherList.main.temp_max.toString()
        tv_temp_min.text = weatherList.main.temp_min.toString()
        tv_wind.text = weatherList.wind.toString()
        tv_humidity.text = weatherList.main.humidity.toString()
        tv_sunrise.text = unixTime(weatherList.sys.sunrise)
        tv_sunset.text = unixTime(weatherList.sys.sunset)
    }

    private fun saveWeatherDetails(){
        val weatherHistory = WeatherHistory(
         getCurrentUserID()
        ,weatherList.sys.country
        ,getCurrentDateTime()
        ,weatherList.main.temp
        ,weatherList.main.temp_max
        ,weatherList.main.temp_min
        ,weatherList.wind.speed
        ,weatherList.main.humidity
        ,weatherList.sys.sunrise
        ,weatherList.sys.sunset)
        FireStoreClass().saveUserWeatherDetail(this@LocationRequest, weatherHistory)
    }

    fun weatherSavedSuccess(){
        showProgressDialog(resources.getString(R.string.please_wait))
        Toast.makeText(this,
                resources.getString(R.string.weather_saved_success), Toast.LENGTH_LONG).show()
        hideProgressDialog()
        startActivity(Intent(this@LocationRequest, MainActivity::class.java))
    }

    private fun isLocationEnabled(): Boolean{
        val locationManager: LocationManager = getSystemService(Context.LOCATION_SERVICE) as LocationManager
        return locationManager.isProviderEnabled(LocationManager.GPS_PROVIDER) ||
                locationManager.isProviderEnabled(LocationManager.NETWORK_PROVIDER)
    }

    @SuppressLint("NewApi")
    private fun getCurrentDateTime(): String{
        val dateTimeFormat = "yyyy-MM-dd HH:mm"
        return LocalDateTime.now()
                .format(DateTimeFormatter
                        .ofPattern(dateTimeFormat))
    }

    private fun unixTime(timex: Long): String?{
        val date = Date(timex * 1000L)
        val sdf = SimpleDateFormat("HH:mm")
        sdf.timeZone = TimeZone.getDefault()
        return sdf.format(date)
    }

}
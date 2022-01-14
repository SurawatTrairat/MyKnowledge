package com.example.myknowledge.activities

import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.util.Log
import android.view.View
import androidx.recyclerview.widget.LinearLayoutManager
import com.example.myknowledge.R
import com.example.myknowledge.adapters.WeatherItemAdapter
import com.example.myknowledge.firebase.FireStoreClass
import com.example.myknowledge.models.WeatherHistory
import kotlinx.android.synthetic.main.activity_history.*
import kotlinx.android.synthetic.main.activity_sign_in.*

class HistoryActivity : BaseActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_history)
        setupActionBar()
        FireStoreClass().getWeatherHistoryList(this@HistoryActivity)
    }

    fun populateWeatherHistoryIntoRecyclerView(weatherHistoryList: ArrayList<WeatherHistory>){
        if(weatherHistoryList.size > 0){
            tv_no_weather_history_available.visibility = View.GONE
            rv_weather_history_list.visibility = View.VISIBLE

            rv_weather_history_list.layoutManager = LinearLayoutManager(this@HistoryActivity)
            rv_weather_history_list.setHasFixedSize(true)

            val adapter = WeatherItemAdapter(this@HistoryActivity, weatherHistoryList)
            rv_weather_history_list.adapter = adapter
        }else{
            tv_no_weather_history_available.visibility = View.VISIBLE
            rv_weather_history_list.visibility = View.GONE
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

}
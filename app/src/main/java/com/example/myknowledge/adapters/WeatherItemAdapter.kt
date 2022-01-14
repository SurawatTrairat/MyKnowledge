package com.example.myknowledge.adapters

import android.content.Context
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.recyclerview.widget.RecyclerView
import com.bumptech.glide.Glide
import com.example.myknowledge.Constants
import com.example.myknowledge.R
import com.example.myknowledge.models.WeatherHistory
import com.example.myknowledge.models.WeatherResponse
import com.google.firebase.firestore.FirebaseFirestore
import kotlinx.android.synthetic.main.item_weather_detail.view.*

open class WeatherItemAdapter(
        private val context: Context,
        private var list: ArrayList<WeatherHistory>
):RecyclerView.Adapter<RecyclerView.ViewHolder>() {

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): RecyclerView.ViewHolder {
        return MyViewHolder(
                LayoutInflater.from(context)
                        .inflate(R.layout.item_weather_detail, parent, false)
        )
    }

    override fun onBindViewHolder(holder: RecyclerView.ViewHolder, position: Int) {
        val model = list[position]
        if(holder is MyViewHolder){
            val uid = model.assignedTo
            var profileImageUri:String = ""
            var userName:String = ""
            FirebaseFirestore.getInstance()
                    .collection(Constants.USER)
                    .document(uid)
                    .get().addOnSuccessListener { document ->
                        profileImageUri = document.get(Constants.IMAGE).toString()
                        userName = document.get(Constants.NAME).toString()
                        Glide
                                .with(context)
                                .load(profileImageUri)
                                .centerCrop()
                                .placeholder(R.drawable.ic_launcher_background)
                                .into(holder.itemView.civ_history_profile)

                        holder.itemView.user_profile.text = userName
                    }

            holder.itemView.gps_data.text = model.location
            holder.itemView.date_time_data.text = model.date
            holder.itemView.current_temp_data.text = model.currentTemp.toString()
            holder.itemView.humidity_data.text = model.humidity.toString()
            holder.itemView.wind_data.text = model.windSpeed.toString()
        }
    }

    override fun getItemCount(): Int {
        return list.size
    }

    private class MyViewHolder(view: View): RecyclerView.ViewHolder(view)

}
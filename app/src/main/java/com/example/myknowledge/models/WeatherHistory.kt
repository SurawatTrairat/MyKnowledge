package com.example.myknowledge.models

import android.os.Parcel
import android.os.Parcelable

data class WeatherHistory(
        var assignedTo:String = "",
        var location:String = "",
        var date:String = "",
        var currentTemp:Double = 0.0,
        var maxTemp:Double = 0.0,
        var minTemp:Double = 0.0,
        var windSpeed:Double = 0.0,
        var humidity:Int = 0,
        var sunrise:Long = 0L,
        var sunset:Long = 0L
): Parcelable {
    constructor(parcel: Parcel) : this(
            parcel.readString()!!,
            parcel.readString()!!,
            parcel.readString()!!,
            parcel.readDouble()!!,
            parcel.readDouble()!!,
            parcel.readDouble()!!,
            parcel.readDouble()!!,
            parcel.readInt()!!,
            parcel.readLong()!!,
            parcel.readLong()!!
    ) {
    }

    override fun writeToParcel(parcel: Parcel, flags: Int) {
        parcel.writeString(assignedTo)
        parcel.writeString(location)
        parcel.writeString(date)
        parcel.writeDouble(currentTemp)
        parcel.writeDouble(maxTemp)
        parcel.writeDouble(minTemp)
        parcel.writeDouble(windSpeed)
        parcel.writeInt(humidity)
        parcel.writeLong(sunrise)
        parcel.writeLong(sunset)
    }

    override fun describeContents(): Int {
        return 0
    }

    companion object CREATOR : Parcelable.Creator<User> {
        override fun createFromParcel(parcel: Parcel): User {
            return User(parcel)
        }

        override fun newArray(size: Int): Array<User?> {
            return arrayOfNulls(size)
        }
    }
}
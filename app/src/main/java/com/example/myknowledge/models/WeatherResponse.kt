package com.example.myknowledge.models

import java.io.Serializable

data class WeatherResponse(
        val savedBy: String = "",
        val coord: Coord,
        val weather: List<Weather>,
        val base: String,
        val main: Main,
        val wind: Wind,
        val sys: Sys,
        val name: String
): Serializable
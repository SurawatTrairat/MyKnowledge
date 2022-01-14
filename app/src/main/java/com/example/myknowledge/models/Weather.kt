package com.example.myknowledge.models

import java.io.Serializable

data class Weather(
        val main: String,
        val description: String
): Serializable
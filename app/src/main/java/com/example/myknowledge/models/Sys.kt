package com.example.myknowledge.models

import java.io.Serializable

data class Sys(
        val country: String,
        val sunrise: Long,
        val sunset: Long
): Serializable
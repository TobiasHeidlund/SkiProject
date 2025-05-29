package me.heidlund.skiapp.models

import java.io.Serializable


data class SkiPoint(
    val lat: Double,
    val lon: Double,
    val alt: Double,
    val speed: Double,
    val time: Long,
    val error: Float
) : Serializable {
    override fun toString(): String {
        return "SkiPoint(lat=$lat, lon=$lon, alt=$alt, speed=$speed, time=$time, error=$error)"
    }
}

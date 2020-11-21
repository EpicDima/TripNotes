package com.asistlab.tripnotes.data

import androidx.room.TypeConverter
import com.asistlab.tripnotes.data.model.Trip
import com.google.gson.Gson
import java.util.*

/**
 * @author EpicDima
 */
class Converters {

    @TypeConverter
    fun locationsToString(value: List<Trip.LatLng>): String = Gson().toJson(value)

    @TypeConverter
    fun stringToLocations(value: String): List<Trip.LatLng> =
        Gson().fromJson(value, Array<Trip.LatLng>::class.java).toList()

    @TypeConverter
    fun pointsToString(value: List<String>): String = Gson().toJson(value)

    @TypeConverter
    fun stringToPoints(value: String): List<String>
            = Gson().fromJson(value, Array<String>::class.java).toList()

    @TypeConverter
    fun dateToLong(value: Date): Long = value.time

    @TypeConverter
    fun longToDate(value: Long): Date = Date(value)
}
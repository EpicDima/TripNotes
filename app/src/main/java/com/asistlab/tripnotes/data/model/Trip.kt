package com.asistlab.tripnotes.data.model

import androidx.room.Entity
import androidx.room.PrimaryKey
import com.google.android.gms.maps.model.LatLng
import java.util.*

/**
 * @author EpicDima
 */
@Entity(tableName = "trips")
data class Trip(
    @PrimaryKey(autoGenerate = true)
    val id: Long = 0,
    val name: String = "",
    val description: String = "",
    val locations: List<LatLng> = emptyList(),
    val points: List<String> = emptyList(),
    val planTime: Date = Date(),
    val trackTime: Long = 0, // minutes
    val done: Boolean = false
)

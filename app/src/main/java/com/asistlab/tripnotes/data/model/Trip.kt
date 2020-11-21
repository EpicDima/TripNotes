package com.asistlab.tripnotes.data.model

import androidx.room.Entity
import androidx.room.PrimaryKey
import java.text.DateFormat
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
    val addresses: List<String> = emptyList(),
    val start: Date = Date(),
    val end: Date = Date(),
    val done: Boolean = false
) {
    companion object {
        private const val MILLISECONDS_IN_DAY = 86_400_000L

        private val FORMATTER: DateFormat = DateFormat.getDateInstance()
    }

    fun startDateToString(): String = FORMATTER.format(start)

    fun endDateToString(): String = FORMATTER.format(end)

    private fun calculateTrackTime(): Long = (end.time - start.time)

    fun calculateTrackTimeInDays(): Long = (calculateTrackTime() / MILLISECONDS_IN_DAY)

    private fun calculateRemainingTime(): Long = (end.time - Date().time)

    fun calculateRemainingTimeInDays(): Long =
        (calculateRemainingTime() / MILLISECONDS_IN_DAY)

    fun isStarted(): Boolean = (Date() >= start)

    fun isOver(): Boolean = done || (Date() >= end)


    class LatLng(val latitude: Double = 0.0, val longitude: Double = 0.0)
}

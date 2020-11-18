package com.asistlab.tripnotes

import java.time.LocalDateTime

/**
 * @author EpicDima
 */
data class Trip(
    val id: Long,
    val name: String,
    val description: String,
    val routeLink: String,
    val planTime: LocalDateTime,
    val trackTime: String,
    val done: Boolean = false
)

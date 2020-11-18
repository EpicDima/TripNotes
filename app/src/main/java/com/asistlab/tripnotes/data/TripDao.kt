package com.asistlab.tripnotes.data

import androidx.lifecycle.LiveData
import androidx.room.*
import com.asistlab.tripnotes.data.model.Trip

/**
 * @author EpicDima
 */
@Dao
interface TripDao {
    @Query("SELECT * FROM trips")
    fun selectLiveData(): LiveData<List<Trip>>

    @Query("SELECT * FROM trips")
    suspend fun select(): List<Trip>

    @Query("SELECT * FROM trips WHERE done = :done")
    fun selectLiveDataWhereDone(done: Boolean): LiveData<List<Trip>>

    @Query("SELECT * FROM trips WHERE done = :done")
    suspend fun selectWhereDone(done: Boolean): List<Trip>

    @Insert(onConflict = OnConflictStrategy.ABORT)
    suspend fun insert(vararg trips: Trip)

    @Update(onConflict = OnConflictStrategy.ABORT)
    suspend fun update(vararg trips: Trip)

    @Delete
    suspend fun delete(vararg trips: Trip)
}
package com.asistlab.tripnotes.data

import androidx.lifecycle.LiveData
import androidx.room.*
import com.asistlab.tripnotes.data.model.Trip

/**
 * @author EpicDima
 */
@Dao
interface TripDao {
    @Query("SELECT * FROM trips WHERE id = :id")
    suspend fun selectById(id: Long): List<Trip>

    @Query("SELECT * FROM trips")
    fun selectLiveData(): LiveData<List<Trip>>

    @Insert(onConflict = OnConflictStrategy.ABORT)
    suspend fun insert(vararg trips: Trip): LongArray

    @Update(onConflict = OnConflictStrategy.ABORT)
    suspend fun update(vararg trips: Trip)

    @Delete
    suspend fun delete(vararg trips: Trip)

    @Query("DELETE FROM trips")
    suspend fun deleteAll()
}
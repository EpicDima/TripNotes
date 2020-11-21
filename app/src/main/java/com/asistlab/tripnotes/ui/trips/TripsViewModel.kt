package com.asistlab.tripnotes.ui.trips

import androidx.hilt.lifecycle.ViewModelInject
import androidx.lifecycle.*
import com.asistlab.tripnotes.data.TripDao
import com.asistlab.tripnotes.data.model.Trip
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.database.DataSnapshot
import com.google.firebase.database.DatabaseError
import com.google.firebase.database.DatabaseReference
import com.google.firebase.database.ValueEventListener
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch


/**
 * @author EpicDima
 */
class TripsViewModel @ViewModelInject constructor(
    private val dao: TripDao,
    auth: FirebaseAuth,
    database: DatabaseReference
) : ViewModel() {

    private val _trips = dao.selectLiveData().switchMap { list ->
        MutableLiveData(list.filter { !it.isOver() })
    }
    val trips: LiveData<List<Trip>> = _trips

    init {
        database.child(auth.currentUser!!.uid).addListenerForSingleValueEvent(object :
            ValueEventListener {
            override fun onDataChange(snapshot: DataSnapshot) {
                snapshot.children.forEach {
                    upsert(it.getValue(Trip::class.java))
                }
            }

            override fun onCancelled(error: DatabaseError) {}
        })
    }

    private fun upsert(trip: Trip?) = viewModelScope.launch(Dispatchers.IO) {
        if (trip != null) {
            val selected = dao.selectById(trip.id)
            if (selected.isEmpty()) {
                dao.insert(trip)
            } else {
                dao.update(trip)
            }
        }
    }
}
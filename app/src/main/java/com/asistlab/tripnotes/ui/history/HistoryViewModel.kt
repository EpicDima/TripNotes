package com.asistlab.tripnotes.ui.history

import androidx.hilt.lifecycle.ViewModelInject
import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import androidx.lifecycle.switchMap
import com.asistlab.tripnotes.data.TripDao
import com.asistlab.tripnotes.data.model.Trip

/**
 * @author EpicDima
 */
class HistoryViewModel @ViewModelInject constructor(
    dao: TripDao
) : ViewModel() {

    private val _trips = dao.selectLiveData().switchMap { list ->
        MutableLiveData(list.filter { it.isOver() })
    }
    val trips: LiveData<List<Trip>> = _trips
}
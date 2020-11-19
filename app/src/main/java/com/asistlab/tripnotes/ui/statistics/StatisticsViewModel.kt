package com.asistlab.tripnotes.ui.statistics

import androidx.hilt.lifecycle.ViewModelInject
import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import com.asistlab.tripnotes.data.TripDao

/**
 * @author EpicDima
 */
class StatisticsViewModel @ViewModelInject constructor(
    private val dao: TripDao
) : ViewModel() {

}
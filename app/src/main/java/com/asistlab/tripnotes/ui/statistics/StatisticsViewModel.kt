package com.asistlab.tripnotes.ui.statistics

import androidx.hilt.lifecycle.ViewModelInject
import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import androidx.lifecycle.switchMap
import com.asistlab.tripnotes.R
import com.asistlab.tripnotes.data.TripDao

/**
 * @author EpicDima
 */
class StatisticsViewModel @ViewModelInject constructor(
    dao: TripDao
) : ViewModel() {

    private val _categoriesCountMap = dao.selectLiveData().switchMap { list ->
        MutableLiveData(mapOf(
            Category.NOT_STARTED to list.count { !it.isStarted() && !it.isOver() },
            Category.STARTED to list.count { it.isStarted() && !it.done },
            Category.DONE to list.count { it.done },
            Category.FINISHED to list.count { it.isOver() && !it.done }
        ))
    }
    val categoriesCountMap: LiveData<Map<Category, Int>> = _categoriesCountMap

    private val _durationCountMap = dao.selectLiveData().switchMap { list ->
        val map = LinkedHashMap<Long, Int>()
        for (days in list.map { it.calculateTrackTimeInDays() }) {
            map[days] = (map[days] ?: 0) + 1
        }
        MutableLiveData<Map<Long, Int>>(map.toSortedMap { days1, days2 -> (days1 - days2).toInt() })
    }
    val durationCountMap: LiveData<Map<Long, Int>> = _durationCountMap

    private val _locationCountMap = dao.selectLiveData().switchMap { list ->
        val map = LinkedHashMap<Int, Int>()
        for (count in list.map { it.locations.size }) {
            map[count] = (map[count] ?: 0) + 1
        }
        MutableLiveData<Map<Int, Int>>(map.toSortedMap { days1, days2 -> (days1 - days2) })
    }
    val locationCountMap: LiveData<Map<Int, Int>> = _locationCountMap


    enum class Category(
        val key: Int
    ) {
        NOT_STARTED(R.string.category_not_started),
        STARTED(R.string.category_started),
        DONE(R.string.category_done),
        FINISHED(R.string.category_finished)
    }
}
package com.asistlab.tripnotes.ui.trip

import android.graphics.Bitmap
import androidx.hilt.lifecycle.ViewModelInject
import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.asistlab.tripnotes.R
import com.asistlab.tripnotes.data.TripDao
import com.asistlab.tripnotes.data.model.Trip
import com.asistlab.tripnotes.other.getImageName
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.database.DatabaseReference
import com.google.firebase.storage.StorageReference
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import java.io.ByteArrayOutputStream
import java.util.*
import kotlin.collections.ArrayList

/**
 * @author EpicDima
 */
class TripViewModel @ViewModelInject constructor(
    private val dao: TripDao,
    private val auth: FirebaseAuth,
    private val database: DatabaseReference,
    private val storage: StorageReference,
) : ViewModel() {

    companion object {
        const val MAX_POINTS = 7
    }

    private var trip: Trip = Trip()

    private val _tripLiveData = MutableLiveData(trip)
    val tripLiveData: LiveData<Trip> = _tripLiveData

    private val _page = MutableLiveData(Page.SHOW)
    val page: LiveData<Page> = _page

    private var image: Bitmap? = null

    private val _points = MutableLiveData<List<Point>>(emptyList())
    val points: LiveData<List<Point>> = _points

    private val _formState = MutableLiveData<FormState>()
    val formState: LiveData<FormState> = _formState

    private val _error = MutableLiveData<Int?>()
    val error: LiveData<Int?> = _error

    private val _success = MutableLiveData<Unit>()
    val success: LiveData<Unit> = _success

    fun init(tripId: Long) = viewModelScope.launch(Dispatchers.IO) {
        val temp = dao.selectById(tripId)
        if (temp.isEmpty()) {
            _page.postValue(Page.CREATE)
        } else {
            trip = temp[0]
            _tripLiveData.postValue(trip)
            _points.postValue(trip.addresses.mapIndexed { index, address ->
                Point(trip.locations[index], address)
            })
        }
    }

    fun edit() {
        if (_page.value!! == Page.SHOW) {
            _page.value = Page.EDIT
        }
    }

    private fun getDatabaseReferenceForTrip(trip: Trip): DatabaseReference =
        database.child(auth.currentUser!!.uid).child(trip.id.toString())

    fun save() = viewModelScope.launch(Dispatchers.IO) {
        if (validate()) {
            if (_page.value!! == Page.CREATE) {
                val id = dao.insert(trip)[0]
                trip = trip.copy(id = id)
                getDatabaseReferenceForTrip(trip).setValue(trip)
                saveImage()
                _success.postValue(null)
            } else if (_page.value!! == Page.EDIT) {
                dao.update(trip)
                getDatabaseReferenceForTrip(trip).setValue(trip)
                saveImage()
                _success.postValue(null)
            }
        }
    }

    private fun saveImage() {
        if (image != null) {
            val baos = ByteArrayOutputStream()
            image!!.compress(Bitmap.CompressFormat.JPEG, 70, baos)
            storage.child(getImageName(auth, trip)).putBytes(baos.toByteArray())
        }
    }

    fun getImageStorage(): StorageReference {
        return storage.child(getImageName(auth, trip))
    }

    fun done() = viewModelScope.launch(Dispatchers.IO) {
        trip = trip.copy(done = true)
        dao.update(trip)
        getDatabaseReferenceForTrip(trip).setValue(trip)
        _success.postValue(null)
    }

    private fun validate() =
        if (!isNameValid(trip.name)) {
            _error.postValue(R.string.invalid_name)
            false
        } else if (!isDescriptionValid(trip.description)) {
            _error.postValue(R.string.invalid_description)
            false
        } else if (!isDatesValid(trip.start, trip.end)) {
            _error.postValue(R.string.invalid_dates)
            false
        } else if (!isPointsValid(points.value!!)) {
            _error.postValue(R.string.invalid_points)
            false
        } else {
            true
        }

    fun delete() = viewModelScope.launch(Dispatchers.IO) {
        dao.delete(trip)
        getDatabaseReferenceForTrip(trip).removeValue()
    }

    fun setImage(bitmap: Bitmap) {
        image = bitmap.copy(Bitmap.Config.ARGB_8888, false)
    }

    fun inputChanged(name: String, description: String) {
        if (!isNameValid(name)) {
            _formState.value = FormState(nameError = R.string.invalid_name)
        } else {
            trip = trip.copy(name = name.trim(), description = description.trim())
            _formState.value = FormState(isDataValid = true)
        }
    }

    private fun isNameValid(name: String) = (name.trim().length >= 6)

    private fun isDescriptionValid(description: String) = true

    private fun isDatesValid(start: Date, end: Date) = (start < end)

    private fun isPointsValid(points: List<Point>) = (points.size >= 2)

    fun getLinkForMap(): String {
        val locationString = trip.locations.joinToString("/") {
            it.latitude.toString() + "," + it.longitude.toString()
        }
        return "https://www.google.by/maps/dir/$locationString"
    }

    fun setStartAndEnd(start: Long, end: Long) {
        trip = trip.copy(start = Date(start), end = Date(end))
        _tripLiveData.value = trip
    }

    fun removePoint() {
        if (trip.locations.isNotEmpty()) {
            trip = trip.copy(
                locations = trip.locations.dropLast(1),
                addresses = trip.addresses.dropLast(1)
            )
            _points.value = _points.value!!.dropLast(1)
            _tripLiveData.value = trip
        }
    }

    fun addPoint(address: String, location: Trip.LatLng) {
        if (trip.locations.size < MAX_POINTS) {
            val addresses = ArrayList(trip.addresses)
            addresses.add(address)
            val locations = ArrayList(trip.locations)
            locations.add(location)
            trip = trip.copy(locations = locations, addresses = addresses)
            val array = ArrayList(_points.value!!)
            array.add(Point(location, address))
            _points.value = array
            _tripLiveData.value = trip
        }
    }

    data class Point(
        val location: Trip.LatLng,
        val address: String
    )

    data class FormState(
        val nameError: Int? = null,
        val descriptionError: Int? = null,
        val isDataValid: Boolean = false
    )
}
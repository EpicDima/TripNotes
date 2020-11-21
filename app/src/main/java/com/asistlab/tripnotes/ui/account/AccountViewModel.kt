package com.asistlab.tripnotes.ui.account

import android.app.Application
import androidx.hilt.lifecycle.ViewModelInject
import androidx.lifecycle.AndroidViewModel
import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.viewModelScope
import com.asistlab.tripnotes.data.TripDao
import com.bumptech.glide.Glide
import com.google.firebase.auth.FirebaseAuth
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch

/**
 * @author EpicDima
 */
class AccountViewModel @ViewModelInject constructor(
    application: Application,
    private val dao: TripDao,
    private val auth: FirebaseAuth
) : AndroidViewModel(application) {

    private val _user = MutableLiveData<User?>(null)
    val user: LiveData<User?> = _user

    init {
        _user.value = User(auth.currentUser?.email)
    }

    fun signOut() {
        viewModelScope.launch(Dispatchers.IO) {
            auth.signOut()
            dao.deleteAll()
            Glide.get(getApplication()).clearDiskCache()
        }
        Glide.get(getApplication()).clearMemory()
    }

    data class User(val email: String?)
}
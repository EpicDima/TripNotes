package com.asistlab.tripnotes.ui.account

import androidx.hilt.lifecycle.ViewModelInject
import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.auth.ktx.auth
import com.google.firebase.ktx.Firebase

/**
 * @author EpicDima
 */
class AccountViewModel @ViewModelInject constructor(
    private val auth: FirebaseAuth
) : ViewModel() {

    private val _user = MutableLiveData<User?>(null)
    val user: LiveData<User?> = _user

    init {
        _user.value = User(auth.currentUser?.email)
    }

    fun signOut() {
        auth.signOut()
    }

    data class User(val email: String?)
}
package com.asistlab.tripnotes.ui.login

import android.util.Patterns
import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import androidx.lifecycle.ViewModelProvider
import com.asistlab.tripnotes.R
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.auth.ktx.auth
import com.google.firebase.ktx.Firebase

/**
 * @author EpicDima
 */
class LoginViewModel : ViewModel() {

    private val auth: FirebaseAuth = Firebase.auth

    private val _isLogged = MutableLiveData(false)
    val isLogged: MutableLiveData<Boolean> = _isLogged

    init {
        _isLogged.value = auth.currentUser != null
    }

    private val _isLoginPage = MutableLiveData(true)
    val isLoginPage: MutableLiveData<Boolean> = _isLoginPage

    private val _formState = MutableLiveData<FormState>()
    val formState: LiveData<FormState> = _formState

    private val _error = MutableLiveData<Int?>()
    val error: LiveData<Int?> = _error

    private val _loading = MutableLiveData(false)
    val loading: LiveData<Boolean> = _loading

    fun signIn(email: String, password: String) {
        _loading.value = true
        auth.signInWithEmailAndPassword(email, password)
            .addOnCompleteListener { task ->
                if (task.isSuccessful) {
                    _isLogged.value = true
                } else {
                    _error.value = R.string.login_failed
                }
                _loading.value = false
            }
    }

    fun signUp(email: String, password: String) {
        _loading.value = true
        auth.createUserWithEmailAndPassword(email, password)
            .addOnCompleteListener { task ->
                if (task.isSuccessful) {
                    _isLogged.value = true
                } else {
                    _error.value = R.string.registration_failed
                }
                _loading.value = false
            }
    }

    fun formDataChanged(email: String, password: String, passwordRepeat: String = "") {
        if (!isEmailValid(email)) {
            _formState.value = FormState(emailError = R.string.invalid_email)
        } else if (!isPasswordValid(password)) {
            _formState.value = FormState(passwordError = R.string.invalid_password)
        } else if (!_isLoginPage.value!! && !isPasswordRepeatValid(password, passwordRepeat)) {
            _formState.value = FormState(passwordRepeatError = R.string.invalid_password_repeat)
        } else {
            _formState.value = FormState(isDataValid = true)
        }
    }
    
    private fun isEmailValid(email: String): Boolean {
        return Patterns.EMAIL_ADDRESS.matcher(email).matches()
    }

    private fun isPasswordValid(password: String): Boolean {
        return password.length > 5
    }

    private fun isPasswordRepeatValid(password: String, repeatPassword: String): Boolean {
        return password == repeatPassword
    }
}

data class FormState(
    val emailError: Int? = null,
    val passwordError: Int? = null,
    val passwordRepeatError: Int? = null,
    val isDataValid: Boolean = false
)

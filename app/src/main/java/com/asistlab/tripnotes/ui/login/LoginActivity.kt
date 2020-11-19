package com.asistlab.tripnotes.ui.login

import android.content.Intent
import android.os.Bundle
import android.widget.Toast
import androidx.activity.viewModels
import androidx.appcompat.app.AppCompatActivity
import androidx.lifecycle.Observer
import com.asistlab.tripnotes.*
import com.asistlab.tripnotes.databinding.ActivityLoginBinding
import com.asistlab.tripnotes.other.afterTextChanged
import com.asistlab.tripnotes.other.gone
import com.asistlab.tripnotes.other.hide
import com.asistlab.tripnotes.other.show
import dagger.hilt.android.AndroidEntryPoint

/**
 * @author EpicDima
 */
@AndroidEntryPoint
class LoginActivity : AppCompatActivity() {

    private lateinit var binding: ActivityLoginBinding
    private val viewModel: LoginViewModel by viewModels()

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityLoginBinding.inflate(layoutInflater)
        setContentView(binding.root)
        viewModel.isLogged.observe(this) {
            if (it) {
                startActivity(Intent(this@LoginActivity, MainActivity::class.java))
                finish()
            }
        }

        setUiObservers()
        setFormObserver()
        setResultObserver()
        setTextChangedListeners()
        setClickListeners()
    }

    private fun setUiObservers() {
        viewModel.loading.observe(this) {
            binding.loading.run {
                if (it) show() else gone()
            }
        }

        viewModel.isLoginPage.observe(this) {
            binding.run {
                if (it) {
                    passwordRepeat.hide()
                    passwordRepeat.text = null
                    signIn.show()
                    signUp.gone()
                    actionChange.setText(R.string.action_to_sign_up)
                } else {
                    passwordRepeat.show()
                    signIn.gone()
                    signUp.show()
                    actionChange.setText(R.string.action_to_sign_in)
                }
            }
        }
    }

    private fun setFormObserver() {
        viewModel.formState.observe(this, {
            val state = it ?: return@observe
            if (viewModel.isLoginPage.value!!) {
                binding.signIn.isEnabled = state.isDataValid
            } else {
                binding.signUp.isEnabled = state.isDataValid
            }

            if (state.emailError != null) {
                binding.email.error = getString(state.emailError)
            }
            if (state.passwordError != null) {
                binding.password.error = getString(state.passwordError)
            }
            if (state.passwordRepeatError != null) {
                binding.passwordRepeat.error = getString(state.passwordRepeatError)
            }
        })
    }

    private fun setResultObserver() {
        viewModel.error.observe(this, {
            if (it != null) {
                Toast.makeText(applicationContext, it, Toast.LENGTH_LONG).show()
            }
        })
    }

    private fun setTextChangedListeners() {
        val onFormChange: (String) -> Unit = {
            viewModel.formDataChanged(
                binding.email.text.toString(),
                binding.password.text.toString(),
                binding.passwordRepeat.text.toString(),
            )
        }
        binding.email.afterTextChanged(onFormChange)
        binding.password.afterTextChanged(onFormChange)
        binding.passwordRepeat.afterTextChanged(onFormChange)
    }

    private fun setClickListeners() {
        binding.signIn.setOnClickListener {
            viewModel.signIn(binding.email.text.toString(), binding.password.text.toString())
        }
        binding.signUp.setOnClickListener {
            viewModel.signUp(binding.email.text.toString(), binding.password.text.toString())
        }
        binding.actionChange.setOnClickListener {
            viewModel.isLoginPage.value = !viewModel.isLoginPage.value!!
        }
    }
}

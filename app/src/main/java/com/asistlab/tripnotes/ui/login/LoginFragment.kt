package com.asistlab.tripnotes.ui.login

import android.content.Intent
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Toast
import androidx.fragment.app.Fragment
import androidx.fragment.app.viewModels
import com.asistlab.tripnotes.MainActivity
import com.asistlab.tripnotes.R
import com.asistlab.tripnotes.databinding.FragmentLoginBinding
import com.asistlab.tripnotes.other.afterTextChanged
import com.asistlab.tripnotes.other.gone
import com.asistlab.tripnotes.other.hide
import com.asistlab.tripnotes.other.show
import dagger.hilt.android.AndroidEntryPoint

/**
 * @author EpicDima
 */
@AndroidEntryPoint
class LoginFragment : Fragment() {

    companion object {
        fun newInstance() = LoginFragment()
    }

    private lateinit var binding: FragmentLoginBinding
    private val viewModel: LoginViewModel by viewModels()

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        binding = FragmentLoginBinding.inflate(inflater)
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        viewModel.isLogged.observe(viewLifecycleOwner) {
            if (it) {
                startActivity(Intent(requireContext(), MainActivity::class.java))
                requireActivity().finish()
            }
        }

        setUiObservers()
        setFormObserver()
        setResultObserver()
        setTextChangedListeners()
        setClickListeners()
    }

    private fun setUiObservers() {
        viewModel.loading.observe(viewLifecycleOwner) {
            binding.loading.run {
                if (it) show() else gone()
            }
        }

        viewModel.isLoginPage.observe(viewLifecycleOwner) {
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
        viewModel.formState.observe(viewLifecycleOwner, {
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
        viewModel.error.observe(viewLifecycleOwner, {
            if (it != null) {
                Toast.makeText(requireContext(), it, Toast.LENGTH_LONG).show()
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
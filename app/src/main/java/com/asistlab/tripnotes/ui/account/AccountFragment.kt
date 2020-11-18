package com.asistlab.tripnotes.ui.account

import android.content.Intent
import androidx.lifecycle.ViewModelProvider
import android.os.Bundle
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.appcompat.app.AppCompatActivity
import androidx.fragment.app.viewModels
import com.asistlab.tripnotes.R
import com.asistlab.tripnotes.databinding.FragmentAccountBinding
import com.asistlab.tripnotes.ui.login.LoginActivity
import com.google.android.material.dialog.MaterialAlertDialogBuilder
import dagger.hilt.android.AndroidEntryPoint

/**
 * @author EpicDima
 */
@AndroidEntryPoint
class AccountFragment : Fragment() {

    companion object {
        fun newInstance() = AccountFragment()
    }

    private lateinit var binding: FragmentAccountBinding
    private val viewModel: AccountViewModel by viewModels()

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        binding = FragmentAccountBinding.inflate(inflater)
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        viewModel.user.observe(viewLifecycleOwner) {
            val user = it ?: return@observe
            if (user.email != null) {
                binding.email.text = user.email
            }
        }

        binding.signOut.setOnClickListener {
            openConfirmationDialog()
        }
    }

    private fun openConfirmationDialog() {
        MaterialAlertDialogBuilder(requireContext()).apply {
            setTitle(getString(R.string.sign_out_dialog_title))
            setMessage(R.string.sign_out_dialog_message)
            setPositiveButton(getString(R.string.yes)) { _, _ -> signOut() }
            setNegativeButton(getString(R.string.cancel)) { dialog, _ -> dialog.cancel() }
        }.create().show()
    }

    private fun signOut() {
        viewModel.signOut()
        startActivity(Intent(requireActivity(), LoginActivity::class.java))
        (requireActivity() as AppCompatActivity).finish()
    }
}
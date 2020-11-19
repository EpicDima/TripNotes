package com.asistlab.tripnotes.ui.statistics

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.TextView
import androidx.fragment.app.Fragment
import androidx.fragment.app.viewModels
import androidx.lifecycle.Observer
import androidx.lifecycle.ViewModelProvider
import com.asistlab.tripnotes.R
import com.asistlab.tripnotes.databinding.FragmentAccountBinding
import com.asistlab.tripnotes.databinding.FragmentStatisticsBinding
import com.asistlab.tripnotes.ui.account.AccountViewModel
import dagger.hilt.android.AndroidEntryPoint

/**
 * @author EpicDima
 */
@AndroidEntryPoint
class StatisticsFragment : Fragment() {

    private val viewModel: StatisticsViewModel by viewModels()
    private lateinit var binding: FragmentStatisticsBinding

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        binding = FragmentStatisticsBinding.inflate(inflater)
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
    }

}
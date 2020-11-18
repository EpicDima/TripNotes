package com.asistlab.tripnotes.ui.trips

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.TextView
import androidx.fragment.app.Fragment
import androidx.fragment.app.viewModels
import androidx.lifecycle.ViewModelProvider
import androidx.navigation.Navigation
import com.asistlab.tripnotes.R
import dagger.hilt.android.AndroidEntryPoint

/**
 * @author EpicDima
 */
@AndroidEntryPoint
class TripsFragment : Fragment() {

    private val viewModel: TripsViewModel by viewModels()

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        val root = inflater.inflate(R.layout.fragment_trips, container, false)
        root.setOnClickListener {
            Navigation.findNavController(it)
                .navigate(R.id.action_navigation_trips_to_editTripFragment)
        }
        return root
    }
}
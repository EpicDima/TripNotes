package com.asistlab.tripnotes.ui

import androidx.lifecycle.ViewModelProvider
import android.os.Bundle
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import com.asistlab.tripnotes.R

/**
 * @author EpicDima
 */
class EditTripFragment : Fragment() {

    companion object {
        fun newInstance() = EditTripFragment()
    }

    private lateinit var viewModel: EditTripViewModel

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        return inflater.inflate(R.layout.edit_trip_fragment, container, false)
    }

    override fun onActivityCreated(savedInstanceState: Bundle?) {
        super.onActivityCreated(savedInstanceState)
        viewModel = ViewModelProvider(this).get(EditTripViewModel::class.java)
    }

}
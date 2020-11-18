package com.asistlab.tripnotes.ui.edit

import android.app.Activity.RESULT_OK
import android.content.Intent
import android.os.Bundle
import android.util.Log
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.viewModels
import com.asistlab.tripnotes.R
import com.asistlab.tripnotes.ui.ChoosePointActivity
import com.google.android.gms.maps.model.LatLng
import dagger.hilt.android.AndroidEntryPoint

/**
 * @author EpicDima
 */
@AndroidEntryPoint
class EditTripFragment : Fragment() {

    companion object {
        const val CHOOSE_LOCATION_CODE = 4763

        fun newInstance() = EditTripFragment()
    }

    private val viewModel: EditTripViewModel by viewModels()

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        val inflate = inflater.inflate(R.layout.fragment_edit_trip, container, false)
        inflate.setOnClickListener {
            startActivityForResult(Intent(requireContext(), ChoosePointActivity::class.java),
                CHOOSE_LOCATION_CODE)
        }
        return inflate
    }

    override fun onActivityCreated(savedInstanceState: Bundle?) {
        super.onActivityCreated(savedInstanceState)
    }

    override fun onActivityResult(requestCode: Int, resultCode: Int, data: Intent?) {
        if (requestCode == CHOOSE_LOCATION_CODE) {
            if (resultCode == RESULT_OK) {
                Log.d("MAIN", data!!.getParcelableExtra<LatLng>(ChoosePointActivity.LOCATION_KEY).toString())
                Log.d("MAIN", data.getStringExtra(ChoosePointActivity.ADDRESS_KEY).toString())
            }
        }
    }

}
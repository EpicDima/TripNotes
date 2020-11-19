package com.asistlab.tripnotes.ui.history

import android.content.Intent
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.Fragment
import androidx.fragment.app.viewModels
import androidx.recyclerview.widget.LinearLayoutManager
import com.asistlab.tripnotes.R
import com.asistlab.tripnotes.data.model.Trip
import com.asistlab.tripnotes.ui.other.TripsAdapter
import com.asistlab.tripnotes.ui.trip.TripActivity
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.storage.StorageReference
import dagger.hilt.android.AndroidEntryPoint
import kotlinx.android.synthetic.main.fragment_history.*
import javax.inject.Inject

/**
 * @author EpicDima
 */
@AndroidEntryPoint
class HistoryFragment : Fragment(), TripsAdapter.OnItemClickListener {

    @Inject
    lateinit var storage: StorageReference

    @Inject
    lateinit var auth: FirebaseAuth

    private val viewModel: HistoryViewModel by viewModels()

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        return inflater.inflate(R.layout.fragment_history, container, false)
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        val adapter = HistoryTripsAdapter(storage, auth.currentUser!!.uid, this)
        recycler_view.adapter = adapter
        recycler_view.layoutManager = LinearLayoutManager(context)
        viewModel.trips.observe(viewLifecycleOwner) {
            adapter.submitList(it)
        }
    }

    override fun onItemClicked(trip: Trip) {
        val intent = Intent(requireContext(), TripActivity::class.java)
        intent.putExtra(TripActivity.TRIP_ID_KEY, trip.id)
        startActivity(intent)
    }
}
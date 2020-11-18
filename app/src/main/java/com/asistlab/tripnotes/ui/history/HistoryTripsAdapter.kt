package com.asistlab.tripnotes.ui.history

import android.view.LayoutInflater
import android.view.ViewGroup
import com.asistlab.tripnotes.data.model.Trip
import com.asistlab.tripnotes.databinding.ItemTripBinding
import com.asistlab.tripnotes.ui.TripsAdapter

/**
 * @author EpicDima
 */
class HistoryTripsAdapter(
    userId: String,
    onItemClickListener: OnItemClickListener
) : TripsAdapter<HistoryTripsAdapter.HistoryTripViewHolder>(userId, onItemClickListener) {

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): HistoryTripViewHolder {
        val inflater = LayoutInflater.from(parent.context)
        val binding = ItemTripBinding.inflate(inflater, parent, false)
        return HistoryTripViewHolder(binding)
    }

    class HistoryTripViewHolder(binding: ItemTripBinding) : TripsAdapter.TripViewHolder(binding) {
        override fun bind(trip: Trip) {
//            binding.image =
        }
    }
}
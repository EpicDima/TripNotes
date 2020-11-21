package com.asistlab.tripnotes.ui.history

import com.asistlab.tripnotes.R
import com.asistlab.tripnotes.data.model.Trip
import com.asistlab.tripnotes.databinding.ItemTripBinding
import com.asistlab.tripnotes.other.gone
import com.asistlab.tripnotes.other.show
import com.asistlab.tripnotes.ui.common.TripsAdapter
import com.google.firebase.storage.StorageReference

/**
 * @author EpicDima
 */
class HistoryTripsAdapter(
    storage: StorageReference,
    userId: String,
    onItemClickListener: OnItemClickListener
) : TripsAdapter<HistoryTripsAdapter.HistoryTripViewHolder>(storage, userId, onItemClickListener) {

    override fun createViewHolder(
        binding: ItemTripBinding,
        storage: StorageReference,
        userId: String
    ): HistoryTripViewHolder {
        return HistoryTripViewHolder(binding, storage, userId)
    }

    class HistoryTripViewHolder(
        binding: ItemTripBinding,
        storage: StorageReference,
        userId: String
    ) : TripsAdapter.TripViewHolder(binding, storage, userId) {

        override fun bind(trip: Trip) {
            super.bind(trip)
            binding.apply {
                if (trip.done) {
                    status.setText(R.string.done)
                    status.show()
                } else {
                    status.gone()
                }
            }
        }
    }
}
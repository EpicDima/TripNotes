package com.asistlab.tripnotes.ui.trips

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
class MyTripsAdapter(
    storage: StorageReference,
    userId: String,
    onItemClickListener: OnItemClickListener
) : TripsAdapter<MyTripsAdapter.MyTripViewHolder>(storage, userId, onItemClickListener) {

    override fun createViewHolder(
        binding: ItemTripBinding,
        storage: StorageReference,
        userId: String
    ): MyTripViewHolder {
        return MyTripViewHolder(binding, storage, userId)
    }


    class MyTripViewHolder(
        binding: ItemTripBinding,
        storage: StorageReference,
        userId: String
    ) : TripsAdapter.TripViewHolder(binding, storage, userId) {

        override fun bind(trip: Trip) {
            super.bind(trip)
            binding.apply {
                if (trip.isStarted()) {
                    val remaining = trip.calculateRemainingTimeInDays()
                    if (remaining > 0) {
                        remainingTime.text = remaining.toString()
                    } else {
                        remainingTime.setText(R.string.remaining_time_less_than_day)
                    }
                    status.setText(R.string.active)
                    status.show()
                    remainingTime.show()
                    remainingTimeLabel.show()
                } else {
                    status.gone()
                    remainingTime.gone()
                    remainingTimeLabel.gone()
                }
            }
        }
    }
}
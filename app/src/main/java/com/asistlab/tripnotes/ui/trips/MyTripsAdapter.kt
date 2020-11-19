package com.asistlab.tripnotes.ui.trips

import android.view.LayoutInflater
import android.view.ViewGroup
import com.asistlab.tripnotes.R
import com.asistlab.tripnotes.data.model.Trip
import com.asistlab.tripnotes.databinding.ItemTripBinding
import com.asistlab.tripnotes.other.getImageName
import com.asistlab.tripnotes.other.gone
import com.asistlab.tripnotes.other.show
import com.asistlab.tripnotes.ui.other.TripsAdapter
import com.bumptech.glide.Glide
import com.google.firebase.storage.StorageReference

/**
 * @author EpicDima
 */
class MyTripsAdapter(
    storage: StorageReference,
    userId: String,
    onItemClickListener: OnItemClickListener
) : TripsAdapter<MyTripsAdapter.MyTripViewHolder>(storage, userId, onItemClickListener) {

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): MyTripViewHolder {
        val inflater = LayoutInflater.from(parent.context)
        val binding = ItemTripBinding.inflate(inflater, parent, false)
        return MyTripViewHolder(binding, storage, userId)
    }

    class MyTripViewHolder(
        binding: ItemTripBinding,
        private val storage: StorageReference,
        private val userId: String
    ) : TripsAdapter.TripViewHolder(binding) {
        override fun bind(trip: Trip) {
            Glide.with(binding.image)
                .load(storage.child(getImageName(userId, trip)))
                .placeholder(R.drawable.ic_default_trip_image_24dp)
                .error(R.drawable.ic_default_trip_image_24dp)
                .into(binding.image)
            binding.name.text = trip.name
            binding.startTime.text = trip.startDateToString()
            binding.endTime.text = trip.endDateToString()
            if (trip.isStarted()) {
                val remaining = trip.calculateRemainingTimeInDays()
                if (remaining > 0) {
                    binding.remainingTime.text = remaining.toString()
                } else {
                    binding.remainingTime.setText(R.string.remaining_time_less_than_day)
                }
                binding.active.show()
                binding.remainingTime.show()
                binding.remainingTimeLabel.show()
            } else {
                binding.active.gone()
                binding.remainingTime.gone()
                binding.remainingTimeLabel.gone()
            }
        }
    }
}
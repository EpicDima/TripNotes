package com.asistlab.tripnotes.ui.history

import android.view.LayoutInflater
import android.view.ViewGroup
import com.asistlab.tripnotes.R
import com.asistlab.tripnotes.data.model.Trip
import com.asistlab.tripnotes.databinding.ItemTripBinding
import com.asistlab.tripnotes.other.getImageName
import com.asistlab.tripnotes.ui.other.TripsAdapter
import com.bumptech.glide.Glide
import com.google.firebase.storage.StorageReference

/**
 * @author EpicDima
 */
class HistoryTripsAdapter(
    storage: StorageReference,
    userId: String,
    onItemClickListener: OnItemClickListener
) : TripsAdapter<HistoryTripsAdapter.HistoryTripViewHolder>(storage, userId, onItemClickListener) {

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): HistoryTripViewHolder {
        val inflater = LayoutInflater.from(parent.context)
        val binding = ItemTripBinding.inflate(inflater, parent, false)
        return HistoryTripViewHolder(binding, storage, userId)
    }

    class HistoryTripViewHolder(
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
        }
    }
}
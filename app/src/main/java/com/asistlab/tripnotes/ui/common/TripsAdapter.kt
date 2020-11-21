package com.asistlab.tripnotes.ui.common

import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.recyclerview.widget.DiffUtil
import androidx.recyclerview.widget.ListAdapter
import androidx.recyclerview.widget.RecyclerView
import com.asistlab.tripnotes.R
import com.asistlab.tripnotes.data.model.Trip
import com.asistlab.tripnotes.databinding.ItemTripBinding
import com.asistlab.tripnotes.other.getImageName
import com.bumptech.glide.Glide
import com.bumptech.glide.load.engine.DiskCacheStrategy
import com.google.firebase.storage.StorageReference

/**
 * @author EpicDima
 */
abstract class TripsAdapter<VH : TripsAdapter.TripViewHolder>(
    private val storage: StorageReference,
    private val userId: String,
    private val onItemClickListener: OnItemClickListener
) : ListAdapter<Trip, VH>(DIFF_CALLBACK) {

    companion object {
        private val DIFF_CALLBACK = object : DiffUtil.ItemCallback<Trip>() {
            override fun areItemsTheSame(oldItem: Trip, newItem: Trip): Boolean =
                oldItem.id == newItem.id

            override fun areContentsTheSame(oldItem: Trip, newItem: Trip): Boolean =
                oldItem == newItem
        }
    }

    final override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): VH {
        val inflater = LayoutInflater.from(parent.context)
        val binding = ItemTripBinding.inflate(inflater, parent, false)
        return createViewHolder(binding, storage, userId)
    }

    final override fun onBindViewHolder(holder: VH, position: Int) {
        val trip = getItem(position)
        holder.bind(trip)
        holder.setListener(trip, onItemClickListener)
    }

    abstract fun createViewHolder(
        binding: ItemTripBinding,
        storage: StorageReference,
        userId: String
    ): VH


    interface OnItemClickListener {
        fun onItemClicked(trip: Trip)
    }


    abstract class TripViewHolder(
        protected val binding: ItemTripBinding,
        private val storage: StorageReference,
        private val userId: String
    ) :
        RecyclerView.ViewHolder(binding.root) {

        open fun bind(trip: Trip) {
            Glide.with(binding.image)
                .load(storage.child(getImageName(userId, trip)))
                .diskCacheStrategy(DiskCacheStrategy.NONE)
                .skipMemoryCache(true)
                .placeholder(R.drawable.ic_default_trip_image_24dp)
                .error(R.drawable.ic_default_trip_image_24dp)
                .into(binding.image)
            binding.name.text = trip.name
            binding.startTime.text = trip.startDateToString()
            binding.endTime.text = trip.endDateToString()
        }

        fun setListener(trip: Trip, listener: OnItemClickListener) {
            itemView.setOnClickListener {
                listener.onItemClicked(trip)
            }
            binding.executePendingBindings()
        }
    }
}
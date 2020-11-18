package com.asistlab.tripnotes.ui

import androidx.recyclerview.widget.DiffUtil
import androidx.recyclerview.widget.ListAdapter
import androidx.recyclerview.widget.RecyclerView
import com.asistlab.tripnotes.data.model.Trip
import com.asistlab.tripnotes.databinding.ItemTripBinding
import com.bumptech.glide.Glide

/**
 * @author EpicDima
 */
abstract class TripsAdapter<VH : TripsAdapter.TripViewHolder>(
    protected val userId: String,
    protected val onItemClickListener: OnItemClickListener
) : ListAdapter<Trip, VH>(DIFF_CALLBACK) {

    companion object {
        private val DIFF_CALLBACK = object : DiffUtil.ItemCallback<Trip>() {
            override fun areItemsTheSame(oldItem: Trip, newItem: Trip): Boolean =
                oldItem.id == newItem.id

            override fun areContentsTheSame(oldItem: Trip, newItem: Trip): Boolean =
                oldItem == newItem
        }
    }

    final override fun onBindViewHolder(holder: VH, position: Int) {
        val trip = getItem(position)
        holder.bind(trip)
        holder.setListener(trip, onItemClickListener)
    }


    interface OnItemClickListener {
        fun onItemClicked(trip: Trip)
    }


    abstract class TripViewHolder(protected val binding: ItemTripBinding) :
        RecyclerView.ViewHolder(binding.root) {

        open fun bind(trip: Trip) {
//            binding.image = Glide().
        }

        fun setListener(trip: Trip, listener: OnItemClickListener) {
            itemView.setOnClickListener {
                listener.onItemClicked(trip)
            }
            binding.executePendingBindings()
        }
    }
}
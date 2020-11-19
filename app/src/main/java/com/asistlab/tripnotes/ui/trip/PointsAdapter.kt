package com.asistlab.tripnotes.ui.trip

import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.recyclerview.widget.DiffUtil
import androidx.recyclerview.widget.ListAdapter
import androidx.recyclerview.widget.RecyclerView
import com.asistlab.tripnotes.databinding.ItemPointBinding

/**
 * @author EpicDima
 */
class PointsAdapter : ListAdapter<TripViewModel.Point, PointsAdapter.PointViewHolder>(DIFF_CALLBACK) {

    companion object {
        private val DIFF_CALLBACK = object : DiffUtil.ItemCallback<TripViewModel.Point>() {
            override fun areItemsTheSame(oldItem: TripViewModel.Point, newItem: TripViewModel.Point): Boolean
                    = oldItem == newItem

            override fun areContentsTheSame(oldItem: TripViewModel.Point, newItem: TripViewModel.Point): Boolean
                    = oldItem == newItem
        }
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): PointViewHolder {
        val inflater = LayoutInflater.from(parent.context)
        val binding = ItemPointBinding.inflate(inflater, parent, false)
        return PointViewHolder(binding)
    }

    override fun onBindViewHolder(holder: PointViewHolder, position: Int) {
        holder.bind(getItem(position))
    }

    class PointViewHolder(
        private val binding: ItemPointBinding
    ) : RecyclerView.ViewHolder(binding.root) {

        fun bind(point: TripViewModel.Point) {
            binding.address.text = point.address
            binding.location.text = String.format("(%.5f; %.5f)",
                point.location.latitude, point.location.longitude)
            binding.executePendingBindings()
        }
    }
}
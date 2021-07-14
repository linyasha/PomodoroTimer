package com.example.pomodorotimer.adapters

import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.recyclerview.widget.DiffUtil
import androidx.recyclerview.widget.ListAdapter
import com.example.pomodorotimer.MainActivity
import com.example.pomodorotimer.model.StopWatchModel
import com.example.pomodorotimer.viewHolder.StopWatchViewHolder
import com.example.pomodorotimer.databinding.ViewHolderRecycleBinding

class StopWatchAdapter : ListAdapter<StopWatchModel, StopWatchViewHolder>(itemComparator) {

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): StopWatchViewHolder {
        val layoutInflater = LayoutInflater.from(parent.context)
        val binding = ViewHolderRecycleBinding.inflate(layoutInflater, parent, false)
        return StopWatchViewHolder(binding, parent.context as MainActivity)
    }

    override fun onBindViewHolder(holder: StopWatchViewHolder, position: Int) {
        holder.bind(getItem(position))
    }

    private companion object {

        private val itemComparator = object : DiffUtil.ItemCallback<StopWatchModel>() {

            override fun areItemsTheSame(oldItem: StopWatchModel, newItem: StopWatchModel): Boolean {
                return oldItem.id == newItem.id
            }

            override fun areContentsTheSame(oldItem: StopWatchModel, newItem: StopWatchModel): Boolean {
                return oldItem.currentMs == newItem.currentMs &&
                        oldItem.isStarted == newItem.isStarted
            }
        }
    }
}
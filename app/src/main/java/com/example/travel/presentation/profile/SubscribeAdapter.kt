package com.example.travel.presentation.profile

import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.recyclerview.widget.DiffUtil
import androidx.recyclerview.widget.ListAdapter
import androidx.recyclerview.widget.RecyclerView
import com.example.travel.R
import com.example.travel.databinding.SubscribeItemBinding
import com.example.travel.domain.model.TypeSubModel
import com.example.travel.domain.model.UserSubscribeAdapterModel
import java.text.SimpleDateFormat
import java.util.Calendar
import java.util.Date
import java.util.Locale

class SubscribeListAdapter :
    ListAdapter<UserSubscribeAdapterModel, SubscribeListAdapter.SubscribeListViewHolder>(callback) {

    class SubscribeListViewHolder(val binding: SubscribeItemBinding) :
        RecyclerView.ViewHolder(binding.root)

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): SubscribeListViewHolder {
        val binding =
            SubscribeItemBinding.inflate(LayoutInflater.from(parent.context), parent, false)
        return SubscribeListViewHolder(binding)
    }

    override fun onBindViewHolder(holder: SubscribeListViewHolder, position: Int) {

        val typeSubItem = getItem(position)
        val format = SimpleDateFormat("dd.MM.yyyy", Locale.getDefault())
        val dateString = format.format(typeSubItem.date_start)
        val dateFormatOriginal = SimpleDateFormat("dd.MM.yyyy", Locale.getDefault())
        val date = dateFormatOriginal.parse(dateString)
        val calendar = Calendar.getInstance()
        calendar.time = date
        calendar.add(Calendar.DAY_OF_MONTH, typeSubItem.period - 1)
        val newDateString = dateFormatOriginal.format(calendar.time)

        holder.binding.tvCity.text = typeSubItem.city
        holder.binding.tvPeriod.text = typeSubItem.period.toString()
        holder.binding.tvInterval.text = "${format.format(typeSubItem.date_start)} - $newDateString"
    }
}

private val callback = object : DiffUtil.ItemCallback<UserSubscribeAdapterModel>() {
    override fun areItemsTheSame(
        oldItem: UserSubscribeAdapterModel,
        newItem: UserSubscribeAdapterModel
    ): Boolean {
        return oldItem.period == newItem.period
    }

    override fun areContentsTheSame(
        oldItem: UserSubscribeAdapterModel,
        newItem: UserSubscribeAdapterModel
    ): Boolean {
        return oldItem == newItem
    }
}

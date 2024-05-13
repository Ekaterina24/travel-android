package com.example.travel.presentation.calendar

import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.TextView
import androidx.recyclerview.widget.DiffUtil
import androidx.recyclerview.widget.ListAdapter
import androidx.recyclerview.widget.RecyclerView
import com.example.travel.R
import com.example.travel.databinding.PlaceItemBinding
import com.example.travel.domain.model.DayPlaceModel
import com.example.travel.domain.model.PlaceModel

class DayListByUserAdapter:
    ListAdapter<PlaceModel, DayListByUserAdapter.DayListByUserViewHolder>(callback),
    View.OnClickListener
{

    class DayListByUserViewHolder(val binding: PlaceItemBinding)
        : RecyclerView.ViewHolder(binding.root)

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): DayListByUserViewHolder {
        val binding = PlaceItemBinding.inflate(LayoutInflater.from(parent.context), parent, false)
        binding.root.setOnClickListener(this)
        binding.imageButton.setOnClickListener(this)
        return DayListByUserViewHolder(binding)
    }

    override fun onBindViewHolder(holder: DayListByUserViewHolder, position: Int) {
        val placeItem = getItem(position)
        holder.binding.tvName.text = placeItem.name
        holder.binding.tvType.text = placeItem.typePlace
    }

    override fun onClick(v: View) {
        val place = v.tag as PlaceModel
        when (v.id) {
            R.id.imageButton -> {
//                actionListener.onChoosePlace(place)
            }
        }
    }
}

val callback = object: DiffUtil.ItemCallback<PlaceModel>() {
    override fun areItemsTheSame(oldItem: PlaceModel, newItem: PlaceModel): Boolean {
        return oldItem.id == newItem.id
    }

    override fun areContentsTheSame(oldItem: PlaceModel, newItem: PlaceModel): Boolean {
        return oldItem == newItem
    }
}
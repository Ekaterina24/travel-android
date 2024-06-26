package com.example.travel.presentation.calendar

import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.recyclerview.widget.DiffUtil
import androidx.recyclerview.widget.ListAdapter
import androidx.recyclerview.widget.RecyclerView
import com.example.travel.R
import com.example.travel.databinding.PlaceItemBinding
import com.example.travel.databinding.PlaceItemCalendarBinding
import com.example.travel.domain.model.PlaceModel

interface PlaceActionListener {
    fun onChoosePlace(place: PlaceModel)
    fun deletePlaceByStringId(placeId: String)
}

class DayListByUserAdapter(
    private val actionListener: PlaceActionListener,
):
    ListAdapter<PlaceModel, DayListByUserAdapter.DayListByUserViewHolder>(callback),
    View.OnClickListener
{

    class DayListByUserViewHolder(val binding: PlaceItemCalendarBinding)
        : RecyclerView.ViewHolder(binding.root)

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): DayListByUserViewHolder {
        val binding = PlaceItemCalendarBinding.inflate(LayoutInflater.from(parent.context), parent, false)
        binding.root.setOnClickListener(this)
        binding.imageButton.setOnClickListener(this)
        binding.ibDelete.setOnClickListener(this)
        return DayListByUserViewHolder(binding)
    }

    override fun onBindViewHolder(holder: DayListByUserViewHolder, position: Int) {
        val placeItem = getItem(position)
//        holder.binding.imageView.load(placeItem.imageUrl)
        holder.binding.tvName.text = placeItem.name

//        holder.binding.tvName.text = "tr:${placeItem.tripId} pl:${placeItem.placeId}"
        holder.binding.tvType.text = placeItem.typePlace

        holder.binding.ibDelete.tag = placeItem



//        holder.itemView.tag = placeItem
//        holder.binding.imageButton.tag = placeItem
        holder.itemView.setOnClickListener {
            Log.d("MY_TAG", "onClick: item")
        }
    }

    override fun onClick(v: View) {
        val place = v.tag as PlaceModel
        when (v.id) {
            R.id.imageButton -> {
//                actionListener.onChoosePlace(place)
            }

            R.id.ibDelete -> {
                actionListener.deletePlaceByStringId(place.id)
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
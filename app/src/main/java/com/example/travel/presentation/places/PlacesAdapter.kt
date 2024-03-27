package com.example.travel.presentation.places

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
import com.example.travel.domain.model.PlaceModel

interface PlaceActionListener {
    fun onChoosePlace(place: PlaceModel)
}

class PlaceListAdapter(
    private val actionListener: PlaceActionListener,
):
    ListAdapter<PlaceModel, PlaceListAdapter.PlaceListViewHolder>(callback),
    View.OnClickListener
{

    class PlaceListViewHolder(val binding: PlaceItemBinding)
        : RecyclerView.ViewHolder(binding.root)

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): PlaceListViewHolder {
        val binding = PlaceItemBinding.inflate(LayoutInflater.from(parent.context), parent, false)
        binding.root.setOnClickListener(this)
        binding.imageButton.setOnClickListener(this)
        return PlaceListViewHolder(binding)
    }

    override fun onBindViewHolder(holder: PlaceListViewHolder, position: Int) {
        val placeItem = getItem(position)
//        holder.binding.imageView.load(placeItem.imageUrl)
        holder.binding.tvName.text = placeItem.name
        holder.binding.tvType.text = placeItem.typePlace
        holder.itemView.tag = placeItem
        holder.binding.imageButton.tag = placeItem
        holder.itemView.setOnClickListener {
            Log.d("MY_TAG", "onClick: item")
        }
    }

    override fun onClick(v: View) {
        val place = v.tag as PlaceModel
        when (v.id) {
            R.id.imageButton -> {
                actionListener.onChoosePlace(place)
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
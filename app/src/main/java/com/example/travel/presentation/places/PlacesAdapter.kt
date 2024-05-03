package com.example.travel.presentation.places

import android.content.Context
import android.content.Intent
import android.content.res.ColorStateList
import android.graphics.Color
import android.os.Bundle
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.TextView
import androidx.core.os.bundleOf
import androidx.navigation.Navigation.findNavController
import androidx.navigation.fragment.findNavController
import androidx.recyclerview.widget.DiffUtil
import androidx.recyclerview.widget.ListAdapter
import androidx.recyclerview.widget.RecyclerView
import com.example.travel.R
import com.example.travel.databinding.PlaceItemBinding
import com.example.travel.domain.model.PlaceModel

interface PlaceActionListener {
//    fun onChoosePlace(place: PlaceModel)

//    fun getPlaceId(genId: Long, isFavorite: Boolean)
    fun getPlaceId(genId: Long)
//    fun getPlaceId(id: String)
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
//        binding.root.setOnClickListener(this)
        binding.imageButton.setOnClickListener(this)
        binding.imageFavorite.setOnClickListener(this)
        return PlaceListViewHolder(binding)
    }

    override fun onBindViewHolder(holder: PlaceListViewHolder, position: Int) {
        val placeItem = getItem(position)
//        holder.binding.imageView.load(placeItem.imageUrl)
        holder.binding.tvName.text = placeItem.name
        holder.binding.tvType.text = placeItem.typePlace
        holder.binding.imageFavorite.imageTintList =
            when (placeItem.is_favourite) {
                true -> ColorStateList.valueOf(Color.RED)
                false -> ColorStateList.valueOf(Color.GRAY)
            }
        holder.binding.checkBox.isChecked = when(placeItem.is_visited) {
            true -> true
            false -> false
        }
//            ColorStateList.valueOf(Color.RED) //
        holder.itemView.tag = placeItem
        holder.binding.imageButton.tag = placeItem
        holder.binding.imageFavorite.tag = placeItem
//        holder.itemView.setOnClickListener {
//            Log.d("MY_TAG", "onClick: item")
//        }
        holder.itemView.setOnClickListener {
            val bundle = bundleOf("key" to placeItem.id)
//            actionListener.getPlaceId(placeItem.id)
//                getPlace.getPlaceId(placeItem.id)
//                val intent = Intent(context, PlaceDetailFragment::class.java)
//
//                intent.putExtra("key", bundle)
//                context.startActivity(intent)
        }
    }

    override fun onClick(v: View) {
        val place = v.tag as PlaceModel
        val position = currentList.indexOf(place)
        when (v.id) {
            R.id.imageButton -> {
//                actionListener.onChoosePlace(place)
            }
            R.id.imageFavorite -> {
                actionListener.getPlaceId(place.generatedId)
//                notifyItemChanged(position) // Обновляем элемент в RecyclerView
            }
        }
    }
}

private val callback = object: DiffUtil.ItemCallback<PlaceModel>() {
    override fun areItemsTheSame(oldItem: PlaceModel, newItem: PlaceModel): Boolean {
        return oldItem.id == newItem.id
    }

    override fun areContentsTheSame(oldItem: PlaceModel, newItem: PlaceModel): Boolean {
        return oldItem == newItem
    }
}

//class PlaceListAdapter(
////    private val actionListener: PlaceActionListener,
//    private val items: List<ListItem>,
//    private val context: Context
//) :
//    RecyclerView.Adapter<RecyclerView.ViewHolder>(),
//    View.OnClickListener {
//
//    class PlaceListViewHolder(val binding: PlaceItemBinding) : RecyclerView.ViewHolder(binding.root)
//
//    inner class PlaceViewHolder(itemView: View) : RecyclerView.ViewHolder(itemView) {
//        fun bind(item: ListItem.PlaceItem) {
//            val name = itemView.findViewById<TextView>(R.id.tvName)
//            name.text = item.place.name
//            val textView = itemView.findViewById<TextView>(R.id.tvAudio)
//            textView.text = item.audioList?.map {
//                it.placeId
//            }.toString()
//
//            itemView.setOnClickListener {
//                val bundle = bundleOf("key" to item.place.id)
//
//                Log.d("MY_TAG", "placeId: ${item.place.id}")
//                findNavController(itemView).navigate(
//                    R.id.action_mapFragment_to_placeDetailFragment, bundle
//                )
////                val intent = Intent(context, PlaceDetailFragment::class.java)
////
////                intent.putExtra("key", bundle)
////                context.startActivity(intent)
//            }
//        }
//    }
//
//
////    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): PlaceListViewHolder {
////        val binding = PlaceItemBinding.inflate(LayoutInflater.from(parent.context), parent, false)
////        binding.root.setOnClickListener(this)
////        binding.imageButton.setOnClickListener(this)
////        return PlaceListViewHolder(binding)
////    }
//
//    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): RecyclerView.ViewHolder {
//        return when (viewType) {
//            0 -> PlaceViewHolder(
//                LayoutInflater.from(parent.context)
//                    .inflate(R.layout.place_item, parent, false)
//            )
//
//            else -> throw IllegalArgumentException("Invalid view type")
//        }
//    }
//
//    override fun getItemViewType(position: Int): Int {
//        return when (items[position]) {
//            is ListItem.PlaceItem -> 0
//        }
//    }
//
//    override fun getItemCount() = items.size
//
//    override fun onBindViewHolder(holder: RecyclerView.ViewHolder, position: Int) {
//        when (val item = items[position]) {
//            is ListItem.PlaceItem -> (holder as PlaceViewHolder).bind(item)
//        }
//    }
//
////    override fun onBindViewHolder(holder: PlaceListViewHolder, position: Int) {
////        val placeItem = getItem(position)
//////        holder.binding.imageView.load(placeItem.imageUrl)
////        holder.binding.tvName.text = placeItem.name
////        holder.binding.tvType.text = placeItem.typePlace
////        holder.itemView.tag = placeItem
////        holder.binding.imageButton.tag = placeItem
////        holder.binding.tvAudio.text = placeItem.
////        holder.itemView.setOnClickListener {
////            Log.d("MY_TAG", "onClick: item")
////        }
////    }
//
//    override fun onClick(v: View) {
//        val place = v.tag as PlaceModel
//        when (v.id) {
////            R.id.imageButton -> {
////                actionListener.onChoosePlace(place)
////            }
//        }
//    }
//}
//
//val callback = object : DiffUtil.ItemCallback<PlaceModel>() {
//    override fun areItemsTheSame(oldItem: PlaceModel, newItem: PlaceModel): Boolean {
//        return oldItem.id == newItem.id
//    }
//
//    override fun areContentsTheSame(oldItem: PlaceModel, newItem: PlaceModel): Boolean {
//        return oldItem == newItem
//    }
//}
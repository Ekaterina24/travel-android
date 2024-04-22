package com.example.travel.presentation.places

import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.core.os.bundleOf
import androidx.recyclerview.widget.DiffUtil
import androidx.recyclerview.widget.ListAdapter
import androidx.recyclerview.widget.RecyclerView
import com.example.travel.R
import com.example.travel.databinding.CategoryItemBinding
import com.example.travel.domain.model.CategoryModel

interface CategoryActionListener {
    fun onChoosePlace(place: CategoryModel)

    fun getCategory(name: String)
}

class CategoryListAdapter(
    private val actionListener: CategoryActionListener,
):
    ListAdapter<CategoryModel, CategoryListAdapter.CategoryListViewHolder>(callback),
    View.OnClickListener
{

    class CategoryListViewHolder(val binding: CategoryItemBinding)
        : RecyclerView.ViewHolder(binding.root)

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): CategoryListViewHolder {
        val binding = CategoryItemBinding.inflate(LayoutInflater.from(parent.context), parent, false)
        binding.root.setOnClickListener(this)
        return CategoryListViewHolder(binding)
    }

    override fun onBindViewHolder(holder: CategoryListViewHolder, position: Int) {
        val categoryItem = getItem(position)
//        holder.binding.imageView.load(placeItem.imageUrl)
        holder.binding.tvCategory.text = categoryItem.category
        holder.itemView.tag = categoryItem
//        holder.itemView.setOnClickListener {
//            Log.d("MY_TAG", "onClick: item")
//        }
        holder.itemView.setOnClickListener {
            actionListener.getCategory(categoryItem.category)
//            val bundle = bundleOf("category" to categoryItem.category)
//            actionListener.getPlaceId(placeItem.id)
//                getPlace.getPlaceId(placeItem.id)
//                val intent = Intent(context, PlaceDetailFragment::class.java)
//
//                intent.putExtra("key", bundle)
//                context.startActivity(intent)
        }
    }

    override fun onClick(v: View) {
        val place = v.tag as CategoryModel
        when (v.id) {
            R.id.imageButton -> {
//                actionListener.onChoosePlace(place)
            }
        }
    }
}

private val callback = object: DiffUtil.ItemCallback<CategoryModel>() {
    override fun areItemsTheSame(oldItem: CategoryModel, newItem: CategoryModel): Boolean {
        return oldItem.category == newItem.category
    }

    override fun areContentsTheSame(oldItem: CategoryModel, newItem: CategoryModel): Boolean {
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
//        val place = v.tag as CategoryModel
//        when (v.id) {
////            R.id.imageButton -> {
////                actionListener.onChoosePlace(place)
////            }
//        }
//    }
//}
//
//val callback = object : DiffUtil.ItemCallback<CategoryModel>() {
//    override fun areItemsTheSame(oldItem: CategoryModel, newItem: CategoryModel): Boolean {
//        return oldItem.id == newItem.id
//    }
//
//    override fun areContentsTheSame(oldItem: CategoryModel, newItem: CategoryModel): Boolean {
//        return oldItem == newItem
//    }
//}
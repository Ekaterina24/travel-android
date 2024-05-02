package com.example.travel.presentation.weather

import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.core.os.bundleOf
import androidx.recyclerview.widget.DiffUtil
import androidx.recyclerview.widget.ListAdapter
import androidx.recyclerview.widget.RecyclerView
import com.example.travel.R
import com.example.travel.databinding.CategoryItemBinding
import com.example.travel.databinding.CategoryNewItemBinding
import com.example.travel.domain.model.CategoryModel
import com.example.travel.presentation.weather.GetStatus

class NewCategoryAdapter:
    ListAdapter<CategoryModel, NewCategoryAdapter.CategoryListViewHolder>(callback)
{

    class CategoryListViewHolder(val binding: CategoryNewItemBinding)
        : RecyclerView.ViewHolder(binding.root)

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): CategoryListViewHolder {
        val binding = CategoryNewItemBinding.inflate(LayoutInflater.from(parent.context), parent, false)
        return CategoryListViewHolder(binding)
    }

    override fun onBindViewHolder(holder: CategoryListViewHolder, position: Int) {
        val categoryItem = getItem(position)
        val img = when (categoryItem.category) {
            "attraction" -> R.drawable.ic_monument
            "adm_div" -> R.drawable.ic_tree
            "building" -> R.drawable.ic_museum
            "branch" -> R.drawable.ic_food
            else -> ""
        } as Int
        holder.binding.icCategory.setImageResource(img)
        holder.binding.tvCategory.text =
        when (categoryItem.category) {
                            "attraction" -> "Отличное время посмотреть достопримечательности!"
                            "adm_div" -> "Хороший повод погулять в парке!"
                            "building" -> "Самое время узнать что-то новое!"
                            "branch" -> "Время для теплых бесед в кафе!"
                            else -> ""
                        }
        holder.itemView.tag = categoryItem
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


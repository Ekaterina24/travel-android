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
import com.example.travel.presentation.weather.GetStatus

interface CategoryActionListener {
    fun onChoosePlace(place: CategoryModel)

    fun getCategory(name: String)
}

class CategoryListAdapter(
    private val actionListener: CategoryActionListener
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
                            "attraction" -> "Достопримечательности"
                            "adm_div" -> "Парки"
                            "building" -> "Музеи"
                            "branch" -> "Еда"
                            else -> ""
                        }
        holder.itemView.tag = categoryItem
        holder.itemView.setOnClickListener {
            actionListener.getCategory(categoryItem.category)
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

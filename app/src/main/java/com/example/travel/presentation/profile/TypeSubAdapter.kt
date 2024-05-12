package com.example.travel.presentation.profile

import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.recyclerview.widget.DiffUtil
import androidx.recyclerview.widget.ListAdapter
import androidx.recyclerview.widget.RecyclerView
import com.example.travel.R
import com.example.travel.databinding.TypeSubItemBinding
import com.example.travel.domain.model.CategoryModel
import com.example.travel.domain.model.TypeSubModel

interface TypeSubActionListener {
    fun onChooseTypeSub(typeSub: TypeSubModel)

//    fun getCategory(name: String)
}

class TypeSubListAdapter(
    private val actionListener: TypeSubActionListener
):
    ListAdapter<TypeSubModel, TypeSubListAdapter.TypeSubListViewHolder>(callback),
    View.OnClickListener
{

    class TypeSubListViewHolder(val binding: TypeSubItemBinding)
        : RecyclerView.ViewHolder(binding.root)

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): TypeSubListViewHolder {
        val binding = TypeSubItemBinding.inflate(LayoutInflater.from(parent.context), parent, false)
//        binding.root.setOnClickListener(this)
        binding.btnBuySub.setOnClickListener(this)
        return TypeSubListViewHolder(binding)
    }

    override fun onBindViewHolder(holder: TypeSubListViewHolder, position: Int) {
        val typeSubItem = getItem(position)
        holder.binding.tvPeriod.text = "Количество дней: ${typeSubItem.period}"
        holder.binding.tvPrice.text = "Цена: ${typeSubItem.price} ₽"
        holder.itemView.tag = typeSubItem
        holder.binding.btnBuySub.tag = typeSubItem
//        holder.itemView.setOnClickListener {
//            actionListener.getCategory(categoryItem.category)
//        }
    }

    override fun onClick(v: View) {
        val typeSub = v.tag as TypeSubModel
        when (v.id) {
            R.id.btnBuySub -> {
                actionListener.onChooseTypeSub(typeSub)
            }
        }
    }
}

private val callback = object: DiffUtil.ItemCallback<TypeSubModel>() {
    override fun areItemsTheSame(oldItem: TypeSubModel, newItem: TypeSubModel): Boolean {
        return oldItem.id == newItem.id
    }

    override fun areContentsTheSame(oldItem: TypeSubModel, newItem: TypeSubModel): Boolean {
        return oldItem == newItem
    }
}

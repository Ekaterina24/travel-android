package com.example.travel.presentation.places

import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.recyclerview.widget.DiffUtil
import androidx.recyclerview.widget.ListAdapter
import androidx.recyclerview.widget.RecyclerView
import com.example.travel.databinding.PlaceItemBinding
import com.example.travel.databinding.ReviewItemBinding
import com.example.travel.domain.model.review.GetReviewModel
import com.example.travel.domain.model.review.ReviewAdapterModel

class ReviewAdapter : ListAdapter<ReviewAdapterModel, ReviewAdapter.ReviewViewHolder>(callback) {
    class ReviewViewHolder(val binding: ReviewItemBinding) : RecyclerView.ViewHolder(binding.root)
    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ReviewViewHolder {
        val binding = ReviewItemBinding.inflate(LayoutInflater.from(parent.context), parent, false)
        return ReviewViewHolder(binding)
    }
    override fun onBindViewHolder(holder: ReviewViewHolder, position: Int) {
        val reviewItem = getItem(position)
        holder.binding.tvName.text = reviewItem.userName
        holder.binding.tvDate.text = reviewItem.date.toString()
        holder.binding.tvRating.text = reviewItem.rating.toString()
        holder.binding.tvReview.text = reviewItem.text
    }

}

private val callback = object : DiffUtil.ItemCallback<ReviewAdapterModel>() {
    override fun areItemsTheSame(oldItem: ReviewAdapterModel, newItem: ReviewAdapterModel): Boolean {
        return oldItem.userName == newItem.userName
    }

    override fun areContentsTheSame(oldItem: ReviewAdapterModel, newItem: ReviewAdapterModel): Boolean {
        return oldItem == newItem
    }
}

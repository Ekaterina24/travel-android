package com.example.travel.presentation.profile

import android.graphics.Color
import android.util.Log
import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.recyclerview.widget.DiffUtil
import androidx.recyclerview.widget.ListAdapter
import androidx.recyclerview.widget.RecyclerView
import com.example.travel.R
import com.example.travel.databinding.RatingItemBinding
import com.example.travel.domain.model.UserProfileModel

interface UserIdentification {
    fun isCurrentUser(userId: Long): Boolean
}
class RatingListAdapter(
    private val userIdentification: UserIdentification
) : ListAdapter<UserProfileModel, RatingListAdapter.RatingListViewHolder>(callback) {

    class RatingListViewHolder(val binding: RatingItemBinding) :
        RecyclerView.ViewHolder(binding.root)

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): RatingListViewHolder {
        val binding =
            RatingItemBinding.inflate(LayoutInflater.from(parent.context), parent, false)
        return RatingListViewHolder(binding)
    }

    override fun onBindViewHolder(holder: RatingListViewHolder, position: Int) {
        val ratingItem = getItem(position)
        holder.binding.tvPos.text = (position + 1).toString()
        holder.binding.tvName.text = ratingItem.username
        holder.binding.tvScore.text = ratingItem.scores.toString()
        holder.itemView.tag = ratingItem
        if (userIdentification.isCurrentUser(ratingItem.id)) {
            holder.binding.container.setBackgroundResource(R.color.green)
        }
    }
}

private val callback = object : DiffUtil.ItemCallback<UserProfileModel>() {
    override fun areItemsTheSame(
        oldItem: UserProfileModel,
        newItem: UserProfileModel
    ): Boolean {
        return oldItem.id == newItem.id
    }

    override fun areContentsTheSame(
        oldItem: UserProfileModel,
        newItem: UserProfileModel
    ): Boolean {
        return oldItem == newItem
    }
}

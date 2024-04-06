package com.example.travel.presentation.places

import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.recyclerview.widget.DiffUtil
import androidx.recyclerview.widget.ListAdapter
import androidx.recyclerview.widget.RecyclerView
import com.example.travel.R
import com.example.travel.databinding.AudioItemBinding
import com.example.travel.domain.model.AudioModel

interface AudioActionListener {
    fun playAudio(audio: AudioModel)
}

class AudioListByPlaceAdapter(
    private val actionListener: AudioActionListener,
):
    ListAdapter<AudioModel, AudioListByPlaceAdapter.AudioListByPlaceViewHolder>(callback2),
    View.OnClickListener
{

    class AudioListByPlaceViewHolder(val binding: AudioItemBinding)
        : RecyclerView.ViewHolder(binding.root)

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): AudioListByPlaceViewHolder {
        val binding = AudioItemBinding.inflate(LayoutInflater.from(parent.context), parent, false)
        binding.root.setOnClickListener(this)
        binding.fStartStop.setOnClickListener(this)
        return AudioListByPlaceViewHolder(binding)
    }

    override fun onBindViewHolder(holder: AudioListByPlaceViewHolder, position: Int) {
        val audioItem = getItem(position)
//        holder.binding.imageView.load(placeItem.imageUrl)
        holder.binding.tvAudio.text = "Гид ${position + 1}"
        holder.itemView.tag = audioItem
        holder.binding.fStartStop.tag = audioItem
    }

    override fun onClick(v: View) {
        val audio = v.tag as AudioModel
        when (v.id) {
            R.id.fStartStop -> {
                 actionListener.playAudio(audio)
            }
        }
    }
}

val callback2 = object: DiffUtil.ItemCallback<AudioModel>() {
    override fun areItemsTheSame(oldItem: AudioModel, newItem: AudioModel): Boolean {
        return oldItem.desc == newItem.desc
    }

    override fun areContentsTheSame(oldItem: AudioModel, newItem: AudioModel): Boolean {
        return oldItem == newItem
    }
}
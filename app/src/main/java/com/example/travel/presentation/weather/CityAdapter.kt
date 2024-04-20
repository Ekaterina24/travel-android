package com.example.travel.presentation.weather

import android.content.Intent
import android.content.SharedPreferences
import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.navigation.fragment.findNavController
import androidx.recyclerview.widget.AsyncListDiffer
import androidx.recyclerview.widget.DiffUtil
import androidx.recyclerview.widget.RecyclerView
import com.example.travel.R
import com.example.travel.data.network.dto.weather.CityResponseApi
import com.example.travel.databinding.CityViewholderBinding

interface OnItemClickListener {
    fun onItemClick(cityName: String?, lat: Double?, lon: Double?)
}
class CityAdapter(
    private val listener: OnItemClickListener,
//    private val pref: com.example.travel.data.local.prefs.SharedPreferences
) : RecyclerView.Adapter<CityAdapter.ViewHolder>() {

    private lateinit var binding: CityViewholderBinding

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): CityAdapter.ViewHolder {
        val inflater = LayoutInflater.from(parent.context)
        binding = CityViewholderBinding.inflate(inflater, parent, false)
        return ViewHolder()
    }

    override fun onBindViewHolder(holder: CityAdapter.ViewHolder, position: Int) {
        val binding = CityViewholderBinding.bind(holder.itemView)
        binding.cityTxt.text = differ.currentList[position].name
        binding.root.setOnClickListener {
//            val intent = Intent(binding.root.context, WeatherFragment::class.java)
//            intent.putExtra("lat", differ.currentList[position].lat)
//            pref.saveFloat("lat", differ.currentList[position].lat?.toFloat()?.let { it })
//            pref.saveFloat("lon", differ.currentList[position].lon?.toFloat() ?: 0f)
//            pref.save("name", differ.currentList[position].name ?: "")
//            intent.putExtra("lon", differ.currentList[position].lon)
//            intent.putExtra("name", differ.currentList[position].name)
            listener.onItemClick(differ.currentList[position].name, differ.currentList[position].lat, differ.currentList[position].lon)
//            binding.root.context.startActivity(intent
        }
    }

    override fun getItemCount() = differ.currentList.size

    inner class ViewHolder : RecyclerView.ViewHolder(binding.root)

    private val differCallback =
        object : DiffUtil.ItemCallback<CityResponseApi.CityResponseApiItem>() {
            override fun areItemsTheSame(
                oldItem: CityResponseApi.CityResponseApiItem,
                newItem: CityResponseApi.CityResponseApiItem
            ): Boolean {
                return oldItem == newItem
            }

            override fun areContentsTheSame(
                oldItem: CityResponseApi.CityResponseApiItem,
                newItem: CityResponseApi.CityResponseApiItem
            ): Boolean {
                return oldItem == newItem
            }

        }
    val differ = AsyncListDiffer(this, differCallback)
}
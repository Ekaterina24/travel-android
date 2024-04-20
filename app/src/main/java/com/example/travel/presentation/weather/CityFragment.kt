package com.example.travel.presentation.weather

import android.os.Bundle
import android.text.Editable
import android.text.TextWatcher
import android.util.Log
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.activity.viewModels
import androidx.core.os.bundleOf
import androidx.fragment.app.viewModels
import androidx.navigation.fragment.findNavController
import androidx.recyclerview.widget.LinearLayoutManager
import com.example.travel.R
import com.example.travel.data.local.prefs.SharedPreferences
import com.example.travel.data.network.dto.weather.CityResponseApi
import com.example.travel.databinding.FragmentCityBinding
import com.example.travel.databinding.FragmentWeatherBinding
import retrofit2.Call
import retrofit2.Callback
import retrofit2.Response

class CityFragment : Fragment(), OnItemClickListener {

    private var _binding: FragmentCityBinding? = null
    private val binding get() = _binding!!

    private val sharedPreferences: SharedPreferences =
        SharedPreferences(activity?.applicationContext)

    private val cityAdapter by lazy { CityAdapter(this) }
    private val cityViewModel: CityViewModel by viewModels()
    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        _binding = FragmentCityBinding.inflate(inflater, container, false)
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        binding.apply {
            cityEdit.addTextChangedListener(object : TextWatcher {
                override fun beforeTextChanged(p0: CharSequence?, p1: Int, p2: Int, p3: Int) {

                }

                override fun onTextChanged(p0: CharSequence?, p1: Int, p2: Int, p3: Int) {

                }

                override fun afterTextChanged(s: Editable?) {
                    progressBar2.visibility = View.VISIBLE
                    cityViewModel.loadCityList(s.toString(), 10).enqueue(object :
                        Callback<CityResponseApi> {
                        override fun onResponse(
                            call: Call<CityResponseApi>,
                            response: Response<CityResponseApi>
                        ) {
                            if (response.isSuccessful) {
                                val data = response.body()
                                data?.let {
                                    progressBar2.visibility = View.GONE
                                    cityAdapter.differ.submitList(it)
                                    cityView.apply {
                                        layoutManager = LinearLayoutManager(
                                            requireContext(),
                                            LinearLayoutManager.HORIZONTAL,
                                            false
                                        )
                                        adapter = cityAdapter
                                    }
                                }
                            }
                        }

                        override fun onFailure(call: Call<CityResponseApi>, t: Throwable) {

                        }

                    })
                }

            })
        }
    }

    override fun onItemClick(cityName: String?, lat: Double?, lon: Double?) {
        val arrayData = arrayListOf(cityName, lat.toString(), lon.toString())

        val bundle = Bundle().apply {
            putStringArrayList("cityName", arrayData)
        }
        findNavController().navigate(R.id.action_cityFragment_to_weatherFragment, bundle)
    }
}
package com.example.travel.presentation.weather

import android.os.Bundle
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.view.ViewOutlineProvider
import android.widget.Toast
import androidx.fragment.app.Fragment
import androidx.fragment.app.viewModels
import androidx.lifecycle.lifecycleScope
import androidx.recyclerview.widget.LinearLayoutManager
import com.example.travel.R
import com.example.travel.data.local.prefs.SharedPreferences
import com.example.travel.databinding.FragmentWeatherBinding
import com.example.travel.domain.model.CategoryModel
import com.example.travel.domain.model.PlaceModel
import com.example.travel.presentation.places.CategoryActionListener
import com.example.travel.presentation.places.CategoryListAdapter
import com.example.travel.presentation.places.PlacesViewModel
import com.example.travel.presentation.places.PlacesViewModelFactory
import com.example.weatherapp.model.CurrentResponseApi
import com.example.weatherapp.model.ForecastResponseApi
import com.github.matteobattilana.weather.PrecipType
import eightbitlab.com.blurview.RenderScriptBlur
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext
import retrofit2.Call
import retrofit2.Response
import java.util.Calendar

interface GetStatus {
    fun getWeatherStatus(): String
}

class WeatherFragment : Fragment(), GetStatus {

    private var _binding: FragmentWeatherBinding? = null
    private val binding get() = _binding!!

    private val weatherViewModel: WeatherViewModel by viewModels()
    private val calendar by lazy { Calendar.getInstance() }
    private val forecastAdapter by lazy { ForecastAdapter() }

    private val sharedPreferences: SharedPreferences by lazy {
        SharedPreferences(requireContext())
    }

    private val viewModel: PlacesViewModel by viewModels {
        PlacesViewModelFactory()
    }

    private var res = ""
    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        _binding = FragmentWeatherBinding.inflate(inflater, container, false)
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        binding.apply {
            val lat = sharedPreferences.getFloatValue("city_lat")?.toDouble()
            val lon = sharedPreferences.getFloatValue("city_lon")?.toDouble()

            val name = sharedPreferences.getStringValue("city")

            //current Temp
            cityTxt.text = name
            progressBar.visibility = View.VISIBLE
            if (lat != null) {
                if (lon != null) {
                    weatherViewModel.loadCurrentWeather(lat, lon, "metric").enqueue(object :
                        retrofit2.Callback<CurrentResponseApi> {
                        override fun onResponse(
                            call: Call<CurrentResponseApi>,
                            response: Response<CurrentResponseApi>
                        ) {
                            if (response.isSuccessful) {
                                val data = response.body()
                                Log.d("MY_TAG", "onResponse: $data")
                                progressBar.visibility = View.GONE
                                detailLayout.visibility = View.VISIBLE
                                data?.let {
                                    statusTxt.text = when (it.weather?.get(0)?.main ?: "-") {
                                        "Clouds" -> "Облачно"
                                        "Clear" -> "Солнечно"
                                        "Rain" -> "Дождливо"
                                        else -> ""
                                    }
                                    sharedPreferences.save("status", it.weather?.get(0)?.main ?: "-")
                                    res = it.weather?.get(0)?.main ?: "-"
                                    windTxt.text = it.wind?.speed?.let { Math.round(it).toString() } + "Km"
                                    humidityTxt.text = it.main?.humidity.toString() + "%"
                                    currentTempTxt.text = it.main?.temp?.let { Math.round(it).toString() } + "°"
                                    maxTempTxt.text = it.main?.tempMax?.let { Math.round(it).toString() } + "°"
                                    minTempTxt.text = it.main?.tempMin?.let { Math.round(it).toString() } + "°"

                                    val drawable = if (isNightNow()) R.drawable.night_bg
                                    else {
                                        setDynamicallyWallpaper(it.weather?.get(0)?.icon ?: "-")
                                    }
                                    bgImage.setImageResource(drawable)

                                    setEffectRainSnow(it.weather?.get(0)?.icon ?: "-")
                                }
                            }
                        }

                        override fun onFailure(call: Call<CurrentResponseApi>, t: Throwable) {
                            Log.d("MY_TAG", "onResponseE: $t")
                            Toast.makeText(requireContext(), t.toString(), Toast.LENGTH_SHORT).show()
                        }

                    })
                }
            }

            //settings Blur View
            var radius = 10f
            val decorView = requireActivity().window.decorView
            val rootView = (decorView.findViewById(android.R.id.content) as ViewGroup?)
            val windowBackground = decorView.background

            rootView?.let {
                blurView.setupWith(it, RenderScriptBlur(requireContext()))
                    .setFrameClearDrawable(windowBackground)
                    .setBlurRadius(radius)
                blurView.outlineProvider = ViewOutlineProvider.BACKGROUND
                blurView.clipToOutline = true
            }

            rootView?.let {
                blurViewRecommend.setupWith(it, RenderScriptBlur(requireContext()))
                    .setFrameClearDrawable(windowBackground)
                    .setBlurRadius(radius)
                blurViewRecommend.outlineProvider = ViewOutlineProvider.BACKGROUND
                blurViewRecommend.clipToOutline = true
            }

            //forecast temp
            if (lat != null) {
                if (lon != null) {
                    weatherViewModel.loadForecastWeather(lat, lon, "metric").enqueue(object : retrofit2.Callback<ForecastResponseApi> {
                        override fun onResponse(
                            call: Call<ForecastResponseApi>,
                            response: Response<ForecastResponseApi>
                        ) {
                            if (response.isSuccessful) {
                                val data = response.body()
                                blurView.visibility = View.VISIBLE
                                blurViewRecommend.visibility = View.VISIBLE

                                data?.let {
                                    forecastAdapter.differ.submitList(it.list)
                                    forecastView.apply {
                                        layoutManager = LinearLayoutManager(
                                            requireContext(), LinearLayoutManager.HORIZONTAL,
                                            false
                                        )
                                        adapter = forecastAdapter
                                    }
                                }
                            }
                        }

                        override fun onFailure(call: Call<ForecastResponseApi>, t: Throwable) {
                                
                        }

                    })
                }
            }
        }

        val rvCategoryAdapter = NewCategoryAdapter()
        binding.recommendationView.adapter = rvCategoryAdapter
        binding.recommendationView.layoutManager = LinearLayoutManager(
            requireContext(), LinearLayoutManager.VERTICAL, false
        )

        viewModel.getCategoryList()

        viewLifecycleOwner.lifecycleScope.launch(Dispatchers.IO) {
            viewModel.categoryList.collect { list ->
                withContext(Dispatchers.Main) {
                    val status = sharedPreferences.getStringValue("status")
                    val newList = mutableListOf<CategoryModel>()
                    list.map { item ->
                        if (status == "Clear") {
                            if (item.category == "adm_div" || item.category == "attraction" || item.category == "building") {
                                newList.add(item)
                            }
                        } else if (status == "Clouds") {
                            if (item.category == "attraction" || item.category == "branch" || item.category == "building") {
                                newList.add(item)
                            }
                        } else if (status == "Rain") {
                            if (item.category == "branch" || item.category == "building") {
                                newList.add(item)
                            }
                        }
                    }
                    rvCategoryAdapter.submitList(newList)
                }
            }
        }

    }

    private fun isNightNow(): Boolean {
        return calendar.get(Calendar.HOUR_OF_DAY) >= 18
    }

    private fun setDynamicallyWallpaper(icon: String): Int {
        return when(icon.dropLast(1)) {
            "01" -> {
                initWeatherView(PrecipType.CLEAR)
                R.drawable.sunny_bg
            }
            "02", "03", "04" -> {
                initWeatherView(PrecipType.CLEAR)
                R.drawable.cloudy_bg
            }
            "09", "10", "11" -> {
                initWeatherView(PrecipType.RAIN)
                R.drawable.rainy_bg
            }
            "13" -> {
                initWeatherView(PrecipType.SNOW)
                R.drawable.snow_bg
            }
            "50" -> {
                initWeatherView(PrecipType.CLEAR)
                R.drawable.haze_bg
            }
            else -> 0
        }
    }

    private fun setEffectRainSnow(icon: String) {
        when(icon.dropLast(1)) {
            "01" -> {
                initWeatherView(PrecipType.CLEAR)

            }
            "02", "03", "04" -> {
                initWeatherView(PrecipType.CLEAR)

            }
            "09", "10", "11" -> {
                initWeatherView(PrecipType.RAIN)

            }
            "13" -> {
                initWeatherView(PrecipType.SNOW)

            }
            "50" -> {
                initWeatherView(PrecipType.CLEAR)

            }
        }
    }

    private fun initWeatherView(type: PrecipType) {
        binding.weatherView.apply {
            setWeatherData(type)
            angle = -20
            emissionRate = 100.0f
        }
    }

    override fun getWeatherStatus(): String {
        return binding.statusTxt.text.toString()
    }
}
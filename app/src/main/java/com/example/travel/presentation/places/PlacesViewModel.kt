package com.example.travel.presentation.places

import androidx.lifecycle.ViewModel
import androidx.lifecycle.ViewModelProvider
import androidx.lifecycle.viewModelScope
import com.example.travel.data.network.CityRepositoryImpl
import com.example.travel.data.network.PlaceRepositoryImpl
import com.example.travel.domain.model.CityModel
import com.example.travel.domain.model.PlaceModel
import com.example.travel.domain.usecase.GetCityListUseCase
import com.example.travel.domain.usecase.GetPlacesUseCase
import kotlinx.coroutines.flow.MutableSharedFlow
import kotlinx.coroutines.flow.asSharedFlow
import kotlinx.coroutines.launch


class PlacesViewModelFactory : ViewModelProvider.Factory {
    override fun <T : ViewModel> create(modelClass: Class<T>): T {
        if (modelClass.isAssignableFrom(PlacesViewModel::class.java)) {
            val repo = PlaceRepositoryImpl()
            val repo2 = CityRepositoryImpl()
                val useCase1 = GetPlacesUseCase(repo)
                val useCase2 = GetCityListUseCase(repo2)

            return PlacesViewModel(useCase1, useCase2) as T
        }
        throw IllegalArgumentException("Unknown class name")
    }
}

class PlacesViewModel(
    private val getPlaceListCase: GetPlacesUseCase,
    private val getCityListCase: GetCityListUseCase,
): ViewModel() {
    private var _placeList = MutableSharedFlow<List<PlaceModel>>()
    var placeList = _placeList.asSharedFlow()

    private var _cityList = MutableSharedFlow<List<CityModel>>()
    var cityList = _cityList.asSharedFlow()


    fun getPlaceList(cityId: Int) {
        viewModelScope.launch {
            _placeList.emit(getPlaceListCase(cityId))
        }
    }

    fun getCityList() {
        viewModelScope.launch {
            _cityList.emit(getCityListCase())
        }
    }
}
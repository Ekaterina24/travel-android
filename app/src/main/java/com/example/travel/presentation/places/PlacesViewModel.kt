package com.example.travel.presentation.places

import android.util.Log
import androidx.lifecycle.ViewModel
import androidx.lifecycle.ViewModelProvider
import androidx.lifecycle.viewModelScope
import com.example.travel.App
import com.example.travel.data.network.AudioRepositoryImpl
import com.example.travel.data.network.CityRepositoryImpl
import com.example.travel.data.network.PlaceRepositoryImpl
import com.example.travel.domain.model.AudioModel
import com.example.travel.domain.model.CategoryModel
import com.example.travel.domain.model.CityModel
import com.example.travel.domain.model.PlaceAudioModel
import com.example.travel.domain.model.PlaceModel
import com.example.travel.domain.usecase.CashCityListUseCase
import com.example.travel.domain.usecase.GetAudioListByPlaceUseCase
import com.example.travel.domain.usecase.GetAudioListUseCase
import com.example.travel.domain.usecase.GetCategoryListUseCase
import com.example.travel.domain.usecase.GetCityListUseCase
import com.example.travel.domain.usecase.GetPlaceByIdUseCase
import com.example.travel.domain.usecase.GetPlacesUseCase
import com.example.travel.domain.usecase.UploadCityListUseCase
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.async
import kotlinx.coroutines.flow.MutableSharedFlow
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.SharingStarted
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asSharedFlow
import kotlinx.coroutines.flow.collect
import kotlinx.coroutines.flow.combine
import kotlinx.coroutines.flow.first
import kotlinx.coroutines.flow.stateIn
import kotlinx.coroutines.launch


class PlacesViewModelFactory : ViewModelProvider.Factory {
    override fun <T : ViewModel> create(modelClass: Class<T>): T {
        if (modelClass.isAssignableFrom(PlacesViewModel::class.java)) {
            val repo = PlaceRepositoryImpl()
            val repo2 = CityRepositoryImpl(App.INSTANCE)
            val repo3 = AudioRepositoryImpl()
                val useCase1 = GetPlacesUseCase(repo)

            val useCase2 = GetCityListUseCase(repo2)
//            val useCase9 = GetCityData(repo2)
            val useCase7 = CashCityListUseCase(repo2)
            val useCase8 = UploadCityListUseCase(repo2)

            val useCase3 = GetAudioListByPlaceUseCase(repo3)
            val useCase4 = GetAudioListUseCase(repo3)
            val useCase5 = GetPlaceByIdUseCase(repo)
            val useCase6 = GetCategoryListUseCase(repo)

            return PlacesViewModel(useCase1, useCase2, useCase7, useCase8, useCase3, useCase4, useCase5, useCase6, ) as T
        }
        throw IllegalArgumentException("Unknown class name")
    }
}

class PlacesViewModel(
    private val getPlaceListCase: GetPlacesUseCase,
    private val getCityListCase: GetCityListUseCase,
    private val cashCityListCase: CashCityListUseCase,
    private val uploadCityListCase: UploadCityListUseCase,
    private val getAudioListByPlaceUseCase: GetAudioListByPlaceUseCase,
    private val getAudioListUseCase: GetAudioListUseCase,
    private val getPlaceByIdUseCase: GetPlaceByIdUseCase,
    private val getCategoryListUseCase: GetCategoryListUseCase,
): ViewModel() {
    private var count = 0
    private var _placeList = MutableSharedFlow<List<PlaceModel>>()
    var placeList = _placeList.asSharedFlow()

    private var _categoryList = MutableSharedFlow<List<CategoryModel>>()
    var categoryList = _categoryList.asSharedFlow()

    private var _cityList = MutableSharedFlow<List<CityModel>>()
    var cityList = _cityList.asSharedFlow()

    private var _audioListByPlace = MutableSharedFlow<List<AudioModel>>()
    var audioListByPlace = _audioListByPlace.asSharedFlow()

    private var _place = MutableSharedFlow<PlaceModel>()
    var place = _place.asSharedFlow()

    fun getPlaceList(cityId: Int, search: String, category: String) {
        viewModelScope.launch(Dispatchers.IO) {
            _placeList.emit(getPlaceListCase(cityId, search, category))
        }
    }

    fun getCategoryList() {
        viewModelScope.launch(Dispatchers.IO) {
            _categoryList.emit(getCategoryListUseCase())
        }
    }

    fun getPlaceById(id: String) {
        viewModelScope.launch(Dispatchers.IO) {
            _place.emit(getPlaceByIdUseCase(id))
        }
    }

    fun getAudioListByPlace(placeId: String) {
        viewModelScope.launch(Dispatchers.IO) {
            _audioListByPlace.emit(getAudioListByPlaceUseCase(placeId))
        }
    }

//    fun getAudioList() {
//        viewModelScope.launch {
//            _audioListByPlace.emit(getAudioListUseCase())
//        }
//    }

//    fun getCityList() {
//        viewModelScope.launch(Dispatchers.IO) {
//            _cityList.emit(getCityListCase())
//        }
//    }

    init {
//        upload()
//        viewModelScope.launch(Dispatchers.IO) {
//            if (_cityList.first().isEmpty()) {
//                getCityList()
//            } else {
//                upload()
//            }
//        }
    }

    fun upload() {
        viewModelScope.launch(Dispatchers.IO) {
            async {
                kotlin.runCatching {
                    uploadCityListCase()
                }.fold(
                    onSuccess = { _cityList.emit(it) },
                    onFailure = { Log.e("TAG", "${it.message}: ", it) }
                )
            }.await()

        }
    }

//    fun getCityList() {
//        viewModelScope.launch(Dispatchers.IO) {
//            kotlin.runCatching {
////                if  (_cityList.first().isEmpty()) {
////                    cashCityListCase(getCityListCase())
////                _isLoading.value = tru
////                }
//                if (count == 0) {
//                    cashCityListCase(getCityListCase())
//                    count++
//                }
//                Log.d("TAG", "getCityList: $count")
//                uploadCityListCase()
//            }.fold(
//                onSuccess = { _cityList.emit(it) },
//                onFailure = { Log.e("TAG", "${it.message}: ", it) }
//            )
////            _isLoading.value = false
//        }
//
//    }

    fun getCityData() {
        viewModelScope.launch(Dispatchers.IO) {
            kotlin.runCatching {
//                if  (_cityList.first().isEmpty()) {
//                    cashCityListCase(getCityListCase())
//                _isLoading.value = tru
//                }
                getCityListCase.getData()
            }.fold(
                onSuccess = { _cityList.emit(it) },
                onFailure = { Log.e("TAG", "${it.message}: ", it) }
            )
//            _isLoading.value = false
        }

    }
}
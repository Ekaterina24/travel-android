package com.example.travel.presentation.places

import android.util.Log
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import androidx.lifecycle.ViewModelProvider
import androidx.lifecycle.viewModelScope
import com.example.travel.App
import com.example.travel.data.network.AudioRepositoryImpl
import com.example.travel.data.network.CityRepositoryImpl
import com.example.travel.data.network.PlaceRepositoryImpl
import com.example.travel.data.network.ReviewRepositoryImpl
import com.example.travel.domain.model.AudioModel
import com.example.travel.domain.model.CategoryModel
import com.example.travel.domain.model.CityModel
import com.example.travel.domain.model.PlaceModel
import com.example.travel.domain.model.review.CreateReviewModel
import com.example.travel.domain.model.review.GetReviewModel
import com.example.travel.domain.usecase.CashCityListUseCase
import com.example.travel.domain.usecase.review.CreateReviewUseCase
import com.example.travel.domain.usecase.GetAudioListByPlaceUseCase
import com.example.travel.domain.usecase.GetAudioListUseCase
import com.example.travel.domain.usecase.GetCategoryListUseCase
import com.example.travel.domain.usecase.GetCityListUseCase
import com.example.travel.domain.usecase.GetPlaceByIdUseCase
import com.example.travel.domain.usecase.place.GetPlacesUseCase
import com.example.travel.domain.usecase.UploadCityListUseCase
import com.example.travel.domain.usecase.place.CashPlaceUseCase
import com.example.travel.domain.usecase.place.UpdatePlaceFavoriteUseCase
import com.example.travel.domain.usecase.place.UpdatePlaceVisitedUseCase
import com.example.travel.domain.usecase.place.UploadPlaceListUseCase
import com.example.travel.domain.usecase.place.UploadPlaceUseCase
import com.example.travel.domain.usecase.review.GetReviewListByPlaceUseCase
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.flow.MutableSharedFlow
import kotlinx.coroutines.flow.asSharedFlow
import kotlinx.coroutines.launch


class PlacesViewModelFactory : ViewModelProvider.Factory {
    override fun <T : ViewModel> create(modelClass: Class<T>): T {
        if (modelClass.isAssignableFrom(PlacesViewModel::class.java)) {
            val placeRepo = PlaceRepositoryImpl(App.INSTANCE)
            val cityRepo = CityRepositoryImpl(App.INSTANCE)
            val audioRepo = AudioRepositoryImpl(App.INSTANCE)
            val reviewRepo = ReviewRepositoryImpl(App.INSTANCE)

            val useCase1 = GetPlacesUseCase(placeRepo)

            val useCase2 = GetCityListUseCase(cityRepo)
//            val useCase9 = GetCityData(repo2)
            val useCase7 = CashCityListUseCase(cityRepo)
            val useCase8 = UploadCityListUseCase(cityRepo)

            val useCase3 = GetAudioListByPlaceUseCase(audioRepo)
            val useCase4 = GetAudioListUseCase(audioRepo)
            val useCase5 = GetPlaceByIdUseCase(placeRepo)
            val useCase6 = GetCategoryListUseCase(placeRepo)
            val createReviewUseCase = CreateReviewUseCase(reviewRepo)
            val getReviewListByPlaceUseCase = GetReviewListByPlaceUseCase(reviewRepo)

            val cashPlaceUseCase = CashPlaceUseCase(placeRepo)
            val uploadPlaceListUseCase = UploadPlaceListUseCase(placeRepo)
            val uploadPlaceUseCase = UploadPlaceUseCase(placeRepo)
            val updatePlaceVisitedUseCase = UpdatePlaceVisitedUseCase(placeRepo)
            val updatePlaceFavoriteUseCase = UpdatePlaceFavoriteUseCase(placeRepo)

            return PlacesViewModel(
                useCase1, useCase2, useCase7, useCase8, useCase3, useCase4, useCase5, useCase6,
                cashPlaceUseCase, uploadPlaceListUseCase, uploadPlaceUseCase,
                updatePlaceVisitedUseCase, updatePlaceFavoriteUseCase,
                createReviewUseCase, getReviewListByPlaceUseCase
            ) as T
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
    private val cashPlaceUseCase: CashPlaceUseCase,
    private val uploadPlaceListUseCase: UploadPlaceListUseCase,
    private val uploadPlaceUseCase: UploadPlaceUseCase,
    private val updatePlaceVisitedUseCase: UpdatePlaceVisitedUseCase,
    private val updatePlaceFavoriteUseCase: UpdatePlaceFavoriteUseCase,
    private val createReviewUseCase: CreateReviewUseCase,
    private val getReviewListByPlaceUseCase: GetReviewListByPlaceUseCase,
) : ViewModel() {
    private var count = 0
    private var _placeList = MutableSharedFlow<List<PlaceModel>>()
    var placeList = _placeList.asSharedFlow()

    private var _reviewListByPlace = MutableSharedFlow<List<GetReviewModel>>()
    var reviewListByPlace = _reviewListByPlace.asSharedFlow()

    private var _categoryList = MutableSharedFlow<List<CategoryModel>>()
    var categoryList = _categoryList.asSharedFlow()

    private var _cityList = MutableSharedFlow<List<CityModel>>()
    var cityList = _cityList.asSharedFlow()

    private var _audioListByPlace = MutableSharedFlow<List<AudioModel>>()
    var audioListByPlace = _audioListByPlace.asSharedFlow()

    private var _place = MutableSharedFlow<PlaceModel>()
    var place = _place.asSharedFlow()

    val locationUpdates = MutableLiveData<LocationModel>()

    fun getPlaceListFromApi(cityId: Int, search: String, category: String) {
        viewModelScope.launch(Dispatchers.IO) {
            _placeList.emit(getPlaceListCase(cityId, search, category))
        }
    }

    fun createReview(token: String, review: CreateReviewModel) {
        viewModelScope.launch {
            createReviewUseCase(token, review)
            getReviewListByPlace(review.placeId)
        }
    }

    fun getReviewListByPlace(id: String) {
        viewModelScope.launch(Dispatchers.IO) {
            _reviewListByPlace.emit(getReviewListByPlaceUseCase(id))
        }
    }

    fun uploadPlaceFromDb(generatedId: Long, cityId: Int, search: String, category: String) {
        viewModelScope.launch {


            kotlin.runCatching {
//                if  (_cityList.first().isEmpty()) {
//                    cashCityListCase(getCityListCase())
//                _isLoading.value = tru
//                }
                uploadPlaceUseCase(generatedId)

            }.fold(
                onSuccess = {
                    _place.emit(it)
                    updatePlaceFavorite(!it.is_favourite, generatedId)
                    getPlaceListData(cityId, search, category)
                },
                onFailure = { Log.e("TAG", "${it.message}: ", it) }
            )
        }
    }

    suspend fun getPlaceFromDbApi(id: String) = uploadPlaceUseCase.getPlaceByIdFromApi(id)

    suspend fun getPlaceListFromDb(cityId: Int, search: String, category: String)
            = getPlaceListCase.getData(cityId, search, category)

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

    fun getPlaceListData(cityId: Int, search: String, category: String) {
        viewModelScope.launch(Dispatchers.IO) {
            kotlin.runCatching {
//                if  (_cityList.first().isEmpty()) {
//                    cashCityListCase(getCityListCase())
//                _isLoading.value = tru
//                }
                getPlaceListCase.getData(cityId, search, category)
            }.fold(
                onSuccess = { _placeList.emit(it) },
                onFailure = { Log.e("TAG", "${it.message}: ", it) }
            )
//            _isLoading.value = false
        }

    }

    fun updatePlaceVisited(isVisited: Boolean, generatedId: Long) {
        viewModelScope.launch {
            updatePlaceVisitedUseCase(isVisited, generatedId)
        }
    }

    fun updatePlaceFavorite(isFavorite: Boolean, generatedId: Long) {
        viewModelScope.launch {
            updatePlaceFavoriteUseCase(isFavorite, generatedId)
        }
    }
    fun uploadPlaceFromDb(generatedId: Long) {
        viewModelScope.launch {


            kotlin.runCatching {
//                if  (_cityList.first().isEmpty()) {
//                    cashCityListCase(getCityListCase())
//                _isLoading.value = tru
//                }
                uploadPlaceUseCase(generatedId)
            }.fold(
                onSuccess = { _place.emit(it) },
                onFailure = { Log.e("TAG", "${it.message}: ", it) }
            )
        }
    }
}
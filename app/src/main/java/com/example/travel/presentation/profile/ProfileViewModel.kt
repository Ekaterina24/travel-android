package com.example.travel.presentation.profile

import android.util.Log
import androidx.lifecycle.ViewModel
import androidx.lifecycle.ViewModelProvider
import androidx.lifecycle.viewModelScope
import com.example.travel.App
import com.example.travel.data.network.AudioRepositoryImpl
import com.example.travel.data.network.CityRepositoryImpl
import com.example.travel.data.network.PlaceRepositoryImpl
import com.example.travel.data.network.UserRepositoryImpl
import com.example.travel.domain.model.PlaceModel
import com.example.travel.domain.model.RegisterModel
import com.example.travel.domain.model.TokenModel
import com.example.travel.domain.model.UserProfileModel
import com.example.travel.domain.usecase.CashCityListUseCase
import com.example.travel.domain.usecase.GetAudioListByPlaceUseCase
import com.example.travel.domain.usecase.GetAudioListUseCase
import com.example.travel.domain.usecase.GetCategoryListUseCase
import com.example.travel.domain.usecase.GetCityListUseCase
import com.example.travel.domain.usecase.GetPlaceByIdUseCase
import com.example.travel.domain.usecase.UploadCityListUseCase
import com.example.travel.domain.usecase.place.CashPlaceUseCase
import com.example.travel.domain.usecase.place.GetPlacesUseCase
import com.example.travel.domain.usecase.place.UpdatePlaceFavoriteUseCase
import com.example.travel.domain.usecase.place.UpdatePlaceVisitedUseCase
import com.example.travel.domain.usecase.place.UploadPlaceListUseCase
import com.example.travel.domain.usecase.place.UploadPlaceUseCase
import com.example.travel.domain.usecase.user.CashUserUseCase
import com.example.travel.domain.usecase.user.GetUserProfileUseCase
import com.example.travel.domain.usecase.user.UploadUserUseCase
import com.example.travel.presentation.places.PlacesViewModel
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.flow.MutableSharedFlow
import kotlinx.coroutines.flow.asSharedFlow
import kotlinx.coroutines.launch


class ProfileViewModelFactory : ViewModelProvider.Factory {
    override fun <T : ViewModel> create(modelClass: Class<T>): T {
        if (modelClass.isAssignableFrom(ProfileViewModel::class.java)) {
            val userRepo = UserRepositoryImpl(App.INSTANCE)
            val placeRepo = PlaceRepositoryImpl(App.INSTANCE)

            val getUserProfileUseCase = GetUserProfileUseCase(userRepo)
            val cashUserUseCase = CashUserUseCase(userRepo)
            val uploadUserUseCase = UploadUserUseCase(userRepo)

            val uploadPlaceListUseCase = UploadPlaceListUseCase(placeRepo)
            val useCase1 = GetPlacesUseCase(placeRepo)


            return ProfileViewModel( getUserProfileUseCase,
                cashUserUseCase, uploadUserUseCase, uploadPlaceListUseCase,
//                useCase1
            ) as T
        }
        throw IllegalArgumentException("Unknown class name")
    }
}

class ProfileViewModel(
    private val getUserProfileUseCase: GetUserProfileUseCase,
    private val cashUserUseCase: CashUserUseCase,
    private val uploadUserUseCase: UploadUserUseCase,
    private val uploadPlaceListUseCase: UploadPlaceListUseCase,
) : ViewModel() {

    private var _userProfile = MutableSharedFlow<UserProfileModel>()
    var userProfile = _userProfile.asSharedFlow()

    private var _placeList = MutableSharedFlow<List<PlaceModel>>()
    var placeList = _placeList.asSharedFlow()

    fun cashUserProfile(token: String) {
        viewModelScope.launch(Dispatchers.IO) {
//            cashUserUseCase(getUserProfileUseCase(token))
        }
    }
    fun uploadUserProfile(token: String) {
        viewModelScope.launch(Dispatchers.IO) {
            cashUserUseCase(getUserProfileUseCase(token))
            _userProfile.emit(uploadUserUseCase())
        }
    }

    fun uploadPlaceList() {
        viewModelScope.launch(Dispatchers.IO) {
            _placeList.emit(uploadPlaceListUseCase())
        }
    }

//    fun getPlaceListData(cityId: Int, search: String, category: String) {
//        viewModelScope.launch(Dispatchers.IO) {
//            kotlin.runCatching {
////                if  (_cityList.first().isEmpty()) {
////                    cashCityListCase(getCityListCase())
////                _isLoading.value = tru
////                }
//                getPlaceListCase.getData(cityId, search, category)
//            }.fold(
//                onSuccess = { _placeList.emit(it) },
//                onFailure = { Log.e("TAG", "${it.message}: ", it) }
//            )
////            _isLoading.value = false
//        }
//
//    }
}
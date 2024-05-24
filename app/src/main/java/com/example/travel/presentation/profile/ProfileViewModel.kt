package com.example.travel.presentation.profile

import androidx.lifecycle.ViewModel
import androidx.lifecycle.ViewModelProvider
import androidx.lifecycle.viewModelScope
import com.example.travel.App
import com.example.travel.data.network.PlaceRepositoryImpl
import com.example.travel.data.network.SubscribeRepositoryImpl
import com.example.travel.data.network.TypeSubRepositoryImpl
import com.example.travel.data.network.UserRepositoryImpl
import com.example.travel.domain.model.CreateSubscribeModel
import com.example.travel.domain.model.PlaceModel
import com.example.travel.domain.model.GetSubscribeModel
import com.example.travel.domain.model.TypeSubModel
import com.example.travel.domain.model.UpdateScoresRequest
import com.example.travel.domain.model.UserProfileModel
import com.example.travel.domain.usecase.place.GetPlacesUseCase
import com.example.travel.domain.usecase.place.UploadPlaceListUseCase
import com.example.travel.domain.usecase.subscribe.CreateSubscribeUseCase
import com.example.travel.domain.usecase.subscribe.GetSubscribeListByUserUseCase
import com.example.travel.domain.usecase.type_sub.GetTypeSubListUseCase
import com.example.travel.domain.usecase.user.CashUserUseCase
import com.example.travel.domain.usecase.user.GetUserListUseCase
import com.example.travel.domain.usecase.user.GetUserProfileUseCase
import com.example.travel.domain.usecase.user.UpdateScoresFromApiUseCase
import com.example.travel.domain.usecase.user.UploadUserUseCase
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.flow.MutableSharedFlow
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.asSharedFlow
import kotlinx.coroutines.launch


class ProfileViewModelFactory : ViewModelProvider.Factory {
    override fun <T : ViewModel> create(modelClass: Class<T>): T {
        if (modelClass.isAssignableFrom(ProfileViewModel::class.java)) {
            val userRepo = UserRepositoryImpl(App.INSTANCE)
            val placeRepo = PlaceRepositoryImpl(App.INSTANCE)
            val typeSubRepo = TypeSubRepositoryImpl(App.INSTANCE)
            val subscribeRepo = SubscribeRepositoryImpl(App.INSTANCE)

            val getUserProfileUseCase = GetUserProfileUseCase(userRepo)
            val cashUserUseCase = CashUserUseCase(userRepo)
            val uploadUserUseCase = UploadUserUseCase(userRepo)
            val getUserListUseCase = GetUserListUseCase(userRepo)
            val updateScoresFromApiUseCase = UpdateScoresFromApiUseCase(userRepo)

            val uploadPlaceListUseCase = UploadPlaceListUseCase(placeRepo)
            val useCase1 = GetPlacesUseCase(placeRepo)

            val getTypeSubListUseCase = GetTypeSubListUseCase(typeSubRepo)

            val createSubscribeUseCase = CreateSubscribeUseCase(subscribeRepo)
            val getSubscribeListByUserUseCase = GetSubscribeListByUserUseCase(subscribeRepo)


            return ProfileViewModel(
                getUserProfileUseCase,
                cashUserUseCase,
                uploadUserUseCase,
                uploadPlaceListUseCase,
//                useCase1
                getTypeSubListUseCase,
                createSubscribeUseCase,
                getSubscribeListByUserUseCase,
                getUserListUseCase,
                updateScoresFromApiUseCase
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
    private val getTypeSubListUseCase: GetTypeSubListUseCase,
    private val createSubscribeUseCase: CreateSubscribeUseCase,
    private val getSubscribeListByUserUseCase: GetSubscribeListByUserUseCase,
    private val getUserListUseCase: GetUserListUseCase,
    private val updateScoresFromApiUseCase: UpdateScoresFromApiUseCase,
) : ViewModel() {

    private var _userProfile = MutableSharedFlow<UserProfileModel>()
    var userProfile = _userProfile.asSharedFlow()

    private var _userList = MutableSharedFlow<List<UserProfileModel>>()
    var userList = _userList.asSharedFlow()

    private var _placeList = MutableSharedFlow<List<PlaceModel>>()
    var placeList = _placeList.asSharedFlow()

    private var _typeSubList = MutableSharedFlow<List<TypeSubModel>>()
    var typeSubList = _typeSubList.asSharedFlow()

    private var _subscribeListByUser = MutableSharedFlow<List<GetSubscribeModel>>()
    var subscribeListByUser = _subscribeListByUser.asSharedFlow()

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

    fun getTypeSubList() {
        viewModelScope.launch {
            _typeSubList.emit(getTypeSubListUseCase())
        }
    }

    fun getUserList() {
        viewModelScope.launch {
            _userList.emit(getUserListUseCase())
        }
    }

    fun createSubscribe(token: String, subscribe: CreateSubscribeModel) {
        viewModelScope.launch {
            createSubscribeUseCase(token, subscribe)
            getSubscribeListByUser(token)
        }
    }

    fun updateScoresFromApi(token: String, scores: UpdateScoresRequest) {
        viewModelScope.launch {
            updateScoresFromApiUseCase(token, scores)
//            getUserList()
        }
    }

    fun getSubscribeListByUser(token: String) {
        viewModelScope.launch {
            _subscribeListByUser.emit(getSubscribeListByUserUseCase(token))
        }
    }
}
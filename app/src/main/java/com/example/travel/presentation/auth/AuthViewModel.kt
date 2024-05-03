package com.example.travel.presentation.auth

import androidx.lifecycle.ViewModel
import androidx.lifecycle.ViewModelProvider
import androidx.lifecycle.viewModelScope
import com.example.travel.App
import com.example.travel.data.network.DayPlacesRepositoryImpl
import com.example.travel.data.network.TripRepositoryImpl
import com.example.travel.data.network.UserRepositoryImpl
import com.example.travel.domain.model.DayPlaceModel
import com.example.travel.domain.model.GetTripListModel
import com.example.travel.domain.model.LoginModel
import com.example.travel.domain.model.RegisterModel
import com.example.travel.domain.model.TokenModel
import com.example.travel.domain.model.TripModel
import com.example.travel.domain.usecase.AddDayPlaceUseCase
import com.example.travel.domain.usecase.CreateTripUseCase
import com.example.travel.domain.usecase.GetDayListByUserUseCase
import com.example.travel.domain.usecase.GetTripListByUserUseCase
import com.example.travel.domain.usecase.LoginUserUseCase
import com.example.travel.domain.usecase.RegisterUserUseCase
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.flow.MutableSharedFlow
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.asSharedFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.launch


class AuthViewModelFactory : ViewModelProvider.Factory {
    override fun <T : ViewModel> create(modelClass: Class<T>): T {
        if (modelClass.isAssignableFrom(AuthViewModel::class.java)) {
            val repo = UserRepositoryImpl(App.INSTANCE)
                val useCase1 = RegisterUserUseCase(repo)
                val useCase2 = LoginUserUseCase(repo)

            return AuthViewModel(useCase1, useCase2) as T
        }
        throw IllegalArgumentException("Unknown class name")
    }
}

class AuthViewModel(
    private val registerUserUseCase: RegisterUserUseCase,
    private val loginUserUseCase: LoginUserUseCase,
): ViewModel() {
    private var _registerUser = MutableSharedFlow<RegisterModel>()
    var registerUser = _registerUser.asSharedFlow()

    private var _userToken = MutableSharedFlow<TokenModel>()
    var userToken = _userToken.asSharedFlow()

    fun registerUser(user: RegisterModel) {
        viewModelScope.launch(Dispatchers.IO) {
            registerUserUseCase(user)
        }
    }
    fun loginUser(user: LoginModel) {
        viewModelScope.launch(Dispatchers.IO) {
            _userToken.emit(loginUserUseCase(user))
        }
    }
}
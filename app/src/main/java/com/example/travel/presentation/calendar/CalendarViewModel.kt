package com.example.travel.presentation.calendar

import androidx.lifecycle.ViewModel
import androidx.lifecycle.ViewModelProvider
import androidx.lifecycle.viewModelScope
import com.example.travel.data.network.DayPlacesRepositoryImpl
import com.example.travel.data.network.TripRepositoryImpl
import com.example.travel.domain.model.DayPlaceModel
import com.example.travel.domain.model.GetTripListModel
import com.example.travel.domain.model.PlaceModel
import com.example.travel.domain.model.TripModel
import com.example.travel.domain.usecase.AddDayPlaceUseCase
import com.example.travel.domain.usecase.CreateTripUseCase
import com.example.travel.domain.usecase.GetDayListByUserUseCase
import com.example.travel.domain.usecase.GetTripListByUserUseCase
import kotlinx.coroutines.flow.MutableSharedFlow
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.asSharedFlow
import kotlinx.coroutines.launch


class CalendarViewModelFactory : ViewModelProvider.Factory {
    override fun <T : ViewModel> create(modelClass: Class<T>): T {
        if (modelClass.isAssignableFrom(CalendarViewModel::class.java)) {
            val repo = TripRepositoryImpl()
            val repo2 = DayPlacesRepositoryImpl()
                val useCase2 = CreateTripUseCase(repo)
                val useCase = GetTripListByUserUseCase(repo)
                val useCase3 = AddDayPlaceUseCase(repo2)
                val useCase4 = GetDayListByUserUseCase(repo2)

            return CalendarViewModel(useCase2, useCase, useCase3, useCase4) as T
        }
        throw IllegalArgumentException("Unknown class name")
    }
}

class CalendarViewModel(
    private val createTripUseCase: CreateTripUseCase,
    private val getTripListByUserUseCase: GetTripListByUserUseCase,
    private val addDayPlaceUseCase: AddDayPlaceUseCase,
    private val getDayListByUserUseCase: GetDayListByUserUseCase,
): ViewModel() {
    private var _tripListByUser = MutableSharedFlow<List<GetTripListModel>>()
    var tripListByUser = _tripListByUser.asSharedFlow()

    private var _dayListByUser = MutableSharedFlow<List<DayPlaceModel>>()
    var dayListByUser = _dayListByUser.asSharedFlow()

    var startDate = MutableStateFlow("00-00-00")
    val finishDate = MutableStateFlow("00-00-00")

    fun createTrip(token: String, trip: TripModel) {
        viewModelScope.launch {
            createTripUseCase(token, trip)
        }
    }

    fun addDayPlace(token: String, dayPlace: DayPlaceModel) {
        viewModelScope.launch {
            addDayPlaceUseCase(token, dayPlace)
        }
    }

    fun getTripListByUser(token: String) {
        viewModelScope.launch {
            _tripListByUser.emit(getTripListByUserUseCase(token))
        }
    }

    fun getDayListByUser(token: String, date: String) {
        viewModelScope.launch {
            _dayListByUser.emit(getDayListByUserUseCase(token, date))
        }
    }
}
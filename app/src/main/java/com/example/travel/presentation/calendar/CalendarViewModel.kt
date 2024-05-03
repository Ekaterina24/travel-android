package com.example.travel.presentation.calendar

import android.util.Log
import androidx.lifecycle.ViewModel
import androidx.lifecycle.ViewModelProvider
import androidx.lifecycle.viewModelScope
import com.example.travel.App
import com.example.travel.data.network.AudioRepositoryImpl
import com.example.travel.data.network.DayPlacesRepositoryImpl
import com.example.travel.data.network.SubscribeRepositoryImpl
import com.example.travel.data.network.TripRepositoryImpl
import com.example.travel.domain.model.AudioModel
import com.example.travel.domain.model.CityModel
import com.example.travel.domain.model.DayPlaceModel
import com.example.travel.domain.model.GetTripListModel
import com.example.travel.domain.model.SubscribeModel
import com.example.travel.domain.model.TripModel
import com.example.travel.domain.usecase.AddDayPlaceUseCase
import com.example.travel.domain.usecase.CashAudioUseCase
import com.example.travel.domain.usecase.CashSubscribeListUseCase
import com.example.travel.domain.usecase.CreateTripUseCase
import com.example.travel.domain.usecase.GetDayListByUserUseCase
import com.example.travel.domain.usecase.GetTripListByUserUseCase
import com.example.travel.domain.usecase.UpdateAudioUseCase
import com.example.travel.domain.usecase.UploadSubscribeUseCase
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.async
import kotlinx.coroutines.flow.MutableSharedFlow
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.asSharedFlow
import kotlinx.coroutines.flow.first
import kotlinx.coroutines.flow.toList
import kotlinx.coroutines.launch


class CalendarViewModelFactory : ViewModelProvider.Factory {
    override fun <T : ViewModel> create(modelClass: Class<T>): T {
        if (modelClass.isAssignableFrom(CalendarViewModel::class.java)) {
            val repo = TripRepositoryImpl()
            val repo2 = DayPlacesRepositoryImpl()
            val repo3 = SubscribeRepositoryImpl(App.INSTANCE)
            val repo4 = AudioRepositoryImpl(App.INSTANCE)
                val useCase2 = CreateTripUseCase(repo)
                val useCase = GetTripListByUserUseCase(repo)
                val useCase3 = AddDayPlaceUseCase(repo2)
                val useCase4 = GetDayListByUserUseCase(repo2)
                val useCase5 = CashSubscribeListUseCase(repo3)
                val useCase6 = CashAudioUseCase(repo4)
                val useCase7 = UpdateAudioUseCase(repo4)
                val useCase8 = UploadSubscribeUseCase(repo3)

            return CalendarViewModel(useCase2, useCase, useCase3, useCase4, useCase5, useCase6, useCase7, useCase8) as T
        }
        throw IllegalArgumentException("Unknown class name")
    }
}

class CalendarViewModel(
    private val createTripUseCase: CreateTripUseCase,
    private val getTripListByUserUseCase: GetTripListByUserUseCase,
    private val addDayPlaceUseCase: AddDayPlaceUseCase,
    private val getDayListByUserUseCase: GetDayListByUserUseCase,
    private val cashSubscribeUseCase: CashSubscribeListUseCase,
    private val cashAudioUseCase: CashAudioUseCase,
    private val updateAudioUseCase: UpdateAudioUseCase,
    private val uploadSubscribeUseCase: UploadSubscribeUseCase,
): ViewModel() {
    private var _tripListByUser = MutableSharedFlow<List<GetTripListModel>>()
    var tripListByUser = _tripListByUser.asSharedFlow()

    private var _dayListByUser = MutableSharedFlow<List<DayPlaceModel>>()
    var dayListByUser = _dayListByUser.asSharedFlow()

    var startDate = MutableStateFlow("00-00-00")
    val finishDate = MutableStateFlow("00-00-00")

    private var _subList = MutableSharedFlow<List<SubscribeModel>>()
    var subList = _subList.asSharedFlow()

    fun createTrip(token: String, trip: TripModel) {
        viewModelScope.launch {
            createTripUseCase(token, trip)
        }
    }

    fun createSub(subscribe: SubscribeModel) {
        viewModelScope.launch {
            cashSubscribeUseCase(subscribe)
        }
    }

    fun uploadSub() {
//
//        viewModelScope.launch(Dispatchers.IO) {
//            runCatching {
//                uploadSubscribeUseCase().first() // Вызываем .first(), чтобы собрать значение из Flow
//            }.onSuccess { list ->
//                _subList.emit(list) // Мы ожидаем List<SubscribeModel>, поэтому можем использовать emit для передачи списка
//            }.onFailure { exception ->
//                Log.e("TAG", "Error uploading subscription: ", exception)
//            }
//        }
        viewModelScope.launch(Dispatchers.IO) {
            async {
                kotlin.runCatching {
                    uploadSubscribeUseCase()
                }.fold(
                    onSuccess = { _subList.emit(it) },
                    onFailure = { Log.e("TAG", "${it.message}: ", it) }
                )
            }.await()

        }
    }

    fun addAudio(audio: AudioModel) {
        viewModelScope.launch {
            cashAudioUseCase(audio)
        }
    }

    fun updateAudio(status: String, id: Long) {
        viewModelScope.launch {
            updateAudioUseCase(status, id)
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
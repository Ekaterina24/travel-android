package com.example.travel.presentation.calendar

import android.os.Bundle
import android.os.Parcelable
import android.util.Log
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.AdapterView
import android.widget.ArrayAdapter
import android.widget.Toast
import androidx.fragment.app.viewModels
import androidx.lifecycle.lifecycleScope
import androidx.navigation.fragment.findNavController
import com.example.travel.R
import com.example.travel.data.local.prefs.SharedPreferences
import com.example.travel.databinding.FragmentCalendarBinding
import com.example.travel.domain.model.AudioModel
import com.example.travel.domain.model.TripModel
import com.example.travel.presentation.places.PlacesViewModel
import com.example.travel.presentation.places.PlacesViewModelFactory
import com.google.android.material.datepicker.MaterialDatePicker
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext
import kotlinx.parcelize.Parcelize
import java.text.SimpleDateFormat
import java.util.Date

@Parcelize
data class DataForTransfer(
    val field1: String,
    val field2: Long
) : Parcelable

class CalendarFragment : Fragment() {

    private val viewModel: CalendarViewModel by viewModels {
        CalendarViewModelFactory()
    }

    private var _binding: FragmentCalendarBinding? = null
    private val binding get() = _binding!!

    private val viewModelPlace: PlacesViewModel by viewModels {
        PlacesViewModelFactory()
    }

    private var selectedDay = ""
    private var tripId = 0
//    private var arrayData = mutableListOf<String>()
//    private val args by navArgs<CalendarFragmentArgs>()

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        _binding = FragmentCalendarBinding.inflate(inflater, container, false)
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        val sharedPreferences = SharedPreferences(activity?.applicationContext)

        val token = "Bearer ${sharedPreferences.getStringValue("token")}"
        val dateRangePicker =
            MaterialDatePicker.Builder.dateRangePicker()
                .setTitleText("Select dates")
//                    .setSelection(
//                        Pair(
//                            MaterialDatePicker.thisMonthInUtcMilliseconds(),
//                            MaterialDatePicker.todayInUtcMilliseconds()
//                        )
//                    )
                .build()

        viewModel.uploadSub()

//        viewLifecycleOwner.lifecycleScope.launch(Dispatchers.IO) {
//            viewModel.subList.collect { list ->
//                list.forEach { sub ->
//                    if ( Date(System.currentTimeMillis()).time - sub.date.time >= 1000 ) {
//                        Log.d("MY_TAG", "time: ${Date(System.currentTimeMillis()).time - sub.date.time}")
//                        viewModel.updateAudio(status = "OPEN", 1)
//                    }
//                }
//            }
//        }


        binding.rangeDate.setOnClickListener {

            dateRangePicker.show(parentFragmentManager, "Выберите даты")
//            viewLifecycleOwner.lifecycleScope.launch(Dispatchers.Default) {
//                val fr = dateRangePicker.selection?.first
//                viewModel.startDate.emit((dateRangePicker.selection?.first).toString())
//                viewModel.finishDate.emit((dateRangePicker.selection?.second).toString())
////                Log.d("MY_TAG", "onViewCreated: ${dateRangePicker.selection?.first}")
////                viewModel.startDate.collect{
////                    binding.rangeDate.text = it
////                }
////                withContext(Dispatchers.Main) {
////                    binding.rangeDate.text =
////                        "${viewModel.startDate.value}-${viewModel.finishDate.value}"
////                }
//            }

        }
        fun getTime(first: String, second: String): ArrayList<String> {
            return arrayListOf(first, second)
        }

        fun getDateTime(time: Long): String {
            try {
                val sdf = SimpleDateFormat("dd:MM:yyyy ")
                val netDate = Date(time * 1000)
                return sdf.format(netDate)
            } catch (e: Exception) {
                return e.toString()
            }
        }

        val first = 0
        val second = 0
        dateRangePicker.addOnPositiveButtonClickListener { datePicked ->
            val startDate = (datePicked.first / 1000).toString()
            val endDate = (datePicked.second / 1000).toString()

            viewLifecycleOwner.lifecycleScope.launch(Dispatchers.Default) {
                viewModel.startDate.emit(startDate)
                viewModel.finishDate.emit(endDate)

                withContext(Dispatchers.Main) {
                    binding.rangeDate.text =
                        "${getDateTime(viewModel.startDate.value.toLong())}-${getDateTime(viewModel.finishDate.value.toLong())}"
                }
            }

        }
        binding.setDate.setOnClickListener {
            Log.d("MY_TAG", "sharedPreferences: ${sharedPreferences.getStringValue("city")}")
            viewModel.createTrip(
                token,
                TripModel(
                    getDateTime(viewModel.startDate.value.toLong()),
                    getDateTime(viewModel.finishDate.value.toLong()),
                    city = sharedPreferences.getStringValue("city") ?: ""
                )
            )
        }

        binding.calendarView.setOnDateChangeListener { calendarView, year, month, day ->
            var d = ""
            var m = ""
            Log.d("MY_TAG", "onViewCreated: $year $month $day")
            d = if (day < 10) {
                "0${day}"
            } else day.toString()

            m = if (month < 10) {
                "0${month + 1}"
            } else (month + 1).toString()

            selectedDay = "${d}:${m}:${year}"
            Log.d("MY_TAG", "onViewCreated: $selectedDay")
            viewModel.getDayListByUser(token, selectedDay)
        }

        viewModel.getTripListByUser(token)

        viewLifecycleOwner.lifecycleScope.launch(Dispatchers.IO) {
            viewModel.tripListByUser.collect { list ->
                list.forEach { trip ->
                    binding.btnAddPlace.setOnClickListener {
                        if (selectedDay !in trip.date_start..trip.date_finish) {
                            Toast.makeText(
                                requireContext(),
                                "Создайте путешествие!",
                                Toast.LENGTH_SHORT
                            ).show()
                        } else {
                            if (selectedDay != "") {
                                val date = SimpleDateFormat("dd:MM:yyyy").parse(selectedDay)

                                Log.d("My_TAG", "time: ${date.time} $selectedDay")
                                Log.d(
                                    "My_TAG",
                                    "start: ${SimpleDateFormat("dd:MM:yyyy").parse(trip.date_start).time} ${trip.date_start} "
                                )
                                Log.d(
                                    "My_TAG",
                                    "finish: ${SimpleDateFormat("dd:MM:yyyy").parse(trip.date_finish).time} ${trip.date_finish}"
                                )

                                if (date.time >= SimpleDateFormat("dd:MM:yyyy").parse(trip.date_start).time
                                    && date.time <= SimpleDateFormat("dd:MM:yyyy").parse(trip.date_finish).time
                                ) {
                                    tripId = trip.id
                                    Log.d(
                                        "My_TAG",
                                        "tripId: ${tripId}",
                                    )
                                }

                                val arrayData = arrayListOf(selectedDay, tripId.toString())

                                val bundle = Bundle().apply {
                                    putStringArrayList("key", arrayData)
                                }
                                findNavController().navigate(
                                    R.id.action_calendarFragment_to_mapFragment, bundle
                                )
                            }
                        }
                    }
                }

                val itemName =
                    list.map { item -> "${item.city} ${item.date_start}-${item.date_finish}" }
                val items = list

                withContext(Dispatchers.Main) {
                    val adapter =
                        ArrayAdapter(
                            requireContext(),
                            android.R.layout.simple_spinner_item,
                            itemName
                        )
                    adapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item)

                    val spinner = binding.spinnerTrip
                    spinner.adapter = adapter

                    sharedPreferences.getStringValue("pos")
                        ?.let { it1 -> spinner.setSelection(it1.toInt()) }

                    spinner.onItemSelectedListener = object : AdapterView.OnItemSelectedListener {
                        override fun onItemSelected(
                            parent: AdapterView<*>,
                            view: View,
                            position: Int,
                            id: Long
                        ) {
                            val itemSelected = parent.getItemAtPosition(position).toString()
                            items.map { trip ->
                                Log.d("MY_TAG", "onItemSelected: $itemSelected")
                                if ("${trip.city} ${trip.date_start}-${trip.date_finish}" == itemSelected) {
                                    when (trip.city) {
                                        "Волгоград" -> {
                                            sharedPreferences.save("city", trip.city)
                                            sharedPreferences.save("pos", "0")
                                        }

                                        "Великий Новгород" -> {
                                            sharedPreferences.save("city", trip.city)
                                            sharedPreferences.save("pos", "1")
                                        }
                                    }
                                }
                            }
                        }

                        override fun onNothingSelected(parent: AdapterView<*>) {}
                    }


                }
            }
        }

        binding.dayText.text = selectedDay

        val rvAdapter = DayListByUserAdapter()
        binding.rvPlaceList.adapter = rvAdapter

        viewLifecycleOwner.lifecycleScope.launch {
            viewModel.dayListByUser.collect { list ->
                val newList = list.map {
                    Log.d("TAG", "placeId: ${it.placeId}")
                    viewModelPlace.getPlaceFromDbApi(it.placeId)
                }
                withContext(Dispatchers.Main) {
                    rvAdapter.submitList(newList)
                }
            }
        }

    }
}
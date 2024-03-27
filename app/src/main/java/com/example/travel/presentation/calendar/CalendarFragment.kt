package com.example.travel.presentation.calendar

import android.os.Bundle
import android.os.Parcelable
import android.util.Log
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.core.os.bundleOf
import androidx.fragment.app.setFragmentResult
import androidx.fragment.app.viewModels
import androidx.lifecycle.lifecycleScope
import androidx.navigation.fragment.findNavController
import androidx.navigation.fragment.navArgs
import com.example.travel.R
import com.example.travel.databinding.FragmentCalendarBinding
import com.example.travel.domain.model.TripModel
import com.example.travel.presentation.places.MapFragmentDirections
import com.example.travel.presentation.places.PlaceListAdapter
import com.google.android.material.datepicker.MaterialDatePicker
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.flow.collect
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext
import kotlinx.parcelize.Parcelize
import java.text.SimpleDateFormat
import java.util.Date

@Parcelize
data class DataForTransfer(
    val field1: String,
    val field2: Long
): Parcelable

class CalendarFragment : Fragment() {

    private val viewModel: CalendarViewModel by viewModels {
        CalendarViewModelFactory()
    }

    private var _binding: FragmentCalendarBinding? = null
    private val binding get() = _binding!!

    private val token = "Bearer eyJhbGciOiJIUzI1NiIsInR5cCI6IkpXVCJ9.eyJ1c2VybmFtZSI6InVzZXIzIiwiaWF0IjoxNzEwNTg0MzUwLCJleHAiOjE3MTA2NzA3NTB9.hPaCqKJJVzF2NSQ1EQwI5RG9gl5hUc9c3RIJux9k55k"
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

        binding.rangeDate.setOnClickListener {

            dateRangePicker.show(parentFragmentManager, "Select dates")
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
                viewModel.createTrip(
                    token,
                    TripModel(
                    getDateTime(viewModel.startDate.value.toLong()),
                    getDateTime(viewModel.finishDate.value.toLong())))
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

        }

        viewModel.getTripListByUser(token)

        viewLifecycleOwner.lifecycleScope.launch(Dispatchers.IO) {
            viewModel.tripListByUser.collect{ list ->
                list.forEach { trip ->
                    binding.btnAddPlace.setOnClickListener {
                        if (selectedDay != "") {
                            val date = SimpleDateFormat("dd:MM:yyyy").parse(selectedDay)
                            println(date.time)
                            Log.d("My_TAG", "time: ${date.time} $selectedDay")
                            Log.d("My_TAG", "start: ${SimpleDateFormat("dd:MM:yyyy").parse(trip.date_start).time} ${trip.date_start} ")
                            Log.d("My_TAG", "finish: ${SimpleDateFormat("dd:MM:yyyy").parse(trip.date_finish).time} ${trip.date_finish}")

                            if (date.time >= SimpleDateFormat("dd:MM:yyyy").parse(trip.date_start).time
                                && date.time <= SimpleDateFormat("dd:MM:yyyy").parse(trip.date_finish).time) {
                                tripId = trip.id
                                Log.d(
                                    "My_TAG",
                                    "tripId: ${tripId}",
                                )
                            }
                        }

//                        val data = DataForTransfer(selectedDay, tripId)
                        val arrayData = arrayListOf(selectedDay, tripId.toString())

                        val bundle = Bundle().apply {
                            putStringArrayList("key", arrayData)
                        }
                        findNavController().navigate(
//                            MapFragmentDirections.actionMapFragmentToCalendarFragment(
//                                date = selectedDay,
//                                tripId = tripId
//                            )
                            R.id.action_calendarFragment_to_mapFragment, bundle
                        )
                    }
                }
            }
        }

binding.dayText.text = selectedDay


        viewModel.getDayListByUser(token)
        val rvAdapter = DayListByUserAdapter()
        binding.rvPlaceList.adapter = rvAdapter

        viewLifecycleOwner.lifecycleScope.launch {
            viewModel.dayListByUser.collect{
                Log.d("MY_TAG", "dayListByUser: $it")
                rvAdapter.submitList(it)
            }
        }

    }
}
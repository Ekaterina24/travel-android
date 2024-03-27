package com.example.travel.presentation.places

import android.R
import android.os.Build
import android.os.Bundle
import android.speech.tts.TextToSpeech
import android.util.Log
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.AdapterView
import android.widget.ArrayAdapter
import androidx.annotation.RequiresApi
import androidx.fragment.app.viewModels
import androidx.lifecycle.lifecycleScope
import androidx.navigation.fragment.findNavController
import com.example.travel.databinding.FragmentMapBinding
import com.example.travel.domain.model.AudioModel
import com.example.travel.domain.model.CityModel
import com.example.travel.domain.model.DayPlaceModel
import com.example.travel.domain.model.PlaceAudioModel
import com.example.travel.domain.model.PlaceModel
import com.example.travel.presentation.calendar.CalendarViewModel
import com.example.travel.presentation.calendar.CalendarViewModelFactory
import com.example.travel.presentation.calendar.DataForTransfer
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.flow.collect
import kotlinx.coroutines.flow.combine
import kotlinx.coroutines.flow.combineTransform
import kotlinx.coroutines.flow.flatMapMerge
import kotlinx.coroutines.flow.zip
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext
import java.util.Locale

class MapFragment : Fragment(), TextToSpeech.OnInitListener {

    private val viewModel: PlacesViewModel by viewModels {
        PlacesViewModelFactory()
    }

    private val viewModelCalendar: CalendarViewModel by viewModels {
        CalendarViewModelFactory()
    }

    private var _binding: FragmentMapBinding? = null
    private val binding get() = _binding!!

    private val token =
        "Bearer eyJhbGciOiJIUzI1NiIsInR5cCI6IkpXVCJ9.eyJ1c2VybmFtZSI6InVzZXIyIiwiaWF0IjoxNzExNTQ1MTgwLCJleHAiOjE3MTE2MzE1ODB9.I7y-2Vz_CtS6dcxm4lmGgheqq3nms-D9VjZiwfxdEtA"
    private var cityId = 0

    var placeId = ""
    private var tts: TextToSpeech? = null
    var text = ""

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        _binding = FragmentMapBinding.inflate(inflater, container, false)
        return binding.root
    }

    @RequiresApi(Build.VERSION_CODES.TIRAMISU)
    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        val args = arguments?.getStringArrayList("key")

        Log.d("MY_TAG", "args: ${args?.get(0)} ${args?.get(1)}")
//

        viewModel.getCityList()

        viewLifecycleOwner.lifecycleScope.launch {
            viewModel.cityList.collect {
                Log.d("MY_TAG", "onViewCreated: $it")

                val itemName = it.map { item -> item.name }
                val items = it

                withContext(Dispatchers.Main) {
                    val adapter =
                        ArrayAdapter(requireContext(), R.layout.simple_spinner_item, itemName)
                    adapter.setDropDownViewResource(R.layout.simple_spinner_dropdown_item)

                    val spinner = binding.spinnerCity
                    spinner.adapter = adapter

                    spinner.onItemSelectedListener = object : AdapterView.OnItemSelectedListener {
                        override fun onItemSelected(
                            parent: AdapterView<*>,
                            view: View,
                            position: Int,
                            id: Long
                        ) {
                            val itemSelected = parent.getItemAtPosition(position).toString()
                            items.map { city ->
                                if (city.name == itemSelected) {
                                    cityId = city.id
                                }
                            }

                            try {
                                viewModel.getPlaceList(cityId)

                            } catch (e: Exception) {
                                Log.e("MY_TAG", "onViewCreated: ${e.message}")
                            }
                        }

                        override fun onNothingSelected(parent: AdapterView<*>) {}
                    }


                }
            }
        }
        val rvAdapter = PlaceListAdapter(
            object : PlaceActionListener {
                override fun onChoosePlace(place: PlaceModel) {
                    Log.d("MY_TAG", "args: ${args?.get(1).toString().toLong()}}")
                    viewModelCalendar.addDayPlace(
                        token,
                        DayPlaceModel(
                            placeId = place.id,
                            dateVisiting = args?.get(0).toString(),
                            tripId = args?.get(1).toString().toInt(),
                        )
                    )
                }

                override fun getPlaceId(id: String) {
                    placeId = id
                    binding.container.visibility = View.VISIBLE
                    Log.d("MyLog", "placeId: $placeId")

                    viewModel.getAudioListByPlace(placeId)
                    viewModel.getPlaceById(placeId)

                    viewLifecycleOwner.lifecycleScope.launch(Dispatchers.IO) {
                        viewModel.place.collect { place ->
                            withContext(Dispatchers.Main) {
                                binding.name.text = place.name
                            }
                        }
                    }

                    viewLifecycleOwner.lifecycleScope.launch(Dispatchers.IO) {
                        viewModel.audioListByPlace.collect { audioList ->
                            withContext(Dispatchers.Main) {
//                                binding.audio.text = audioList.toString()
                                text = audioList[0].desc
                            }
                        }
                    }
                }

            }

        )
        binding.rvPlaceList.adapter = rvAdapter


        viewLifecycleOwner.lifecycleScope.launch {
            viewModel.placeList.collect {
                Log.d("MY_TAG", "onViewCreated: $it")
                rvAdapter.submitList(it)
            }
        }

        tts = TextToSpeech(context, this)
        binding.btnAudio.setOnClickListener {
            speakOut()
        }
    }

    override fun onInit(status: Int) {
        if (status == TextToSpeech.SUCCESS) {
            val result = tts!!.setLanguage(Locale("ru"))

            if (result == TextToSpeech.LANG_MISSING_DATA || result == TextToSpeech.LANG_NOT_SUPPORTED) {
                Log.e("TTS","The Language not supported!")
            } else {
                binding.btnAudio.isEnabled = true
            }
        }
    }

    private fun speakOut() {
        val text = text
        tts!!.speak(text, TextToSpeech.QUEUE_FLUSH, null,"")
    }

    override fun onDestroyView() {
        if (tts != null) {
            tts!!.stop()
            tts!!.shutdown()
        }
        super.onDestroyView()
    }

}
package com.example.travel.presentation

import android.os.Bundle
import android.util.Log
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.DialogFragment
import androidx.fragment.app.viewModels
import com.example.travel.R
import com.example.travel.databinding.FragmentMapBinding
import com.example.travel.databinding.FragmentPlaceDetailBinding
import com.example.travel.presentation.places.PlacesViewModel
import com.example.travel.presentation.places.PlacesViewModelFactory

class PlaceDetailFragment : DialogFragment() {
    private var _binding: FragmentPlaceDetailBinding? = null
    private val binding get() = _binding!!

    private val viewModel: PlacesViewModel by viewModels {
        PlacesViewModelFactory()
    }
    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        _binding = FragmentPlaceDetailBinding.inflate(inflater, container, false)
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        val args = arguments?.getString("key")
        Log.d("MyLog", "placeId: ${args}")
//        viewModel.audioListByPlace
    }


}
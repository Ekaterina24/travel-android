package com.example.travel.presentation.profile

import androidx.lifecycle.ViewModelProvider
import android.os.Bundle
import android.util.Log
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.viewModels
import androidx.lifecycle.lifecycleScope
import com.example.travel.R
import com.example.travel.data.local.prefs.SharedPreferences
import com.example.travel.databinding.FragmentProfileBinding
import com.example.travel.domain.model.PlaceModel
import com.example.travel.presentation.auth.AuthViewModel
import com.example.travel.presentation.auth.AuthViewModelFactory
import com.example.travel.presentation.places.PlaceActionListener
import com.example.travel.presentation.places.PlaceListAdapter
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext

class ProfileFragment : Fragment() {

    companion object {
        fun newInstance() = ProfileFragment()
    }

//    private lateinit var viewModel: ProfileViewModel

    private val viewModelProfile: ProfileViewModel by viewModels {
        ProfileViewModelFactory()
    }

    private var _binding: FragmentProfileBinding? = null
    private val binding get() = _binding!!

    private val sharedPreferences: SharedPreferences by lazy {
        SharedPreferences(requireContext())
    }

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        _binding = FragmentProfileBinding.inflate(inflater, container, false)
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        val token = "Bearer ${sharedPreferences.getStringValue("token")}"
        Log.d("TAG", "onViewCreated: $token")
//       viewModelProfile.cashUserProfile(token)
        viewModelProfile.uploadUserProfile(token)
        viewLifecycleOwner.lifecycleScope.launch(Dispatchers.IO) {
            viewModelProfile.userProfile.collect {
                    binding.tvName.text = it.username
                Log.d("TAG", "onViewCreated: ${it.username}")
            }
        }

        viewModelProfile.uploadPlaceList()

        val rvAdapter = PlaceListAdapter(
            object : PlaceActionListener {

                override fun getPlaceId(genId: Long) {
//                    placeId = id
//                    viewModel.uploadPlaceFromDb(genId)
//
////                    binding.container.visibility = View.VISIBLE
////                    Log.d("MyLog", "placeId: $placeId")
////
////                    viewModel.getAudioListByPlace(placeId)
////                    viewModel.getPlaceById(placeId)
//
//                    viewLifecycleOwner.lifecycleScope.launch(Dispatchers.IO) {
//                        viewModel.place.collect { place ->
//
//                            placeIsFav = !place.is_favourite
//                            Log.d("TAG", "getPlaceId: $genId, placeIsNotFav $placeIsFav")
//                            viewModel.updatePlaceFavorite(placeIsFav, genId)
////                            withContext(Dispatchers.Main) {
////                                binding.im
////                            }
//                        }
//                    }

                }

            }

        )


        binding.rvPlaceList.adapter = rvAdapter

        viewLifecycleOwner.lifecycleScope.launch(Dispatchers.IO) {
            viewModelProfile.placeList.collect {
//                Log.d("MY_TAG", "places: ${it.size}")
                withContext(Dispatchers.Main) {
                    val newList = mutableListOf<PlaceModel>()
                    it.map { item ->
                        if (item.is_visited) {
                            newList.add(item)
                        }
                    }
//                    rvAdapterPlace.submitList(newList)
                    Log.d("MY_TAG", "newList: ${newList}")
                    rvAdapter.submitList(newList)
                }

            }
        }
    }


}
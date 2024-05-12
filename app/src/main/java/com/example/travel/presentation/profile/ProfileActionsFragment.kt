package com.example.travel.presentation.profile

import android.os.Bundle
import android.util.Log
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.activityViewModels
import androidx.lifecycle.lifecycleScope
import com.example.travel.data.local.prefs.SharedPreferences
import com.example.travel.databinding.FragmentProfileActionsBinding
import com.example.travel.domain.model.CreateSubscribeModel
import com.example.travel.domain.model.PlaceModel
import com.example.travel.domain.model.TypeSubModel
import com.example.travel.domain.model.UserSubscribeAdapterModel
import com.example.travel.domain.model.review.ReviewAdapterModel
import com.example.travel.presentation.places.PlaceActionListener
import com.example.travel.presentation.places.PlaceListAdapter
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext
import java.text.SimpleDateFormat
import java.util.Date
import java.util.Locale

class ProfileActionsFragment : Fragment() {

    companion object {
        fun newInstance() = ProfileActionsFragment()
    }

    private val viewModelProfile: ProfileViewModel by activityViewModels {
        ProfileViewModelFactory()
    }

    private var _binding: FragmentProfileActionsBinding? = null
    private val binding get() = _binding!!

    private val sharedPreferences: SharedPreferences by lazy {
        SharedPreferences(requireContext())
    }

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        _binding = FragmentProfileActionsBinding.inflate(inflater, container, false)
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        val token = "Bearer ${sharedPreferences.getStringValue("token")}"
        val city = sharedPreferences.getStringValue("city")

//        viewModelProfile.uploadPlaceList()
//
//        val rvAdapter = PlaceListAdapter(
//            object : PlaceActionListener {
//
//                override fun getPlaceId(genId: Long) {
////                    placeId = id
////                    viewModel.uploadPlaceFromDb(genId)
////
//////                    binding.container.visibility = View.VISIBLE
//////                    Log.d("MyLog", "placeId: $placeId")
//////
//////                    viewModel.getAudioListByPlace(placeId)
//////                    viewModel.getPlaceById(placeId)
////
////                    viewLifecycleOwner.lifecycleScope.launch(Dispatchers.IO) {
////                        viewModel.place.collect { place ->
////
////                            placeIsFav = !place.is_favourite
////                            Log.d("TAG", "getPlaceId: $genId, placeIsNotFav $placeIsFav")
////                            viewModel.updatePlaceFavorite(placeIsFav, genId)
//////                            withContext(Dispatchers.Main) {
//////                                binding.im
//////                            }
////                        }
////                    }
//
//                }
//
//            }
//
//        )
//
//
//        binding.rvPlaceList.adapter = rvAdapter
//
//        viewLifecycleOwner.lifecycleScope.launch(Dispatchers.IO) {
//            viewModelProfile.placeList.collect {
//                withContext(Dispatchers.Main) {
//                    val newList = mutableListOf<PlaceModel>()
//                    it.map { item ->
//                        if (item.is_visited) {
//                            newList.add(item)
//                        }
//                    }
//                    Log.d("MY_TAG", "newList: ${newList}")
//                    rvAdapter.submitList(newList)
//                }
//
//            }
//        }


        viewModelProfile.getTypeSubList()
        val rvTypeSubAdapter = TypeSubListAdapter(
            object : TypeSubActionListener {
                override fun onChooseTypeSub(typeSub: TypeSubModel) {
                    viewModelProfile.createSubscribe(
                        token = token,
                        subscribe = CreateSubscribeModel(
                            typeId = typeSub.id,
                            city = city.toString()
                        )
                    )
                }

            }
        )

        binding.rvTypeSubList.adapter = rvTypeSubAdapter
        viewLifecycleOwner.lifecycleScope.launch(Dispatchers.IO) {
            viewModelProfile.typeSubList.collect {
                Log.d("MY_TAG", "typeSubList: ${it} ${it.size}")
                withContext(Dispatchers.Main) {
                    rvTypeSubAdapter.submitList(it)
                }
            }
        }

        val rvSubscribeAdapter = SubscribeListAdapter()
        binding.rvSubscribeList.adapter = rvSubscribeAdapter
        viewModelProfile.getSubscribeListByUser(token)
        viewLifecycleOwner.lifecycleScope.launch(Dispatchers.IO) {
            viewModelProfile.typeSubList.collect { typeSubList ->
                viewModelProfile.subscribeListByUser.collect { subscribeListByUser ->

                    val newList = mutableListOf<UserSubscribeAdapterModel>()

                    subscribeListByUser.forEach { sub ->
                        typeSubList.firstOrNull { it.id == sub.typeId }?.let { type ->
                            newList.add(
                                UserSubscribeAdapterModel(
                                    date_start = sub.date,
                                    period = type.period,
                                    city = sub.city,
                                    id = sub.id
                                )
                            )
                        }
                    }


//                        subscribeListByUser.map { sub ->
//                            typeSubList.map { type ->
//                                if (type.id == sub.typeId) {
//                                    newList.add(
//                                        UserSubscribeAdapterModel(
//                                            date_start = sub.date,
//                                            period = type.period,
//                                            city = sub.city,
//                                            id = sub.id
//                                        )
//                                    )
//                                }
//
//                            }
//                        }
                    withContext(Dispatchers.Main) {
                        rvSubscribeAdapter.submitList(newList)
                    }
                }
            }
        }
    }


}
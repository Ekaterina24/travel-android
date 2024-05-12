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
import com.example.travel.databinding.FragmentProfileRatingBinding
import com.example.travel.domain.model.CreateSubscribeModel
import com.example.travel.domain.model.PlaceModel
import com.example.travel.domain.model.TypeSubModel
import com.example.travel.domain.model.UserProfileModel
import com.example.travel.domain.model.UserSubscribeAdapterModel
import com.example.travel.domain.model.review.ReviewAdapterModel
import com.example.travel.presentation.places.PlaceActionListener
import com.example.travel.presentation.places.PlaceListAdapter
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.async
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext
import java.text.SimpleDateFormat
import java.util.Date
import java.util.Locale

class ProfileRatingFragment : Fragment() {

    companion object {
        fun newInstance() = ProfileRatingFragment()
    }

    private val viewModelProfile: ProfileViewModel by activityViewModels {
        ProfileViewModelFactory()
    }

    private var _binding: FragmentProfileRatingBinding? = null
    private val binding get() = _binding!!

    private val sharedPreferences: SharedPreferences by lazy {
        SharedPreferences(requireContext())
    }

    private lateinit var currentUser: UserProfileModel

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        _binding = FragmentProfileRatingBinding.inflate(inflater, container, false)
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        val token = "Bearer ${sharedPreferences.getStringValue("token")}"
        val city = sharedPreferences.getStringValue("city")

        viewModelProfile.uploadPlaceList()

        val rvAdapter = PlaceListAdapter(
            object : PlaceActionListener {

                override fun getPlaceId(genId: Long) {


                }

            }

        )


        binding.rvPlaceList.adapter = rvAdapter

        viewLifecycleOwner.lifecycleScope.launch(Dispatchers.IO) {
            viewModelProfile.placeList.collect {
                withContext(Dispatchers.Main) {
                    val newList = mutableListOf<PlaceModel>()
                    it.map { item ->
                        if (item.is_visited) {
                            newList.add(item)
                        }
                    }
                    Log.d("MY_TAG", "newList: ${newList}")
                    rvAdapter.submitList(newList)
                }

            }
        }

        viewModelProfile.getUserList()
        viewModelProfile.uploadUserProfile(token)
        viewLifecycleOwner.lifecycleScope.launch(Dispatchers.IO) {
            viewModelProfile.userProfile.collect {
                currentUser = it
            }
        }
        val rvRatingAdapter = RatingListAdapter(object : UserIdentification {
            override fun isCurrentUser(userId: Long): Boolean {
                return currentUser.id == userId
            }
        }
        )
        binding.rvRating.adapter = rvRatingAdapter

        viewLifecycleOwner.lifecycleScope.launch(Dispatchers.IO) {
            viewModelProfile.userList.collect {
                withContext(Dispatchers.Main) {
                    rvRatingAdapter.submitList(it)
                }

            }
        }


    }


}
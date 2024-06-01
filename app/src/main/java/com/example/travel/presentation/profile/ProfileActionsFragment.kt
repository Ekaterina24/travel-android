package com.example.travel.presentation.profile

import android.os.Bundle
import android.util.Log
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Toast
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
import com.musfickjamil.snackify.Snackify
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

        viewModelProfile.getTypeSubList()
        val rvTypeSubAdapter = TypeSubListAdapter(
            object : TypeSubActionListener {
                override fun onChooseTypeSub(typeSub: TypeSubModel) {
                    lifecycleScope.launch(Dispatchers.IO) {
                        val isSubscribed = viewModelProfile.subscribeListByUser.value.any { sub ->
                            sub.city == city
                        }

                        withContext(Dispatchers.Main) {
                            if (isSubscribed) {
                                Snackify.info(
                                    binding.rvTypeSubList,
                                    "У вас есть активная подписка на город $city!",
                                    Toast.LENGTH_SHORT
                                ).show()
                            } else {
                                viewModelProfile.createSubscribe(
                                    token = token,
                                    subscribe = CreateSubscribeModel(
                                        typeId = typeSub.id,
                                        city = city.toString()
                                    )
                                )
                                Snackify.success(
                                    binding.rvTypeSubList,
                                    "Подписка на город $city активирована!",
                                    Toast.LENGTH_SHORT
                                ).show()
                            }
                        }
                    }

                }

            }
        )

        binding.rvTypeSubList.adapter = rvTypeSubAdapter
        viewLifecycleOwner.lifecycleScope.launch(Dispatchers.IO) {
            viewModelProfile.typeSubList.collect {
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
                    withContext(Dispatchers.Main) {
                        rvSubscribeAdapter.submitList(newList)
                    }
                }
            }
        }
    }


}
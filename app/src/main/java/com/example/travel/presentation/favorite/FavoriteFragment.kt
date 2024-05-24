package com.example.travel.presentation.favorite

import android.os.Bundle
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.activityViewModels
import androidx.fragment.app.viewModels
import androidx.lifecycle.Lifecycle
import androidx.lifecycle.flowWithLifecycle
import androidx.lifecycle.lifecycleScope
import com.example.travel.R
import com.example.travel.data.local.prefs.SharedPreferences
import com.example.travel.databinding.FragmentCalendarBinding
import com.example.travel.databinding.FragmentFavoriteBinding
import com.example.travel.domain.model.PlaceModel
import com.example.travel.presentation.places.PlaceActionListener
import com.example.travel.presentation.places.PlaceListAdapter
import com.example.travel.presentation.places.PlacesViewModel
import com.example.travel.presentation.places.PlacesViewModelFactory
import com.example.travel.presentation.utils.textChangedFlow
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.flow.collectLatest
import kotlinx.coroutines.flow.debounce
import kotlinx.coroutines.flow.flowOn
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext

class FavoriteFragment : Fragment() {

    private var _binding: FragmentFavoriteBinding? = null
    private val binding get() = _binding!!

    private val viewModelPlace: PlacesViewModel by activityViewModels {
        PlacesViewModelFactory()
    }

    private var category = ""
    private var search = ""
    private lateinit var rvAdapter: PlaceListAdapter

    private val sharedPreferences: SharedPreferences by lazy {
        SharedPreferences(requireContext())
    }

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        _binding = FragmentFavoriteBinding.inflate(inflater, container, false)
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        var cityId = sharedPreferences.getStringValue("pos")?.toInt() ?: 1
        cityId += 1

        rvAdapter = PlaceListAdapter(
            object : PlaceActionListener {
                override fun getPlaceId(genId: Long) {
                    viewModelPlace.uploadPlaceFromDb(genId, cityId, search, category) //get
                }
            }
        )

        binding.rvPlaceList.adapter = rvAdapter

        viewLifecycleOwner.lifecycleScope.launch {
            val fullList = mutableListOf<PlaceModel>()

            launch(Dispatchers.IO) {
                viewModelPlace.placeList.collect { list ->
                    fullList.clear()
                    list.forEach {  item ->
                        if (item.is_favourite) {
                            fullList.add(item)
                        }
                    }
                    withContext(Dispatchers.Main) {
                        filterPlaces(fullList, search)
                    }
                }
            }

            binding.searchView.textChangedFlow()
                .flowWithLifecycle(lifecycle, Lifecycle.State.CREATED)
                .debounce(50)
                .collectLatest { textSearch ->
                    search = textSearch.lowercase()
                    filterPlaces(fullList, search)
                }
        }

        viewModelPlace.getPlaceListData(
            cityId,
            search,
            category
        )

    }

    private fun filterPlaces(fullList: List<PlaceModel>, search: String) {
        val filteredList = if (search.isNotEmpty()) {
            fullList.filter { it.name.contains(search, ignoreCase = true) }
        } else {
            fullList
        }
        rvAdapter.submitList(filteredList)
    }

}
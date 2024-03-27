package com.example.travel

import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.util.Log
import androidx.activity.viewModels
import androidx.lifecycle.Lifecycle
import androidx.lifecycle.flowWithLifecycle
import androidx.lifecycle.lifecycleScope
import androidx.navigation.NavController
import androidx.navigation.fragment.NavHostFragment
import androidx.navigation.ui.setupWithNavController
import com.example.travel.databinding.ActivityMainBinding
import com.example.travel.presentation.places.PlacesViewModel
import com.example.travel.presentation.places.PlacesViewModelFactory
import kotlinx.coroutines.flow.catch
import kotlinx.coroutines.flow.filterNotNull
import kotlinx.coroutines.launch

class MainActivity : AppCompatActivity() {

    private val viewModel: PlacesViewModel by viewModels {
        PlacesViewModelFactory()
    }

    private lateinit var navController: NavController
    private lateinit var binding: ActivityMainBinding

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityMainBinding.inflate(layoutInflater)
        setContentView(binding.root)

        val navHostFragment = supportFragmentManager
            .findFragmentById(R.id.fragment_container) as NavHostFragment
        navController = navHostFragment.navController
//        setupActionBarWithNavController(navController)

        binding.bottomNav.setupWithNavController(navController)


//        viewModel.getPlaceList()

        lifecycleScope.launch {
            viewModel.placeList
                .flowWithLifecycle(lifecycle, Lifecycle.State.CREATED)
                .catch { e ->
                    Log.d(
                        "MY_TAG",
                        "Error viewModel.placeList + ${e.localizedMessage ?: e.message}"
                    )
                }
                .filterNotNull()
                .collect { places ->
//                    Log.d("MY_TAG","places: ${places}")
                }
        }
    }
}
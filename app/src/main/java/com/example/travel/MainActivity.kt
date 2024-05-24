package com.example.travel

import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.util.Log
import android.view.View
import androidx.activity.viewModels
import androidx.lifecycle.Lifecycle
import androidx.lifecycle.flowWithLifecycle
import androidx.lifecycle.lifecycleScope
import androidx.navigation.NavController
import androidx.navigation.findNavController
import androidx.navigation.fragment.NavHostFragment
import androidx.navigation.ui.setupWithNavController
import com.example.travel.data.local.prefs.SharedPreferences
import com.example.travel.databinding.ActivityMainBinding
import com.example.travel.presentation.PermissionUtils
import com.example.travel.presentation.places.PlacesViewModel
import com.example.travel.presentation.places.PlacesViewModelFactory
import kotlinx.coroutines.flow.catch
import kotlinx.coroutines.flow.filterNotNull
import kotlinx.coroutines.launch
import java.text.SimpleDateFormat
import java.util.Date
import java.util.Locale

class MainActivity : AppCompatActivity() {

    private lateinit var navController: NavController
    lateinit var binding: ActivityMainBinding

    private val topDestinationIds = setOf(
        R.id.authFragment
    )

    private val sharedPreferences: SharedPreferences by lazy {
        SharedPreferences(this)
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityMainBinding.inflate(layoutInflater)
        setContentView(binding.root)

        val token = sharedPreferences.getStringValue("token")
//        PermissionUtils(this).checkPermissions()

        val navHostFragment = supportFragmentManager
            .findFragmentById(R.id.fragment_container) as NavHostFragment
        navController = navHostFragment.navController

        binding.bottomNav.setupWithNavController(navController)

//        navController.addOnDestinationChangedListener { _, destination, _ ->
//            if (destination.id in topDestinationIds) {
//                binding.bottomNav.visibility = View.GONE
//            } else {
//                binding.bottomNav.visibility = View.VISIBLE
//            }
//        }

        binding.bottomNav.setOnNavigationItemSelectedListener { menuItem ->
            when (menuItem.itemId) {
                R.id.calendarFragment -> {
                    if (!checkAuthTime()) {
                        findNavController(R.id.fragment_container).navigate(R.id.requireRegisterFragment)
                    } else {
                        findNavController(R.id.fragment_container).navigate(R.id.calendarFragment)
                    }
                    true
                }
                R.id.weatherFragment -> {
                    if (!checkAuthTime()) {
                        findNavController(R.id.fragment_container).navigate(R.id.requireRegisterFragment)
                    } else {
                        findNavController(R.id.fragment_container).navigate(R.id.weatherFragment)
                    }
                    true
                }
                R.id.controlProfileFragment -> {
                    if (!checkAuthTime()) {
                        findNavController(R.id.fragment_container).navigate(R.id.requireRegisterFragment)
                    } else {
                        findNavController(R.id.fragment_container).navigate(R.id.controlProfileFragment)
                    }
                    true
                }
                R.id.favoriteFragment -> {
                    if (!checkAuthTime()) {
                        findNavController(R.id.fragment_container).navigate(R.id.requireRegisterFragment)
                    } else {
                        findNavController(R.id.fragment_container).navigate(R.id.favoriteFragment)
                    }
                    true
                }
                R.id.mapFragment -> {
                    findNavController(R.id.fragment_container).navigate(R.id.mapFragment)
                    true
                }
                else -> false
            }
        }
    }

    fun saveAuthTime() {
        val format = SimpleDateFormat("dd-MM-yyyy HH:mm:ss", Locale.getDefault())
        sharedPreferences.save("AUTHORISATION_TIME", format.format(Date()).toString())
    }

    fun checkAuthTime(): Boolean {
        try {
            val format = SimpleDateFormat("dd-MM-yyyy HH:mm:ss", Locale.getDefault())
            val time = format.parse(
                sharedPreferences.getStringValue("AUTHORISATION_TIME") ?: return false
            )?.time ?: return false
            return Date().time < (time + 1000 * 60 * 60 * 24)
        } catch (_: Exception) {
        }
        return false
    }



}
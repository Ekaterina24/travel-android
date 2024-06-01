package com.example.travel.presentation.profile

import android.content.Intent
import android.graphics.Color
import android.os.Bundle
import android.util.Log
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Toast
import androidx.appcompat.app.AppCompatDelegate
import androidx.fragment.app.activityViewModels
import androidx.lifecycle.lifecycleScope
import androidx.navigation.fragment.findNavController
import com.example.travel.MainActivity
import com.example.travel.R
import com.example.travel.data.local.prefs.SharedPreferences
import com.example.travel.databinding.FragmentProfileBinding
import com.example.travel.domain.model.UpdateEmailRequest
import com.example.travel.domain.model.UpdateScoresRequest
import com.example.travel.domain.model.UserProfileModel
import com.google.android.material.dialog.MaterialAlertDialogBuilder
import com.musfickjamil.snackify.Snackify
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext

class ProfileFragment : Fragment() {

    companion object {
        fun newInstance() = ProfileFragment()
    }

    private val themeTitleList = arrayOf("Light", "Dark")

    private val viewModelProfile: ProfileViewModel by activityViewModels {
        ProfileViewModelFactory()
    }

    private var _binding: FragmentProfileBinding? = null
    private val binding get() = _binding!!

    private val sharedPreferences: SharedPreferences by lazy {
        SharedPreferences(requireContext())
    }

    private lateinit var currentUser: UserProfileModel

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        _binding = FragmentProfileBinding.inflate(inflater, container, false)
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        var checkedTheme = sharedPreferences.theme
        Log.d("TAG", "checkedTheme: $checkedTheme")
        binding.chosenTheme.setText("Светлая тема")
        when(themeTitleList[sharedPreferences.theme!!]) {
            "Light" -> {
                binding.chosenTheme.setText("Светлая тема")
                binding.switchTheme.isChecked = false
            }
            "Dark" -> {
                binding.chosenTheme.setText("Темная тема")
                binding.switchTheme.isChecked = true
            }
        }

        val token = "Bearer ${sharedPreferences.getStringValue("token")}"
        val city = sharedPreferences.getStringValue("city")
        Log.d("TAG", "onViewCreated: $token")
//       viewModelProfile.cashUserProfile(token)

//        if (sharedPreferences.getStringValue("token")?.isNotEmpty() == true) {
        viewModelProfile.uploadUserProfile(token)
//        }

        binding.switchTheme.isChecked = false
        binding.switchTheme.setOnCheckedChangeListener { compoundButton, isChecked ->
            when (isChecked) {
                true -> checkedTheme = 1
                false -> checkedTheme = 0
            }
            sharedPreferences.theme = checkedTheme
            AppCompatDelegate.setDefaultNightMode(sharedPreferences.themeFlags[checkedTheme!!])
        }

        viewLifecycleOwner.lifecycleScope.launch(Dispatchers.IO) {
            viewModelProfile.userProfile.collect {
                withContext(Dispatchers.Main) {
                    binding.etEmail.setText(it.email)
                    Log.d("TAG", "email: ${it.email}")
                    currentUser = it

                    binding.etEmail.apply {
                        isFocusable = false
                        isFocusableInTouchMode = false
                        isCursorVisible = false
                    }
                }

            }
        }


        binding.btnEdit.setOnClickListener {
            it.isEnabled = false
            binding.btnSave.isEnabled = true
            binding.btnSave.setBackgroundColor(requireContext().getColor(R.color.green))
            binding.btnEdit.setBackgroundColor(requireContext().getColor(R.color.gray))
            binding.etEmail.apply {
                isFocusable = true
                isFocusableInTouchMode = true
                isCursorVisible = true
            }

        }

        binding.btnSave.isEnabled = false
        binding.btnSave.setBackgroundColor(requireContext().getColor(R.color.gray))
        binding.btnSave.setOnClickListener {
            viewModelProfile.updateEmailFromApi(token, UpdateEmailRequest(binding.etEmail.text.toString()))
            Snackify.success(binding.btnSave, "Почта сохранена", Toast.LENGTH_SHORT).show()
            binding.btnEdit.setBackgroundColor(requireContext().getColor(R.color.blue))
            it.isEnabled = false
            binding.btnEdit.isEnabled = true
            binding.etEmail.apply {
                isFocusable = false
                isFocusableInTouchMode = false
                isCursorVisible = false
            }
            binding.btnSave.setBackgroundColor(requireContext().getColor(R.color.gray))
        }

        binding.btnExit.setOnClickListener {
            sharedPreferences.removeStringValue("token")
            sharedPreferences.removeStringValue("AUTHORISATION_TIME")
            findNavController().navigate(R.id.action_controlProfileFragment_to_mapFragment)
        }


    }


}
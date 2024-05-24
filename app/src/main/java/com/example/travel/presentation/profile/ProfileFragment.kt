package com.example.travel.presentation.profile

import android.content.Intent
import android.os.Bundle
import android.util.Log
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.appcompat.app.AppCompatDelegate
import androidx.fragment.app.activityViewModels
import androidx.lifecycle.lifecycleScope
import androidx.navigation.fragment.findNavController
import com.example.travel.MainActivity
import com.example.travel.R
import com.example.travel.data.local.prefs.SharedPreferences
import com.example.travel.databinding.FragmentProfileBinding
import com.google.android.material.dialog.MaterialAlertDialogBuilder
import com.musfickjamil.snackify.Snackify
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext

class ProfileFragment : Fragment() {

    companion object {
        fun newInstance() = ProfileFragment()
    }

    private val themeTitleList = arrayOf("Light", "Dark", "Auto")

    private val viewModelProfile: ProfileViewModel by activityViewModels {
        ProfileViewModelFactory()
    }

    private var _binding: FragmentProfileBinding? = null
    private val binding get() = _binding!!

    private val sharedPreferences: SharedPreferences by lazy {
        SharedPreferences(requireContext())
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

//        if (sharedPreferences.getBooleanValue("theme") == true) {
//            (activity as MainActivity).setTheme(R.style.AppTheme_Dark)
//        } else {
//            (activity as MainActivity).setTheme(R.style.Theme_Travel)
//        }
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

        var checkedTheme = sharedPreferences.theme

        binding.etEmail.setText("Theme ${themeTitleList[sharedPreferences.theme!!]}")

        val themeDialog = MaterialAlertDialogBuilder(requireContext())
            .setTitle("Theme")
            .setPositiveButton("Ok") { _, _ ->
                sharedPreferences.theme = checkedTheme
                AppCompatDelegate.setDefaultNightMode(sharedPreferences.themeFlags[checkedTheme!!])
                binding.etEmail.setText("Theme ${themeTitleList[sharedPreferences.theme!!]}")
            }

            .setSingleChoiceItems(themeTitleList, checkedTheme!!) { _, which ->
                checkedTheme = which
            }
            .setCancelable(false)

//        if (sharedPreferences.getBooleanValue("theme") == true) {
//            (activity as MainActivity).setTheme(R.style.AppTheme_Dark)
//        } else {
//            (activity as MainActivity).setTheme(R.style.AppTheme_Light)
//        }

        val token = "Bearer ${sharedPreferences.getStringValue("token")}"
        val city = sharedPreferences.getStringValue("city")
        Log.d("TAG", "onViewCreated: $token")
//       viewModelProfile.cashUserProfile(token)

//        if (sharedPreferences.getStringValue("token")?.isNotEmpty() == true) {
        viewModelProfile.uploadUserProfile(token)
//        }

//        binding.switchTheme.isChecked = sharedPreferences.getBooleanValue("theme")!!


        binding.switchTheme.setOnCheckedChangeListener { compoundButton, isChecked ->
            if (isChecked) {
                themeDialog.show()
//                sharedPreferences.saveBoolean("theme", true)
//                restartApp()
            } else {
                sharedPreferences.saveBoolean("theme", false)
                restartApp()
            }
        }

        viewLifecycleOwner.lifecycleScope.launch(Dispatchers.IO) {
            viewModelProfile.userProfile.collect {
                withContext(Dispatchers.Main) {
                    binding.etEmail.setText(it.email)

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
            binding.etEmail.apply {
                isFocusable = true
                isFocusableInTouchMode = true
                isCursorVisible = true
            }

        }

        binding.btnSave.isEnabled = false
        binding.btnSave.setOnClickListener {
            it.isEnabled = false
            binding.btnEdit.isEnabled = true
            binding.etEmail.apply {
                isFocusable = false
                isFocusableInTouchMode = false
                isCursorVisible = false
            }

            Snackify.success(binding.btnEdit, "Данные изменены", Snackify.LENGTH_SHORT).show()
        }

        binding.btnExit.setOnClickListener {
            sharedPreferences.removeStringValue("token")
            sharedPreferences.removeStringValue("AUTHORISATION_TIME")
            findNavController().navigate(R.id.action_controlProfileFragment_to_mapFragment)
        }


    }

    fun restartApp() {
        val i = Intent(requireContext().applicationContext, MainActivity::class.java)
        i.flags = Intent.FLAG_ACTIVITY_NEW_TASK or Intent.FLAG_ACTIVITY_CLEAR_TASK
        startActivity(i)
        (activity as MainActivity).finish()
    }


}
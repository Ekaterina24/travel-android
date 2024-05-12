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
import com.example.travel.databinding.FragmentProfileBinding
import com.musfickjamil.snackify.Snackify
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext

class ProfileFragment : Fragment() {

    companion object {
        fun newInstance() = ProfileFragment()
    }

    private val viewModelProfile: ProfileViewModel by activityViewModels {
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
        val city = sharedPreferences.getStringValue("city")
        Log.d("TAG", "onViewCreated: $token")
//       viewModelProfile.cashUserProfile(token)
        viewModelProfile.uploadUserProfile(token)
        viewLifecycleOwner.lifecycleScope.launch(Dispatchers.IO) {
            viewModelProfile.userProfile.collect {
                withContext(Dispatchers.Main) {
                    binding.etName.setText(it.username)
                    binding.etEmail.setText(it.email)

                    binding.etName.apply {
                        isFocusable = false
                        isFocusableInTouchMode = false
                        isCursorVisible = false
                    }
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
            binding.etName.apply {
                isFocusable = true
                isFocusableInTouchMode = true
                isCursorVisible = true
            }
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
            Log.d("TAG", "updateUser: ${binding.etName.text} ${binding.etEmail.text}")
            binding.etName.apply {
                isFocusable = false
                isFocusableInTouchMode = false
                isCursorVisible = false
            }
            binding.etEmail.apply {
                isFocusable = false
                isFocusableInTouchMode = false
                isCursorVisible = false
            }

            Snackify.success(binding.btnEdit, "Данные изменены", Snackify.LENGTH_SHORT).show()
        }

    }


}
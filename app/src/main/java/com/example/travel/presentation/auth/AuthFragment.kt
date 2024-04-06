package com.example.travel.presentation.auth

import android.os.Bundle
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.Fragment
import androidx.fragment.app.viewModels
import androidx.lifecycle.lifecycleScope
import androidx.navigation.fragment.findNavController
import com.example.travel.R
import com.example.travel.databinding.FragmentAuthBinding
import com.example.travel.domain.model.LoginModel
import com.example.travel.domain.model.RegisterModel
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext
import com.example.travel.data.local.prefs.SharedPreferences


class AuthFragment : Fragment() {

    private val viewModel: AuthViewModel by viewModels {
        AuthViewModelFactory()
    }

    private val sharedPreferences: SharedPreferences by lazy {
        SharedPreferences(requireContext())
    }

    private var _binding: FragmentAuthBinding? = null
    private val binding get() = _binding!!

    private lateinit var name: String
    private lateinit var email: String
    private lateinit var password: String

//    private val token = "Bearer eyJhbGciOiJIUzI1NiIsInR5cCI6IkpXVCJ9.eyJ1c2VybmFtZSI6InVzZXIyIiwiaWF0IjoxNzExNTQ1MTgwLCJleHAiOjE3MTE2MzE1ODB9.I7y-2Vz_CtS6dcxm4lmGgheqq3nms-D9VjZiwfxdEtA"

//    private var arrayData = mutableListOf<String>()
//    private val args by navArgs<CalendarFragmentArgs>()

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        _binding = FragmentAuthBinding.inflate(inflater, container, false)
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

//        with(binding) {
//            name = tvName.text.toString()
//            email = tvEmail.text.toString()
//            password = tvPassword.text.toString()
//        }

        binding.btnRegister.setOnClickListener {
            with(binding) {
                name = tvName.text.toString()
                email = tvEmail.text.toString()
                password = tvPassword.text.toString()
            }
            viewModel.registerUser(RegisterModel(name, email, password))
        }

        binding.btnLogin.setOnClickListener {
            with(binding) {
                email = tvEmail.text.toString()
                password = tvPassword.text.toString()
            }
            viewModel.loginUser(LoginModel("user5@yandex.ru", "Df4f%bn%d"))

            viewLifecycleOwner.lifecycleScope.launch {
                viewModel.userToken.collect {
                    withContext(Dispatchers.Main) {
                        sharedPreferences.save("token", it.accessToken)
                    }
                }
            }

            findNavController().navigate(R.id.action_authFragment_to_mapFragment)
        }

binding.etName.setOnClickListener {
    if (sharedPreferences.getStringValue("token") != null) {
        binding.etName.text = "Token"
    }
}
    }
}
package com.example.travel.presentation.auth

import android.os.Bundle
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.core.widget.doAfterTextChanged
import androidx.fragment.app.Fragment
import androidx.fragment.app.activityViewModels
import androidx.lifecycle.lifecycleScope
import androidx.navigation.fragment.findNavController
import com.example.travel.MainActivity
import com.example.travel.R
import com.example.travel.data.local.prefs.SharedPreferences
import com.example.travel.databinding.FragmentAuthBinding
import com.example.travel.domain.ApiResponse
import com.example.travel.domain.model.LoginModel
import com.musfickjamil.snackify.Snackify
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.async
import kotlinx.coroutines.flow.collectLatest
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext


class AuthFragment : Fragment() {

    private val viewModel: AuthViewModel by activityViewModels {
        AuthViewModelFactory()
    }

    private val sharedPreferences: SharedPreferences by lazy {
        SharedPreferences(requireContext())
    }

    private var _binding: FragmentAuthBinding? = null
    private val binding get() = _binding!!

    private lateinit var email: String
    private lateinit var password: String


    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        _binding = FragmentAuthBinding.inflate(inflater, container, false)
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        binding.tvEmail.doAfterTextChanged {
            email = it.toString()
            Log.d("MY_TAG", "email: $it")
        }

        binding.tvPassword.doAfterTextChanged {
            password = it.toString()
        }

        binding.btnSkip.setOnClickListener {
            findNavController().navigate(R.id.action_controlFragment_to_mapFragment)
            (activity as MainActivity).binding.bottomNav.visibility = View.VISIBLE
        }

        binding.btnLogin.setOnClickListener {

            viewLifecycleOwner.lifecycleScope.launch(Dispatchers.IO) {
                with(binding) {
                    email = tvEmail.text.toString()
                    password = tvPassword.text.toString()
                }
                viewModel.loginUser(LoginModel(email, password))

                viewLifecycleOwner.lifecycleScope.launch(Dispatchers.IO) {
                    viewModel.loginResult.collectLatest {
                        when (it) {
                            is ApiResponse.Success -> {
                                async(Dispatchers.Main) {
                                    binding.progressBar.visibility = View.VISIBLE
                                    sharedPreferences.save("token", it.value.accessToken)
                                    (activity as MainActivity).saveAuthTime()
                                }.await()
                                withContext(Dispatchers.Main) {
                                    (activity as MainActivity).binding.bottomNav.visibility =
                                        View.VISIBLE
                                    binding.progressBar.visibility = View.GONE
                                    findNavController().navigate(R.id.action_controlFragment_to_mapFragment)
                                }
                            }

                            is ApiResponse.Failure -> {
                                Snackify.error(
                                    binding.linearLayout,
                                    "${it.message}",
                                    Snackify.LENGTH_LONG
                                ).show()
                            }
                        }
                    }
                }
            }
        }
    }
}
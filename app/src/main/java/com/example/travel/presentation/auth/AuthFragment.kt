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
import com.example.travel.domain.model.LoginModel
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.async
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
            binding.progressBar.visibility = View.VISIBLE
            viewModel.loginUser(LoginModel(email, password))

            viewLifecycleOwner.lifecycleScope.launch(Dispatchers.IO) {
                viewModel.userToken.collect {
                    async {
                        sharedPreferences.save("token", it.accessToken)
                        sharedPreferences.save("token_expire_in", it.expiresIn.toString())
                        (activity as MainActivity).saveAuthTime()
                    }.await()
                    withContext(Dispatchers.Main) {
                        (activity as MainActivity).binding.bottomNav.visibility = View.VISIBLE
                        binding.progressBar.visibility = View.GONE
                        findNavController().navigate(R.id.action_controlFragment_to_mapFragment)
                    }
                }
            }
        }
    }
}
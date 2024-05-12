package com.example.travel.presentation

import android.os.Bundle
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.navigation.findNavController
import androidx.navigation.fragment.findNavController
import com.example.travel.MainActivity
import com.example.travel.R
import com.example.travel.databinding.FragmentRegisterBinding
import com.example.travel.databinding.FragmentRequireRegisterBinding
import com.example.travel.presentation.auth.ControlFragment

class RequireRegisterFragment : Fragment() {
    private var _binding: FragmentRequireRegisterBinding? = null
    private val binding get() = _binding!!

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        _binding = FragmentRequireRegisterBinding.inflate(inflater, container, false)
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        binding.btnRegister.setOnClickListener{
            findNavController().navigate(R.id.controlFragment)
            (activity as MainActivity).binding.bottomNav.visibility = View.GONE
        }
    }
}
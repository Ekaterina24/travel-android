package com.example.travel.presentation.profile

import android.content.Context
import android.os.Bundle
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.viewpager2.adapter.FragmentStateAdapter
import com.example.travel.R
import com.example.travel.databinding.FragmentControlBinding
import com.example.travel.databinding.FragmentProfileControlBinding
import com.google.android.material.tabs.TabLayoutMediator

class ControlProfileFragment : Fragment() {

    private var _binding: FragmentProfileControlBinding? = null
    private val binding get() = _binding!!

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        _binding = FragmentProfileControlBinding.inflate(inflater, container, false)
        initTabLayout()
        return binding.root
    }

    private fun initTabLayout(){
        val context = activity?.applicationContext
        binding.tabLayout.addTab(binding.tabLayout.newTab().setText(R.string.settings))
        binding.tabLayout.addTab(binding.tabLayout.newTab().setText(R.string.actions))
        binding.tabLayout.addTab(binding.tabLayout.newTab().setText(R.string.rating))

        val adapter = SubscribersDataCollectionAdapter(this)
        binding.viewpager.adapter = adapter

        TabLayoutMediator(binding.tabLayout, binding.viewpager) { tab, position ->
            tab.text = context?.let { adapter.getPageTitle(position, it) }
            binding.viewpager.setCurrentItem(tab.position, true)
        }.attach()
    }

    private class SubscribersDataCollectionAdapter(fragment: Fragment) :
        FragmentStateAdapter(fragment) {

        val listTabName = listOf(
            R.string.settings,
            R.string.actions,
            R.string.rating
        )

        fun getPageTitle(position: Int, context: Context): String {
            return context.getString(listTabName[position])
        }

        override fun getItemCount(): Int = listTabName.size
        override fun createFragment(position: Int): Fragment {
            val fragment = when (position) {
                0 -> ProfileFragment()
                1 -> ProfileActionsFragment()
                2 -> ProfileRatingFragment()
                else -> Fragment()
            }
            return fragment
        }
    }


}
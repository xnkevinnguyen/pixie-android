package com.pixie.android.ui.draw.home

import android.content.Context
import android.content.Intent
import android.content.SharedPreferences
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Button
import android.widget.TextView
import androidx.activity.OnBackPressedCallback
import androidx.fragment.app.Fragment
import androidx.lifecycle.Observer
import androidx.lifecycle.ViewModelProvider
import com.pixie.android.R
import com.pixie.android.ui.user.AuthActivity
import com.pixie.android.utilities.InjectorUtils

class HomeFragment: Fragment() {

    private lateinit var homeViewModel: HomeViewModel
    private lateinit var preferences: SharedPreferences
    private lateinit var editor: SharedPreferences.Editor

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {

        val factory = InjectorUtils.provideHomeViewModelFactory()
        homeViewModel = ViewModelProvider(this, factory).get(HomeViewModel::class.java)
        val root = inflater.inflate(R.layout.home_fragment, container, false)

        val logout: Button = root.findViewById(R.id.logout_btn)
        preferences = requireContext().getSharedPreferences("Login", Context.MODE_PRIVATE)
        editor = preferences.edit()

        val intent = Intent(root.context, AuthActivity::class.java)

        logout.setOnClickListener {
            homeViewModel.logout()
            editor.remove("isLoggedIn")
            editor.apply()
            startActivity(intent)
            requireActivity().finish()
        }

        return root
    }
}
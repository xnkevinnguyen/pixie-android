package com.pixie.android.ui.draw.home

import android.os.Bundle
import android.view.*
import androidx.fragment.app.Fragment
import androidx.lifecycle.ViewModelProvider
import com.pixie.android.R
import com.pixie.android.utilities.InjectorUtils

class HomeFragment: Fragment() {

    private lateinit var homeViewModel: HomeViewModel

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {

        val factory = InjectorUtils.provideHomeViewModelFactory()
        homeViewModel = ViewModelProvider(this, factory).get(HomeViewModel::class.java)


        return inflater.inflate(R.layout.home_fragment, container, false)
    }

}
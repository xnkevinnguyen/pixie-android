package com.pixie.android.ui.draw.leaderboard

import android.content.SharedPreferences
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.Fragment
import androidx.lifecycle.ViewModelProvider
import com.pixie.android.R
import com.pixie.android.ui.draw.profile.ProfileViewModel
import com.pixie.android.utilities.InjectorUtils

class LeaderboardFragment: Fragment() {

    private lateinit var leaderboardViewModel: LeaderboardViewModel

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {

        val factory = InjectorUtils.provideLeaderboardViewModelFactory()
        leaderboardViewModel = ViewModelProvider(this, factory).get(LeaderboardViewModel::class.java)
        val root = inflater.inflate(R.layout.leaderboard_layout, container, false)

        return root
    }
}
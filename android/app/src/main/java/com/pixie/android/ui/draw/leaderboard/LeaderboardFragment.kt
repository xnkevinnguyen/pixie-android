package com.pixie.android.ui.draw.leaderboard

import android.os.Bundle
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.LinearLayout
import android.widget.TextView
import androidx.fragment.app.Fragment
import androidx.lifecycle.Observer
import androidx.lifecycle.ViewModelProvider
import com.google.android.material.tabs.TabLayout
import com.pixie.android.R
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
        leaderboardViewModel.setup()

        val leaderboardTab = root.findViewById<TabLayout>(R.id.leaderboard_tab)
        val freeLayout = root.findViewById<LinearLayout>(R.id.free_layout)
        val soloLayout = root.findViewById<LinearLayout>(R.id.solo_layout)
        val coopLayout = root.findViewById<LinearLayout>(R.id.coop_layout)
        leaderboardTab.addOnTabSelectedListener(object : TabLayout.OnTabSelectedListener {
            override fun onTabReselected(tab: TabLayout.Tab?) {
                // Do nothing
            }

            override fun onTabUnselected(tab: TabLayout.Tab?) {
                //Do nothing
            }

            override fun onTabSelected(tab: TabLayout.Tab?) {

                if (tab?.position ==0){ // free
                    freeLayout.visibility= View.VISIBLE
                    soloLayout.visibility = View.INVISIBLE
                    coopLayout.visibility = View.INVISIBLE
                }
                else if (tab?.position ==1){ // solo
                    freeLayout.visibility= View.INVISIBLE
                    soloLayout.visibility = View.VISIBLE
                    coopLayout.visibility = View.INVISIBLE
                }
                else if (tab?.position ==2){ // coop
                    freeLayout.visibility= View.INVISIBLE
                    soloLayout.visibility = View.INVISIBLE
                    coopLayout.visibility = View.VISIBLE
                }
            }

        })

        val bestFreeUser = root.findViewById<TextView>(R.id.best_free_user)
        val bestFreeScore = root.findViewById<TextView>(R.id.best_free_score)
        val bestFreeUser2 = root.findViewById<TextView>(R.id.best_free_user2)
        val bestFreeScore2 = root.findViewById<TextView>(R.id.best_free_score2)
        val bestFreeUser3 = root.findViewById<TextView>(R.id.best_free_user3)
        val bestFreeScore3 = root.findViewById<TextView>(R.id.best_free_score3)

        val cumulativeFreeUser = root.findViewById<TextView>(R.id.cumulative_free_user)
        val cumulativeFreeScore = root.findViewById<TextView>(R.id.cumulative_free_score)
        val cumulativeFreeUser2 = root.findViewById<TextView>(R.id.cumulative_free_user2)
        val cumulativeFreeScore2 = root.findViewById<TextView>(R.id.cumulative_free_score2)
        val cumulativeFreeUser3 = root.findViewById<TextView>(R.id.cumulative_free_user3)
        val cumulativeFreeScore3 = root.findViewById<TextView>(R.id.cumulative_free_score3)

        val mostFreeUser = root.findViewById<TextView>(R.id.most_free_user)
        val mostFreeScore = root.findViewById<TextView>(R.id.most_free_score)
        val mostFreeUser2 = root.findViewById<TextView>(R.id.most_free_user2)
        val mostFreeScore2 = root.findViewById<TextView>(R.id.most_free_score2)
        val mostFreeUser3 = root.findViewById<TextView>(R.id.most_free_user3)
        val mostFreeScore3 = root.findViewById<TextView>(R.id.most_free_score3)

        val bestSoloUser = root.findViewById<TextView>(R.id.best_solo_user)
        val bestSoloScore = root.findViewById<TextView>(R.id.best_solo_score)
        val bestSoloUser2 = root.findViewById<TextView>(R.id.best_solo_user2)
        val bestSoloScore2 = root.findViewById<TextView>(R.id.best_solo_score2)
        val bestSoloUser3 = root.findViewById<TextView>(R.id.best_solo_user3)
        val bestSoloScore3 = root.findViewById<TextView>(R.id.best_solo_score3)

        val cumulativeSoloUser = root.findViewById<TextView>(R.id.cumulative_solo_user)
        val cumulativeSoloScore = root.findViewById<TextView>(R.id.cumulative_solo_score)
        val cumulativeSoloUser2 = root.findViewById<TextView>(R.id.cumulative_solo_user2)
        val cumulativeSoloScore2 = root.findViewById<TextView>(R.id.cumulative_solo_score2)
        val cumulativeSoloUser3 = root.findViewById<TextView>(R.id.cumulative_solo_user3)
        val cumulativeSoloScore3 = root.findViewById<TextView>(R.id.cumulative_solo_score3)

        val bestCoopUser = root.findViewById<TextView>(R.id.best_coop_user)
        val bestCoopScore = root.findViewById<TextView>(R.id.best_coop_score)
        val bestCoopUser2 = root.findViewById<TextView>(R.id.best_coop_user2)
        val bestCoopScore2 = root.findViewById<TextView>(R.id.best_coop_score2)
        val bestCoopUser3 = root.findViewById<TextView>(R.id.best_coop_user3)
        val bestCoopScore3 = root.findViewById<TextView>(R.id.best_coop_score3)

        val cumulativeCoopUser = root.findViewById<TextView>(R.id.cumulative_coop_user)
        val cumulativeCoopScore = root.findViewById<TextView>(R.id.cumulative_coop_score)
        val cumulativeCoopUser2 = root.findViewById<TextView>(R.id.cumulative_coop_user2)
        val cumulativeCoopScore2 = root.findViewById<TextView>(R.id.cumulative_coop_score2)
        val cumulativeCoopUser3 = root.findViewById<TextView>(R.id.cumulative_coop_user3)
        val cumulativeCoopScore3 = root.findViewById<TextView>(R.id.cumulative_coop_score3)

        leaderboardViewModel.getBestScore("Free").observe(viewLifecycleOwner, Observer {
            if(it.size != 0){
                if(it.size >= 1){
                    bestFreeUser.text = it[0].username
                    bestFreeScore.text = it[0].value.toString()
                }

                if(it.size >= 2){
                    bestFreeUser2.text = it[1].username
                    bestFreeScore2.text = it[1].value.toString()
                }

                if(it.size >= 3){
                    bestFreeUser3.text = it[2].username
                    bestFreeScore3.text = it[2].value.toString()
                }
            }
        })


        leaderboardViewModel.getBestScore("Solo").observe(viewLifecycleOwner, Observer {
            if (it.size != 0) {
                if (it.size >= 1) {
                    bestSoloUser.text = it[0].username
                    bestSoloScore.text = it[0].value.toString()
                }

                if (it.size >= 2) {
                    bestSoloUser2.text = it[1].username
                    bestSoloScore2.text = it[1].value.toString()
                }

                if (it.size >= 3) {
                    bestSoloUser3.text = it[2].username
                    bestSoloScore3.text = it[2].value.toString()
                }
            }
        })

        leaderboardViewModel.getBestScore("Coop").observe(viewLifecycleOwner, Observer {
            if (it.size != 0) {
                if (it.size >= 1) {
                    bestCoopUser.text = it[0].username
                    bestCoopScore.text = it[0].value.toString()
                }

                if (it.size >= 2) {
                    bestCoopUser2.text = it[1].username
                    bestCoopScore2.text = it[1].value.toString()
                }

                if (it.size >= 3) {
                    bestCoopUser3.text = it[2].username
                    bestCoopScore3.text = it[2].value.toString()
                }
            }
        })

        leaderboardViewModel.getBestCumulativeScore("Free").observe(viewLifecycleOwner, Observer {
            if (it.size != 0) {
                if (it.size >= 1) {
                    cumulativeFreeUser.text = it[0].username
                    cumulativeFreeScore.text = it[0].value.toString()
                }

                if (it.size >= 2) {
                    cumulativeFreeUser2.text = it[1].username
                    cumulativeFreeScore2.text = it[1].value.toString()
                }

                if (it.size >= 3) {
                    cumulativeFreeUser3.text = it[2].username
                    cumulativeFreeScore3.text = it[2].value.toString()
                }
            }
        })

        leaderboardViewModel.getBestCumulativeScore("Solo").observe(viewLifecycleOwner, Observer {
            if (it.size != 0) {
                if (it.size >= 1) {
                    cumulativeSoloUser.text = it[0].username
                    cumulativeSoloScore.text = it[0].value.toString()
                }

                if (it.size >= 2) {
                    cumulativeSoloUser2.text = it[1].username
                    cumulativeSoloScore2.text = it[1].value.toString()
                }

                if (it.size >= 3) {
                    cumulativeSoloUser3.text = it[2].username
                    cumulativeSoloScore3.text = it[2].value.toString()
                }
            }
        })

        leaderboardViewModel.getBestCumulativeScore("Coop").observe(viewLifecycleOwner, Observer {
            if (it.size != 0) {
                if (it.size >= 1) {
                    cumulativeCoopUser.text = it[0].username
                    cumulativeCoopScore.text = it[0].value.toString()
                }

                if (it.size >= 2) {
                    cumulativeCoopUser2.text = it[1].username
                    cumulativeCoopScore2.text = it[1].value.toString()
                }

                if (it.size >= 3) {
                    cumulativeCoopUser3.text = it[2].username
                    cumulativeCoopScore3.text = it[2].value.toString()
                }
            }
        })

        leaderboardViewModel.getMostGameWon().observe(viewLifecycleOwner, Observer {
            if (it.size != 0) {
                if (it.size >= 1) {
                    mostFreeUser.text = it[0].username
                    mostFreeScore.text = it[0].value?.toInt().toString()
                }

                if (it.size >= 2) {
                    mostFreeUser2.text = it[1].username
                    mostFreeScore2.text = it[1].value?.toInt().toString()
                }

                if (it.size >= 3) {
                    mostFreeUser3.text = it[2].username
                    mostFreeScore3.text = it[2].value?.toInt().toString()
                }
            }
        })

        return root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {

        val freeLayout = view.findViewById<LinearLayout>(R.id.free_layout)
        val soloLayout = view.findViewById<LinearLayout>(R.id.solo_layout)
        val coopLayout = view.findViewById<LinearLayout>(R.id.coop_layout)

        freeLayout.visibility= View.VISIBLE
        soloLayout.visibility = View.INVISIBLE
        coopLayout.visibility = View.INVISIBLE

        super.onViewCreated(view, savedInstanceState)
    }
}
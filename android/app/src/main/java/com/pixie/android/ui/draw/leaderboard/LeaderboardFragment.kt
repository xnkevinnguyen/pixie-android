package com.pixie.android.ui.draw.leaderboard

import android.os.Bundle
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.TextView
import androidx.fragment.app.Fragment
import androidx.lifecycle.Observer
import androidx.lifecycle.ViewModelProvider
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

        val freeValues = leaderboardViewModel.getBestScore("Free")
        if(freeValues.size != 0){
            if(freeValues.size >= 1){
                bestFreeUser.text = freeValues[0].username
                bestFreeScore.text = freeValues[0].value.toString()
            }

            if(freeValues.size >= 2){
                bestFreeUser2.text = freeValues[1].username
                bestFreeScore2.text = freeValues[1].value.toString()
            }

            if(freeValues.size >= 3){
                bestFreeUser3.text = freeValues[2].username
                bestFreeScore3.text = freeValues[2].value.toString()
            }
        }

        val soloValues = leaderboardViewModel.getBestScore("Solo")
        if(soloValues.size != 0){
            if(soloValues.size >= 1){
                bestSoloUser.text = soloValues[0].username
                bestSoloScore.text = soloValues[0].value.toString()
            }

            if(soloValues.size >= 2){
                bestSoloUser2.text = soloValues[1].username
                bestSoloScore2.text = soloValues[1].value.toString()
            }

            if(soloValues.size >= 3){
                bestSoloUser3.text = soloValues[2].username
                bestSoloScore3.text = soloValues[2].value.toString()
            }
        }

        val coopValues = leaderboardViewModel.getBestScore("Coop")
        if(coopValues.size != 0){
            if(coopValues.size >= 1){
                bestCoopUser.text = coopValues[0].username
                bestCoopScore.text = coopValues[0].value.toString()
            }

            if(coopValues.size >= 2){
                bestCoopUser2.text = coopValues[1].username
                bestCoopScore2.text = coopValues[1].value.toString()
            }

            if(coopValues.size >= 3){
                bestCoopUser3.text = coopValues[2].username
                bestCoopScore3.text = coopValues[2].value.toString()
            }
        }

        val cumulativeFreeValues = leaderboardViewModel.getBestCumulativeScore("Free")
        if(cumulativeFreeValues.size != 0){
            if(cumulativeFreeValues.size >= 1){
                cumulativeFreeUser.text = cumulativeFreeValues[0].username
                cumulativeFreeScore.text = cumulativeFreeValues[0].value.toString()
            }

            if(cumulativeFreeValues.size >= 2){
                cumulativeFreeUser2.text = cumulativeFreeValues[1].username
                cumulativeFreeScore2.text = cumulativeFreeValues[1].value.toString()
            }

            if(cumulativeFreeValues.size >= 3){
                cumulativeFreeUser3.text = cumulativeFreeValues[2].username
                cumulativeFreeScore3.text = cumulativeFreeValues[2].value.toString()
            }
        }

        val cumulativeSoloValues = leaderboardViewModel.getBestCumulativeScore("Solo")
        if(cumulativeSoloValues.size != 0){
            if(cumulativeSoloValues.size >= 1){
                cumulativeSoloUser.text = cumulativeSoloValues[0].username
                cumulativeSoloScore.text = cumulativeSoloValues[0].value.toString()
            }

            if(cumulativeSoloValues.size >= 2){
                cumulativeSoloUser2.text = cumulativeSoloValues[1].username
                cumulativeSoloScore2.text = cumulativeSoloValues[1].value.toString()
            }

            if(cumulativeSoloValues.size >= 3){
                cumulativeSoloUser3.text = cumulativeSoloValues[2].username
                cumulativeSoloScore3.text = cumulativeSoloValues[2].value.toString()
            }
        }

        val cumulativeCoopValues = leaderboardViewModel.getBestCumulativeScore("Coop")
        if(cumulativeCoopValues.size != 0){
            if(cumulativeCoopValues.size >= 1){
                cumulativeCoopUser.text = cumulativeCoopValues[0].username
                cumulativeCoopScore.text = cumulativeCoopValues[0].value.toString()
            }

            if(cumulativeCoopValues.size >= 2){
                cumulativeCoopUser2.text = cumulativeCoopValues[1].username
                cumulativeCoopScore2.text = cumulativeCoopValues[1].value.toString()
            }

            if(cumulativeCoopValues.size >= 3){
                cumulativeCoopUser3.text = cumulativeCoopValues[2].username
                cumulativeCoopScore3.text = cumulativeCoopValues[2].value.toString()
            }
        }

        val mostGameWonValues = leaderboardViewModel.getMostGameWon()
        if(mostGameWonValues.size != 0){
            if(mostGameWonValues.size >= 1){
                mostFreeUser.text = mostGameWonValues[0].username
                mostFreeScore.text = mostGameWonValues[0].value?.toInt().toString()
            }

            if(mostGameWonValues.size >= 2){
                mostFreeUser2.text = mostGameWonValues[1].username
                mostFreeScore2.text = mostGameWonValues[1].value?.toInt().toString()
            }

            if(mostGameWonValues.size >= 3){
                mostFreeUser3.text = mostGameWonValues[2].username
                mostFreeScore3.text = mostGameWonValues[2].value?.toInt().toString()
            }
        }

        return root
    }
}
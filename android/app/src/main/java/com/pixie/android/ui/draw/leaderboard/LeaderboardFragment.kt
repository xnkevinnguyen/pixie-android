package com.pixie.android.ui.draw.leaderboard

import android.os.Bundle
import android.text.style.UnderlineSpan
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.LinearLayout
import android.widget.ListView
import androidx.core.text.toSpannable
import androidx.fragment.app.Fragment
import androidx.lifecycle.Observer
import androidx.lifecycle.ViewModelProvider
import com.google.android.material.tabs.TabItem
import com.google.android.material.tabs.TabLayout
import com.pixie.android.R
import com.pixie.android.ui.draw.leaderboard.adapters.*
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

        val tab1 = root.findViewById<TabItem>(R.id.tab1)
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

        val adapter1 =
            LeaderboardUserAdapter(
                requireContext()
            )
        val bestFreeScoreList = root.findViewById<ListView>(R.id.best_free_score_list)
        bestFreeScoreList.adapter = adapter1
        val adapter2 =
            LeaderboardUserAdapter2(
                requireContext()
            )
        val bestSoloScoreList = root.findViewById<ListView>(R.id.best_solo_score_list)
        bestSoloScoreList.adapter = adapter2

        val adapter3 =
            LeaderboardUserAdapter3(
                requireContext()
            )
        val bestCoopScoreList = root.findViewById<ListView>(R.id.best_coop_score_list)
        bestCoopScoreList.adapter = adapter3

        val adapter4 =
            LeaderboardUserAdapter4(
                requireContext()
            )
        val freeCumulativeScoreList = root.findViewById<ListView>(R.id.best_cumulative_score_list)
        freeCumulativeScoreList.adapter = adapter4

        val adapter5 =
            LeaderboardUserAdapter5(
                requireContext()
            )
        val soloCumulativeScoreList = root.findViewById<ListView>(R.id.solo_cumulative_score_list)
        soloCumulativeScoreList.adapter = adapter5

        val adapter6 =
            LeaderboardUserAdapter6(
                requireContext()
            )
        val coopCumulativeScoreList = root.findViewById<ListView>(R.id.coop_cumulative_score_list)
        coopCumulativeScoreList.adapter = adapter6

        val adapter7 =
            LeaderboardUserAdapter7(
                requireContext()
            )
        val mostGameWonList = root.findViewById<ListView>(R.id.most_game_list)
        mostGameWonList.adapter = adapter7

        leaderboardViewModel.getBestScore("Free").observe(viewLifecycleOwner, Observer {
            adapter1.set(it)
        })


        leaderboardViewModel.getBestScore("Solo").observe(viewLifecycleOwner, Observer {
            adapter2.set(it)
        })

        leaderboardViewModel.getBestScore("Coop").observe(viewLifecycleOwner, Observer {
            adapter3.set(it)
        })

        leaderboardViewModel.getBestCumulativeScore("Free").observe(viewLifecycleOwner, Observer {
            adapter4.set(it)
        })

        leaderboardViewModel.getBestCumulativeScore("Solo").observe(viewLifecycleOwner, Observer {
            adapter5.set(it)
        })

        leaderboardViewModel.getBestCumulativeScore("Coop").observe(viewLifecycleOwner, Observer {
            adapter6.set(it)
        })

        leaderboardViewModel.getMostGameWon().observe(viewLifecycleOwner, Observer {
            adapter7.set(it)
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
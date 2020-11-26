package com.pixie.android.ui.draw.profile

import android.content.Context
import android.content.Intent
import android.content.SharedPreferences
import android.content.res.ColorStateList
import android.graphics.Color
import android.icu.text.SimpleDateFormat
import android.icu.util.Calendar
import android.os.Bundle
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Button
import android.widget.ImageView
import android.widget.LinearLayout
import android.widget.TextView
import androidx.fragment.app.Fragment
import androidx.lifecycle.Observer
import androidx.lifecycle.ViewModelProvider
import com.pixie.android.R
import com.pixie.android.model.history.ConnectionHistory
import com.pixie.android.model.profile.ScoreData
import com.pixie.android.ui.draw.history.connectionHistory.ConnectionHistoryFragment
import com.pixie.android.ui.draw.history.gameHistory.GameHistoryFragment
import com.pixie.android.ui.user.AuthActivity
import com.pixie.android.utilities.Constants
import com.pixie.android.utilities.InjectorUtils
import java.util.*
import kotlin.random.Random

class ProfileFragment: Fragment() {

    private lateinit var profileViewModel: ProfileViewModel
    private lateinit var preferences: SharedPreferences
    private lateinit var editor: SharedPreferences.Editor

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {

        val factory = InjectorUtils.provideProfileViewModelFactory()
        profileViewModel = ViewModelProvider(this, factory).get(ProfileViewModel::class.java)
        val root = inflater.inflate(R.layout.profile_fragment, container, false)

        profileViewModel.fetchAllInfo()

        val logout: Button = root.findViewById(R.id.btn_logout)
        preferences = requireContext().getSharedPreferences(Constants.SHARED_PREFERENCES_LOGIN, Context.MODE_PRIVATE)
        editor = preferences.edit()

        val intent = Intent(root.context, AuthActivity::class.java)

        logout.setOnClickListener {
            editor.remove("isLoggedIn")
            editor.apply()
            // this will trigger a logout and exit channels
            startActivity(intent)
            requireActivity().finish()
        }

        val avatar : ImageView = root.findViewById(R.id.avatar)
        val lp = LinearLayout.LayoutParams(200, 200)
        avatar.layoutParams =lp
        profileViewModel.getUserInfo().observe(viewLifecycleOwner, Observer {
            var foregroundColor: Int? = null
            if (!it.avatarForeground.isNullOrEmpty()) {
                foregroundColor = Color.parseColor(it.avatarForeground)
            }
            if (foregroundColor == null) {
                foregroundColor =
                    Color.argb(255, Random.nextInt(256), Random.nextInt(256), Random.nextInt(256))
            }
            avatar.setColorFilter(
                foregroundColor
            )

            var backgroundColor: Int? = null
            if (!it.avatarBackground.isNullOrEmpty()) {
                backgroundColor = Color.parseColor(it.avatarBackground)
            }
            if (backgroundColor == null) {
                backgroundColor =
                    Color.argb(255, Random.nextInt(256), Random.nextInt(256), Random.nextInt(256))
            }
            avatar.backgroundTintList = ColorStateList.valueOf(
                backgroundColor
            )
        })

        val showHistory = root.findViewById<Button>(R.id.btn_show_hist)
        showHistory.setOnClickListener {
            val dialog = ConnectionHistoryFragment()
            dialog.show(childFragmentManager, "ConnectionDialogFragment")
        }

        val showGameHistory = root.findViewById<Button>(R.id.btn_game_hist)
        showGameHistory.setOnClickListener {
            val dialog = GameHistoryFragment()
            dialog.show(childFragmentManager, "GameDialogFragment")
        }

        val lastConnection = root.findViewById<TextView>(R.id.user_last_connection)
        val lastDisconnection = root.findViewById<TextView>(R.id.user_last_disconnection)
        profileViewModel.getLogList().observe(viewLifecycleOwner, Observer {
            val format = SimpleDateFormat("yyyy-MM-dd'T'HH:mm:ss.SSSXXX")
            if(it.isNotEmpty()){
                val lastLog = findLastLog(it)
                if(lastLog.connection != "null") {
                    val dateConnection = getDate(
                        format.parse(lastLog.connection).time,
                        "yyyy-MM-dd HH:mm:ss"
                    )
                    lastConnection.text = dateConnection
                }


                if(lastLog.disconnection != "null") {
                    val dateDisconnection = getDate(
                        format.parse(lastLog.disconnection).time,
                        "yyyy-MM-dd HH:mm:ss"
                    )
                    lastDisconnection.text = dateDisconnection
                }
            }
        })
        return root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        setProfileValues()
        setLastGame()
    }

    private fun setProfileValues(){
        val username: TextView = requireActivity().findViewById(R.id.user_username)
        val name: TextView = requireActivity().findViewById(R.id.name)
        val surname: TextView = requireActivity().findViewById(R.id.surname)
        val registration: TextView = requireActivity().findViewById(R.id.user_registration)
        profileViewModel.getUserInfo().observe(viewLifecycleOwner, Observer {
            username.text = it.username
            name.text = it.lastName
            surname.text = it.firstName
            val date = getDate(it.createdAt.toLong(), "yyyy-MM-dd HH:mm:ss")
            registration.text = date
        })

        val nbGames: TextView = requireActivity().findViewById(R.id.nb_games)
        val percent: TextView = requireActivity().findViewById(R.id.percent_win)
        val averageGame: TextView = requireActivity().findViewById(R.id.average_game)
        val totalGame: TextView = requireActivity().findViewById(R.id.total_game)
        val bestScore: TextView = requireActivity().findViewById(R.id.best_score)
        profileViewModel.getUserStats().observe(viewLifecycleOwner, Observer {
            nbGames.text = it.nbGames.toString()
            percent.text = it.percentWin.toString()
            averageGame.text = it.averageGameTime.toString()
            totalGame.text = it.totalGameTime.toString()
            bestScore.text = it.bestFreeScore.toString()
        })
    }

    private fun setLastGame(){
        val date = requireActivity().findViewById<TextView>(R.id.game_date)
        val points = requireActivity().findViewById<TextView>(R.id.game_my_points)
        val winner = requireActivity().findViewById<TextView>(R.id.game_winner)
        val winnerScore = requireActivity().findViewById<TextView>(R.id.game_winner_points)
        val difficulty = requireActivity().findViewById<TextView>(R.id.game_difficulty)
        val mode = requireActivity().findViewById<TextView>(R.id.game_mode)

        profileViewModel.getGameList().observe(viewLifecycleOwner, Observer {
            val format = SimpleDateFormat("yyyy-MM-dd'T'HH:mm:ss.SSSXXX")
            var dateGame: String? = resources.getString(R.string.not_available)
            if(it.isNotEmpty()) {
                if (it[it.size - 1].date != "null") {
                    dateGame = getDate(format.parse(it[it.size - 1].date).time, "yyyy-MM-dd HH:mm:ss")
                }
                date.text = dateGame
                points.text = findUserPoints(it[it.size - 1].points)
                var winnerString: String = ""
                it[it.size - 1].winner?.forEach{
                    winnerString += "$it, "
                }
                if(it[it.size - 1].winner?.size ?: 0 == 0){
                    winnerString = "Unavailable"
                }
                winner.text = winnerString
                winnerScore.text = it[it.size - 1].score.toString()
                difficulty.text = it[it.size - 1].difficulty
                mode.text = it[it.size - 1].gameMode
            }
        })
    }

    private fun findUserPoints(listOfScores: List<ScoreData>): String{
        for (score in listOfScores){
            if(score.username == profileViewModel.getUsername()){
                return score.value.toString()
            }
        }
        return "0"
    }

    private fun findLastLog(listOfLogs: List<ConnectionHistory>): ConnectionHistory{
        var lastConnection: String = "null"
        var lastDisconnection:String = "null"
        for (log in listOfLogs){
            if(log.connection != "null"){
                lastConnection = log.connection
            }
            if(log.disconnection != "null"){
                lastDisconnection = log.disconnection
            }
        }
        return ConnectionHistory(connection = lastConnection, disconnection = lastDisconnection)
    }

    private fun getDate(milliSeconds: Long, dateFormat: String?): String? {
        // Create a DateFormatter object for displaying date in specified format.
        val formatter = SimpleDateFormat(dateFormat)

        // Create a calendar object that will convert the date and time value in milliseconds to date.
        val calendar: Calendar = Calendar.getInstance()
        calendar.timeInMillis = milliSeconds
        return formatter.format(calendar.time)
    }

}
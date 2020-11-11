package com.pixie.android.ui.draw.history.gameHistory

import android.content.Context
import android.icu.text.SimpleDateFormat
import android.icu.util.Calendar
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.BaseAdapter
import android.widget.TextView
import androidx.core.content.ContextCompat
import androidx.lifecycle.ViewModelProvider
import androidx.lifecycle.ViewModelStore
import com.pixie.android.R
import com.pixie.android.model.history.ConnectionHistory
import com.pixie.android.model.history.GameHistory
import com.pixie.android.model.profile.ScoreData
import com.pixie.android.ui.chat.ChatViewModel
import com.pixie.android.ui.draw.profile.ProfileViewModel
import com.pixie.android.utilities.InjectorUtils

class GameAdapter (context: Context) : BaseAdapter() {

    private val inflater: LayoutInflater =
        context.getSystemService(Context.LAYOUT_INFLATER_SERVICE) as LayoutInflater

    val context =context
    private var listOfGames = ArrayList<GameHistory>()

    fun set(newGameList:ArrayList<GameHistory>?){
        if(newGameList!=null) {
            listOfGames= ArrayList(newGameList)
        }
        notifyDataSetChanged()
    }
    fun add(gameHistory: GameHistory) {
        this.listOfGames.add(gameHistory)
        notifyDataSetChanged()
    }
    fun clear(){
        listOfGames.clear()
    }

    override fun getCount(): Int {
        return listOfGames.size
    }

    override fun getItem(position: Int): Any {
        return listOfGames[position]
    }

    override fun getItemId(position: Int): Long {
        return position.toLong()
    }

    override fun getView(position: Int, convertView: View?, parent: ViewGroup): View {
        val history: GameHistory = listOfGames[listOfGames.size - position - 1]
        val rowView = inflater.inflate(R.layout.game_history_row, parent, false)
        val date = rowView.findViewById<TextView>(R.id.history_date)
        val time = rowView.findViewById<TextView>(R.id.history_time)
        val points = rowView.findViewById<TextView>(R.id.history_points)
        val winner = rowView.findViewById<TextView>(R.id.history_winner)
        val score = rowView.findViewById<TextView>(R.id.history_score)
        val difficulty = rowView.findViewById<TextView>(R.id.history_difficulty)
        val mode = rowView.findViewById<TextView>(R.id.history_mode)

        val format = SimpleDateFormat("yyyy-MM-dd'T'HH:mm:ss.SSSXXX")
        var gameDate = context.resources.getString(R.string.not_available)
        var gameTime = context.resources.getString(R.string.not_available)
        if(history.date != "null") {
            gameDate = getDate(format.parse(history.date).time, "yyyy-MM-dd")
            gameTime = getDate(format.parse(history.date).time, "HH:mm:ss")
        }
        date.text = gameDate
        time.text = gameTime
        points.text = findUserPoints(history.points)
        var winnerString: String = ""
        history.winner?.forEach{
            winnerString += "$it, "
        }
        if(history.winner?.size ?: 0 == 0){
            winnerString = "Unavailable"
        }

        winner.text = winnerString
        score.text= history.score.toString()
        difficulty.text = history.difficulty
        mode.text = history.gameMode

        if((position % 2) != 0){
            date.background = ContextCompat.getDrawable(context, R.drawable.history_table_input_border_blue)
            time.background = ContextCompat.getDrawable(context, R.drawable.history_table_input_border_blue)
            points.background = ContextCompat.getDrawable(context, R.drawable.history_table_input_border_blue)
            winner.background = ContextCompat.getDrawable(context, R.drawable.history_table_input_border_blue)
            score.background = ContextCompat.getDrawable(context, R.drawable.history_table_input_border_blue)
            difficulty.background = ContextCompat.getDrawable(context, R.drawable.history_table_input_border_blue)
            mode.background = ContextCompat.getDrawable(context, R.drawable.history_table_input_border_blue)
        }
        return rowView

    }

    private fun getDate(milliSeconds: Long, dateFormat: String?): String? {
        // Create a DateFormatter object for displaying date in specified format.
        val formatter = SimpleDateFormat(dateFormat)

        // Create a calendar object that will convert the date and time value in milliseconds to date.
        val calendar: Calendar = Calendar.getInstance()
        calendar.timeInMillis = milliSeconds
        return formatter.format(calendar.time)
    }

    private fun findUserPoints(listOfScores: List<ScoreData>): String{
        for (score in listOfScores){
            val factory = InjectorUtils.provideProfileViewModelFactory()
            val profileViewModel = ViewModelProvider(ViewModelStore(),factory).get(ProfileViewModel::class.java)
            if(score.username == profileViewModel.getUsername()){
                return score.value.toString()
            }
        }
        return "0"
    }
}
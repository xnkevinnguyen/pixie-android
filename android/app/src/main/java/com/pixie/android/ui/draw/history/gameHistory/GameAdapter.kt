package com.pixie.android.ui.draw.history.gameHistory

import android.content.Context
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.BaseAdapter
import android.widget.TextView
import com.pixie.android.R
import com.pixie.android.model.history.ConnectionHistory
import com.pixie.android.model.history.GameHistory

class GameAdapter (context: Context) : BaseAdapter() {

    private val inflater: LayoutInflater =
        context.getSystemService(Context.LAYOUT_INFLATER_SERVICE) as LayoutInflater

    private var listOfGames = ArrayList<GameHistory>()

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
        val history: GameHistory = listOfGames[position]
        val rowView = inflater.inflate(R.layout.connection_history_row, parent, false)
        val date = rowView.findViewById<TextView>(R.id.history_date)
        val time = rowView.findViewById<TextView>(R.id.history_time)
        val points = rowView.findViewById<TextView>(R.id.history_points)
        val winner = rowView.findViewById<TextView>(R.id.history_winner)
        val score = rowView.findViewById<TextView>(R.id.history_score)
        val difficulty = rowView.findViewById<TextView>(R.id.history_difficulty)
        val mode = rowView.findViewById<TextView>(R.id.history_mode)

        date.text = history.date
        time.text = history.time
        points.text = history.points
        winner.text = history.winner
        score.text= history.score
        difficulty.text = history.difficulty
        mode.text = history.gameMode

        return rowView

    }
}
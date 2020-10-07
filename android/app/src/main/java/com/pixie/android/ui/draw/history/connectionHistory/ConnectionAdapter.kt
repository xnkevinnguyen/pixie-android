package com.pixie.android.ui.draw.history.connectionHistory

import android.content.Context
import android.text.TextUtils
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.BaseAdapter
import android.widget.Filter
import android.widget.Filterable
import android.widget.TextView
import com.pixie.android.R
import com.pixie.android.model.history.ConnectionHistory

class ConnectionAdapter (context: Context) : BaseAdapter(), Filterable {

    private val inflater: LayoutInflater =
        context.getSystemService(Context.LAYOUT_INFLATER_SERVICE) as LayoutInflater

    private var listOfConnection = ArrayList<ConnectionHistory>()
    var filteredListOfConnection = ArrayList<ConnectionHistory>()

    fun add(connectionHistory: ConnectionHistory) {
        this.listOfConnection.add(connectionHistory)
        this.filteredListOfConnection.add(connectionHistory)
        notifyDataSetChanged()
    }
    fun clear(){
        filteredListOfConnection.clear()
    }

    override fun getCount(): Int {
        return filteredListOfConnection.size
    }

    override fun getItem(position: Int): Any {
        return filteredListOfConnection[position]
    }

    override fun getItemId(position: Int): Long {
        return position.toLong()
    }

    override fun getView(position: Int, convertView: View?, parent: ViewGroup): View {
        val history: ConnectionHistory = filteredListOfConnection[position]
        val rowView = inflater.inflate(R.layout.connection_history_row, parent, false)
        val connection = rowView.findViewById<TextView>(R.id.connection)
        val disconnection = rowView.findViewById<TextView>(R.id.disconnection)
        connection.text = history.connexion
        disconnection.text = history.disconnection
        return rowView

    }

    override fun getFilter(): Filter {
        return object : Filter() {
            override fun publishResults(
                constraint: CharSequence,
                results: FilterResults
            ) {
                if (results.count == 0) {
                    notifyDataSetInvalidated();
                } else {
                    filteredListOfConnection = results.values as ArrayList<ConnectionHistory>
                    notifyDataSetChanged()
                }
            }

            override fun performFiltering(constraint: CharSequence): FilterResults {
                val filterString = constraint.toString().toLowerCase()
                val results = FilterResults()
                val count = listOfConnection.size
                var filterableString: ConnectionHistory
                if (TextUtils.isEmpty(constraint)) {
                    results.count = listOfConnection.size
                    results.values = listOfConnection
                } else {
                    val filteredResults = ArrayList<ConnectionHistory>()
                    for (i in 0 until count) {
                        filterableString = listOfConnection[i]
//                        if (filterableString.username.toLowerCase().contains(filterString)) {
//                            filteredResults.add(filterableString)
//                        }
                    }
                    results.values = filteredResults
                    results.count = filteredResults.size
                }
                return results
            }
        }
    }
}
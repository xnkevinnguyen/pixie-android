package com.pixie.android.ui.draw.availableGames

import android.content.Context
import android.content.SharedPreferences
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ArrayAdapter
import android.widget.Button
import android.widget.Spinner
import android.widget.TextView
import androidx.fragment.app.Fragment
import com.pixie.android.R
import com.pixie.android.utilities.Constants

class AvailableGamesFragment : Fragment() {

    private lateinit var preferencesGame: SharedPreferences
    private lateinit var editorGame: SharedPreferences.Editor

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {

        val root = inflater.inflate(R.layout.available_game_fragment, container, false)

        preferencesGame = requireContext().getSharedPreferences(Constants.SHARED_PREFERENCES_GAME, Context.MODE_PRIVATE)
        editorGame = preferencesGame.edit()

        val gameMode1 = root.findViewById<TextView>(R.id.mode_type)
        val gameMode2 = root.findViewById<TextView>(R.id.mode_type2)
        val gameMode3 = root.findViewById<TextView>(R.id.mode_type3)
        val gameMode4 = root.findViewById<TextView>(R.id.mode_type4)
        val mode = root.findViewById<TextView>(R.id.mode)
        val difficulty = root.findViewById<TextView>(R.id.level)
        val spinnerLanguage =  root.findViewById<Spinner>(R.id.spinner_language_game)
        val createBtn = root.findViewById<Button>(R.id.create)

        val modeType = preferencesGame.getString(Constants.GAME_MODE, "Free for all")

        gameMode1.text = modeType
        gameMode2.text = modeType
        gameMode3.text = modeType
        gameMode4.text = modeType

        val gameMode = "Mode: $modeType"
        mode.text = gameMode

        val level = "Difficulty: " + preferencesGame.getString(Constants.GAME_DIFF, "Medium")
        difficulty.text = level

        val itemsLang = arrayOf(resources.getString(R.string.eng), resources.getString(R.string.fr))
        val adapterLang: ArrayAdapter<String> = ArrayAdapter(
            requireContext(),
            android.R.layout.simple_spinner_dropdown_item,
            itemsLang
        )
        spinnerLanguage.adapter = adapterLang

        createBtn.setOnClickListener {
            editorGame.putString(Constants.GAME_LANGUAGE, spinnerLanguage.selectedItem.toString())
            editorGame.apply()
        }
        return root
    }
}
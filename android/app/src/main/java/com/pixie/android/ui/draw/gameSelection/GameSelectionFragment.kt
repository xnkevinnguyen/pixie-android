package com.pixie.android.ui.draw.gameSelection

import android.content.Context
import android.content.SharedPreferences
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Button
import android.widget.ImageButton
import android.widget.ImageView
import androidx.core.content.ContextCompat
import androidx.fragment.app.Fragment
import androidx.lifecycle.ViewModelProvider
import androidx.navigation.findNavController
import com.pixie.android.R
import com.pixie.android.utilities.Constants
import com.pixie.android.utilities.InjectorUtils

class GameSelectionFragment: Fragment() {

    private lateinit var preferencesGame: SharedPreferences
    private lateinit var editorGame: SharedPreferences.Editor

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {

        val root = inflater.inflate(R.layout.game_selection_fragment, container, false)

        preferencesGame = requireContext().getSharedPreferences(Constants.SHARED_PREFERENCES_GAME, Context.MODE_PRIVATE)
        editorGame = preferencesGame.edit()

        editorGame.putString(Constants.GAME_MODE, "Free for all")
        editorGame.putString(Constants.GAME_DIFF, "Medium")
        editorGame.apply()

        val freeBtn = root.findViewById<ImageButton>(R.id.free_for_all)
        val soloBtn = root.findViewById<ImageButton>(R.id.solo)
        val coopBtn = root.findViewById<ImageButton>(R.id.coop)

        freeBtn.setOnClickListener {
            setCheckMarkColor(R.color.green, freeBtn)
            setCheckMarkColor(R.color.dark_grey, soloBtn)
            setCheckMarkColor(R.color.dark_grey, coopBtn)

            editorGame.putString(Constants.GAME_MODE, "Free for all")
            editorGame.apply()
        }
        soloBtn.setOnClickListener {
            setCheckMarkColor(R.color.dark_grey, freeBtn)
            setCheckMarkColor(R.color.green, soloBtn)
            setCheckMarkColor(R.color.dark_grey, coopBtn)

            editorGame.putString(Constants.GAME_MODE, "Solo")
            editorGame.apply()
        }
        coopBtn.setOnClickListener {
            setCheckMarkColor(R.color.dark_grey, freeBtn)
            setCheckMarkColor(R.color.dark_grey, soloBtn)
            setCheckMarkColor(R.color.green, coopBtn)

            editorGame.putString(Constants.GAME_MODE, "Coop")
            editorGame.apply()
        }

        val easyBtn = root.findViewById<ImageView>(R.id.easy)
        val mediumBtn = root.findViewById<ImageView>(R.id.medium)
        val hardBtn = root.findViewById<ImageView>(R.id.hard)

        easyBtn.setOnClickListener {
            setDifficultyColor(R.color.green, easyBtn)
            setDifficultyColor(R.color.dark_grey, mediumBtn)
            setDifficultyColor(R.color.dark_grey, hardBtn)

            editorGame.putString(Constants.GAME_DIFF, "Easy")
            editorGame.apply()
        }
        mediumBtn.setOnClickListener {
            setDifficultyColor(R.color.dark_grey, easyBtn)
            setDifficultyColor(R.color.yellow, mediumBtn)
            setDifficultyColor(R.color.dark_grey, hardBtn)

            editorGame.putString(Constants.GAME_DIFF, "Medium")
            editorGame.apply()
        }
        hardBtn.setOnClickListener {
            setDifficultyColor(R.color.dark_grey, easyBtn)
            setDifficultyColor(R.color.dark_grey, mediumBtn)
            setDifficultyColor(R.color.red, hardBtn)

            editorGame.putString(Constants.GAME_DIFF, "Hard")
            editorGame.apply()
        }

        val nextBtn = root.findViewById<Button>(R.id.next_btn)
        nextBtn.setOnClickListener {
            val mode = preferencesGame.getString(Constants.GAME_MODE, "Free for all")
            if(mode != "Solo") {
                val navController = requireActivity().findNavController(R.id.nav_host_fragment)
                navController.navigate(R.id.nav_available_games)
            } else {
                val navController = requireActivity().findNavController(R.id.nav_host_fragment)
                navController.navigate(R.id.nav_game_creation)
            }
        }
        return root
    }

    private fun setCheckMarkColor(color: Int, button: ImageButton){
        button.setColorFilter(ContextCompat.getColor(requireContext(), color), android.graphics.PorterDuff.Mode.SRC_IN)
    }

    private fun setDifficultyColor(color: Int, button: ImageView){
        button.setColorFilter(ContextCompat.getColor(requireContext(), color), android.graphics.PorterDuff.Mode.SRC_IN)
    }
}
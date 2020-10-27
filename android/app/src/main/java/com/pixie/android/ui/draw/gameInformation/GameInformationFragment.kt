package com.pixie.android.ui.draw.gameInformation

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.TextView
import androidx.fragment.app.Fragment
import androidx.lifecycle.Observer
import androidx.lifecycle.ViewModelProvider
import com.pixie.android.R
import com.pixie.android.utilities.InjectorUtils


class GameInformationFragment: Fragment() {

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {

        val root =inflater.inflate(R.layout.game_information_fragment, container, false)
        val timerElement = root.findViewById<TextView>(R.id.time_left)

        val factory = InjectorUtils.provideGameInformationViewModelFactory()
        val gameInfoViewModel = ViewModelProvider(this,factory).get(GameInformationViewModel::class.java)
        gameInfoViewModel.getTimer().observe(viewLifecycleOwner, Observer {
          timerElement.text = it.toString()
        })

        return root
    }
}
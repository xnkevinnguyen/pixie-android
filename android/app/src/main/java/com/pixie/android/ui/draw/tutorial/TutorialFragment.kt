package com.pixie.android.ui.draw.tutorial

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Button
import androidx.fragment.app.Fragment
import androidx.fragment.app.FragmentManager
import androidx.fragment.app.FragmentTransaction
import com.pixie.android.R


class TutorialFragment: Fragment() {

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {

        val root = inflater.inflate(R.layout.tutorial_layout, container, false)

        val getStarted = root.findViewById<Button>(R.id.get_started)

        getStarted.setOnClickListener {
            val fragment: Fragment = TutorialPage2Fragment()
            val transaction: FragmentTransaction = childFragmentManager.beginTransaction()
            transaction.replace(R.id.tutorial_page1, fragment)
            transaction.commit()
        }
        return root
    }
}
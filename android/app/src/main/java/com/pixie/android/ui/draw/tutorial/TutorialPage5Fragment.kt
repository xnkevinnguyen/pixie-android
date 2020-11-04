package com.pixie.android.ui.draw.tutorial

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Button
import androidx.fragment.app.Fragment
import androidx.fragment.app.FragmentTransaction
import androidx.navigation.Navigation
import com.pixie.android.R

class TutorialPage5Fragment: Fragment() {

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {

        val root = inflater.inflate(R.layout.tutorial_layout_page5, container, false)

        val next = root.findViewById<Button>(R.id.next_page5)

        next.setOnClickListener {
            val fragment: Fragment = TutorialPage6Fragment()
            val transaction: FragmentTransaction = childFragmentManager.beginTransaction()
            transaction.replace(R.id.tutorial_page5, fragment)
            transaction.commit()
        }
        return root
    }
}
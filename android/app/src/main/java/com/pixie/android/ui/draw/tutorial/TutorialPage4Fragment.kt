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

class TutorialPage4Fragment: Fragment() {

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {

        val root = inflater.inflate(R.layout.tutorial_layout_page4, container, false)

        val next = root.findViewById<Button>(R.id.next_page4)

        next.setOnClickListener {
            val navController = Navigation.findNavController(requireActivity(), R.id.nav_host_fragment)
            navController.navigate(R.id.nav_tutorial_page5)
        }
        return root
    }
}
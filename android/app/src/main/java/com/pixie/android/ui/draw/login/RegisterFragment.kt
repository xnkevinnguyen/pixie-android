package com.pixie.android.ui.draw.login

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ImageView
import android.widget.TextView
import androidx.fragment.app.Fragment
import androidx.navigation.Navigation.findNavController
import com.pixie.android.R
import kotlinx.android.synthetic.main.register_fragment.*


class RegisterFragment : Fragment() {
    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        // Inflate the layout for this fragment
        val root =  inflater.inflate(R.layout.register_fragment, container, false)
        val navController = findNavController(requireActivity(), R.id.nav_login_fragment)

        val toLogin = root.findViewById<ImageView>(R.id.return_login)
        toLogin.setOnClickListener {
            navController.navigate(R.id.nav_login)
        }

        val swipe = root.findViewById<TextView>(R.id.swipeLeft)
        swipe.setOnClickListener {
            navController.navigate(R.id.nav_login)
        }

        return root
    }
}
package com.pixie.android.ui.draw.profile

import android.app.ActionBar
import android.os.Bundle
import android.view.Gravity
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ImageView
import android.widget.LinearLayout
import android.widget.TextView
import androidx.constraintlayout.widget.ConstraintLayout
import androidx.core.view.marginTop
import androidx.fragment.app.Fragment
import androidx.lifecycle.Observer
import androidx.lifecycle.ViewModelProvider
import com.pixie.android.R
import com.pixie.android.utilities.InjectorUtils

class ProfileFragment: Fragment() {

    private lateinit var profileViewModel: ProfileViewModel

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {

        val factory = InjectorUtils.provideProfileViewModelFactory()
        profileViewModel = ViewModelProvider(this, factory).get(ProfileViewModel::class.java)
        val root = inflater.inflate(R.layout.profile_fragment, container, false)
        val textView: TextView = root.findViewById(R.id.text_profile)
        profileViewModel.text.observe(viewLifecycleOwner, Observer {
            textView.text = it
        })

        var avatar : ImageView = root.findViewById(R.id.imageView)
        val lp = LinearLayout.LayoutParams(200, 200)
        lp.gravity = (Gravity.CENTER_HORIZONTAL)
        avatar.layoutParams =lp

        var header : LinearLayout = root.findViewById(R.id.header_profile)
        header.setPadding(16, 0, 16, 0)

        var username: TextView = root.findViewById(R.id.username)
        username.gravity = Gravity.CENTER_HORIZONTAL
        username.textSize = 25F

        return root
    }
}
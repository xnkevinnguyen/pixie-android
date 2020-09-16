package com.pixie.android.ui.draw.drawTools

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.Fragment
import androidx.lifecycle.Observer
import androidx.lifecycle.ViewModelProvider
import com.pixie.android.R
import com.pixie.android.utilities.InjectorUtils
import kotlinx.android.synthetic.main.draw_tools_fragment.*

class DrawToolsFragment() : Fragment() {
    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {

        val rootView: View = inflater.inflate(
            R.layout.draw_tools_fragment,
            container, false
        )

        return rootView
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        val factory = InjectorUtils.provideDrawViewModelFactory()
        val viewModel = ViewModelProvider(this, factory).get(DrawToolsViewModel::class.java)

        viewModel.getPrimaryColor().observe(viewLifecycleOwner, Observer {
            color_picker.setBackgroundColor(it.toArgb())
        })
        color_picker.setOnClickListener {
            viewModel.modifyPrimaryColor()
        }
        undo_button.setOnClickListener {
            viewModel.undo()
        }


        super.onViewCreated(view, savedInstanceState)
    }
}
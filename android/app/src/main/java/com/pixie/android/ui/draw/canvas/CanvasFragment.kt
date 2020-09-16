package com.pixie.android.ui.draw.canvas

import android.graphics.Path
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.Fragment
import androidx.lifecycle.Observer
import androidx.lifecycle.ViewModelProvider
import com.pixie.android.R
import com.pixie.android.utilities.InjectorUtils
import kotlinx.android.synthetic.main.canvas_fragment.*
import kotlinx.android.synthetic.main.draw_activity.*

class CanvasFragment: Fragment() {
    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {

        val rootView: View = inflater.inflate(
            R.layout.canvas_fragment,
            container, false
        )

        return rootView
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        val factory = InjectorUtils.provideCanvasViewModelFactory()
        val viewModel = ViewModelProvider(this,factory).get(CanvasViewModel::class.java)

        viewModel.getPrimaryColor().observe(viewLifecycleOwner, Observer {
            my_canvas.drawColor = it.toArgb()
            my_canvas.reinitializeDrawingParameters()
        })

        viewModel.getDrawCommandHistory().observe(viewLifecycleOwner, Observer {
            //canvas undo
            my_canvas.drawFromCommandList(it)
        })
        my_canvas.completedCommand.observe(viewLifecycleOwner, Observer {
            viewModel.addCommandToHistory(it)
        })

        super.onViewCreated(view, savedInstanceState)
    }
}
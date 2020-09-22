package com.pixie.android.ui.draw.canvas

import android.graphics.Color
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

class CanvasFragment : Fragment() {
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
        val viewModel = ViewModelProvider(this, factory).get(CanvasViewModel::class.java)

        viewModel.getPrimaryColor().observe(viewLifecycleOwner, Observer {
            my_canvas.drawColor = it.toArgb()
            my_canvas.reinitializeDrawingParameters()
        })

        viewModel.getDrawCommandHistory().observe(viewLifecycleOwner, Observer {

            my_canvas.drawFromCommandList(it)
        })
        my_canvas.completedCommand.observe(viewLifecycleOwner, Observer {
            viewModel.addCommandToHistory(it)
        })

        viewModel.getStrokeWidth().observe(viewLifecycleOwner, Observer {
            my_canvas.drawStroke = it
            my_canvas.reinitializeDrawingParameters()
        })

        viewModel.getCellWidthGrid().observe(viewLifecycleOwner, Observer {
            my_grid.setCellWidth(it)
        })

        viewModel.getEraser().observe(viewLifecycleOwner, Observer {
            my_canvas.setErase(it)
        })

        viewModel.getGridVal().observe(viewLifecycleOwner, Observer {
            showGrid(it)
        })

        super.onViewCreated(view, savedInstanceState)

    }

    private fun showGrid(gridOn: Boolean) {
        if (gridOn) {
            my_grid.visibility = View.VISIBLE
            my_canvas.setBackgroundColor(Color.TRANSPARENT)
        } else {
            my_grid.visibility = View.GONE
            my_canvas.setBackgroundColor(Color.WHITE)
        }
    }
}
package com.pixie.android.ui.draw.canvas

import android.annotation.SuppressLint
import android.graphics.Color
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.Fragment
import androidx.lifecycle.Observer
import androidx.lifecycle.ViewModelProvider
import com.pixie.android.R
import com.pixie.android.data.game.ShowWordinGameType
import com.pixie.android.model.draw.CommandType
import com.pixie.android.utilities.InjectorUtils
import kotlinx.android.synthetic.main.canvas_fragment.*
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch

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

        my_canvas.setViewModel(viewModel)
        viewModel.getPrimaryColor().observe(viewLifecycleOwner, Observer {
            my_canvas.drawColor = it.toArgb()
            my_canvas.reinitializeDrawingParameters()
        })

        viewModel.getDrawCommandHistory().observe(viewLifecycleOwner, Observer {
            // should only draw DRAW commands and remove ERASE commands
            val filteredCommand = it.filter { it.value.type == CommandType.DRAW }
            my_canvas.drawFromCommandList(ArrayList(filteredCommand.values))


        })

        viewModel.getShouldShowWord().observe(viewLifecycleOwner,Observer{
            if(it.shouldShowWordBig && it.word !=null){
                display_word.visibility = View.VISIBLE
                if(it.type == ShowWordinGameType.IS_DRAWER)
                    display_word_top.text = String.format(resources.getString(R.string.display_word_top),it.word)
                else if (it.type == ShowWordinGameType.OTHER_DRAWER)
                    display_word_top.text = String.format(resources.getString(R.string.next_drawer),it.word)
            }else if(!it.shouldShowWordBig && it.word!=null){
                display_word.visibility = View.GONE
                    display_word_canvas.visibility=View.VISIBLE
                display_word_canvas.bringToFront()
                    display_word_canvas.text=String.format(resources.getString(R.string.your_word),it.word)
            }else{
                display_word.visibility = View.GONE
                display_word_canvas.visibility=View.GONE

            }
        })
        viewModel.getIsCanvasLocked().observe(viewLifecycleOwner,Observer{
                my_canvas.setIsCanvasLocked(it)
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
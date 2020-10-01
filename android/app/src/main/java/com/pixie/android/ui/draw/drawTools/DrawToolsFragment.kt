package com.pixie.android.ui.draw.drawTools

import android.app.Dialog
import android.content.Context
import android.content.SharedPreferences
import android.graphics.Color
import android.os.Bundle
import android.util.Log
import android.view.*
import android.widget.*
import androidx.core.content.ContextCompat
import androidx.fragment.app.Fragment
import androidx.lifecycle.Observer
import androidx.lifecycle.ViewModelProvider
import com.pixie.android.R
import com.pixie.android.apolloClient
import com.pixie.android.utilities.InjectorUtils
import kotlinx.android.synthetic.main.draw_tools_fragment.*
import kotlinx.coroutines.GlobalScope
import kotlinx.coroutines.launch
import top.defaults.colorpicker.ColorPickerPopup
import java.lang.Exception

class DrawToolsFragment() : Fragment() {
    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {

        return inflater.inflate(
            R.layout.draw_tools_fragment,
            container, false
        )
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        val factory = InjectorUtils.provideDrawViewModelFactory()
        val viewModel = ViewModelProvider(this, factory).get(DrawToolsViewModel::class.java)

        viewModel.getPrimaryColor().observe(viewLifecycleOwner, Observer {
            color_picker.setBackgroundColor(it.toArgb())
        })

        color_picker.setOnClickListener {
            ColorPickerPopup.Builder(context)
                .initialColor(Color.RED) // Set initial color
                .enableBrightness(true) // Enable brightness slider or not
                .enableAlpha(true) // Enable alpha slider or not
                .okTitle("Choose")
                .cancelTitle("Cancel")
                .showIndicator(true)
                .showValue(true)
                .build()
                .show(view, object : ColorPickerPopup.ColorPickerObserver() {
                    override fun onColorPicked(color: Int) {
                        viewModel.modifyPrimaryColor(Color.valueOf(color))
                    }
                })
        }

        val unselectedColor = ContextCompat.getColor(requireContext(), R.color.unselectedButtonColor);
        val selectedColor = ContextCompat.getColor(requireContext(), R.color.selectedButtonColor);

        pencil.setOnClickListener {
            pencil.setBackgroundColor(selectedColor)
            eraser.setBackgroundColor(unselectedColor)
            strokeSizeDialog(false)
            viewModel.setEraser(false)
        }

        eraser.setOnClickListener {
            eraser.setBackgroundColor(selectedColor)
            pencil.setBackgroundColor(unselectedColor)
            strokeSizeDialog(true)
            viewModel.setEraser(true)




        }
        undo_button.setOnClickListener {
            viewModel.undo()
        }
        redo_button.setOnClickListener{
            viewModel.redo()
        }


        grid.setOnClickListener {
            gridDialog()
        }

        super.onViewCreated(view, savedInstanceState)

    }

    private fun strokeSizeDialog(eraserOn: Boolean){
        val factory = InjectorUtils.provideDrawViewModelFactory()
        val viewModel = ViewModelProvider(this, factory).get(DrawToolsViewModel::class.java)

        val small = resources.getInteger(R.integer.small_size)
        val medium = resources.getInteger(R.integer.medium_size)
        val large = resources.getInteger(R.integer.large_size)

        val dialog = Dialog(requireContext())

        // Change dialog box position in the window
        val window: Window? = dialog.window
        if(window != null){
            val wlp = window.attributes
            wlp.gravity = Gravity.LEFT
            wlp.y = -200
            wlp.x = 150
        }

        dialog.setContentView(R.layout.size_chooser_layout)
        val smallSize = dialog.findViewById<ImageButton>(R.id.small_stroke_size)
        val mediumSize = dialog.findViewById<ImageButton>(R.id.medium_stroke_size)
        val largeSize = dialog.findViewById<ImageButton>(R.id.large_stroke_size)

        smallSize.setOnClickListener(){
            viewModel.modifyStrokeWidth(small.toFloat())
            if(eraserOn) viewModel.setEraser(true)
            else viewModel.setEraser(false)
            dialog.dismiss()
        }
        mediumSize.setOnClickListener(){
            viewModel.modifyStrokeWidth(medium.toFloat())
            if(eraserOn) viewModel.setEraser(true)
            else viewModel.setEraser(false)
            dialog.dismiss()
        }
        largeSize.setOnClickListener(){
            viewModel.modifyStrokeWidth(large.toFloat())
            if(eraserOn) viewModel.setEraser(true)
            else viewModel.setEraser(false)
            dialog.dismiss()
        }
        dialog.show()
    }

    private fun gridDialog(){
        val factory = InjectorUtils.provideDrawViewModelFactory()
        val viewModel = ViewModelProvider(this, factory).get(DrawToolsViewModel::class.java)

        val dialog = Dialog(requireContext())
        dialog.setContentView(R.layout.cell_width_layout)
        val cellWidthInput = dialog.findViewById<EditText>(R.id.cellWidthInput)
        val preferences: SharedPreferences = requireContext().getSharedPreferences("MyPrefs", Context.MODE_PRIVATE)
        val editor: SharedPreferences.Editor = preferences.edit()
        cellWidthInput.setText(preferences.getString("cellWidth", "25"), TextView.BufferType.EDITABLE)
        dialog.findViewById<TextView>(R.id.positive_button).setOnClickListener(){
            viewModel.modifyCellWidthGrid(Integer.parseInt(cellWidthInput.text.toString()))
            editor.putString("cellWidth", cellWidthInput.text.toString())
            editor.apply()
            dialog.dismiss()
        }

        dialog.findViewById<TextView>(R.id.negative_button).setOnClickListener(){
            dialog.dismiss()
        }

        val toggleButton = dialog.findViewById<ToggleButton>(R.id.toggleButton)
        toggleButton.isChecked = preferences.getBoolean("gridChecked", false)
        toggleButton?.setOnCheckedChangeListener { _, isChecked ->
            val msg = "Grid is " + if (isChecked) "ON" else "OFF"
            Toast.makeText(requireContext(), msg, Toast.LENGTH_SHORT).show()
            if(isChecked){
                viewModel.setGridValue(true)
                editor.putBoolean("gridChecked", isChecked)
                editor.apply()
            }
            else {
                viewModel.setGridValue(false)
                editor.putBoolean("gridChecked", isChecked)
                editor.apply()
            }
        }
        dialog.show()
    }
}
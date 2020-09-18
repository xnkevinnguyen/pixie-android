package com.pixie.android.ui.draw

import android.graphics.Color
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.core.content.ContextCompat
import androidx.fragment.app.Fragment
import androidx.lifecycle.ViewModelProvider
import com.pixie.android.R
import com.pixie.android.ui.draw.drawTools.DrawToolsViewModel
import com.pixie.android.utilities.InjectorUtils
import kotlinx.android.synthetic.main.draw_tools_fragment.*
import top.defaults.colorpicker.ColorPickerPopup

class DrawingFragment : Fragment() {
    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {

        return inflater.inflate(
            R.layout.draw_fragment,
            container, false
        )
    }
}
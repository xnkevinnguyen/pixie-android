package com.pixie.android.ui.draw

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.Fragment
import androidx.lifecycle.ViewModelProvider
import com.pixie.android.R
import com.pixie.android.ui.draw.canvas.CanvasViewModel
import com.pixie.android.utilities.InjectorUtils

class DrawFragment : Fragment() {
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

    override fun onDestroyView() {
        // Set DrawCommandHistory whenever you enter the canvas page or else it crashes
        val factory = InjectorUtils.provideCanvasViewModelFactory()
        val viewModel = ViewModelProvider(this, factory).get(CanvasViewModel::class.java)

        viewModel.resetDrawCommandHistory()
        super.onDestroyView()
    }
}
package com.pixie.android.ui.draw

import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import com.pixie.android.R


class DrawActivity : AppCompatActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        // Instantiates CanvasFragment and DrawToolsFragment

        setContentView(R.layout.draw_activity)
    }

}
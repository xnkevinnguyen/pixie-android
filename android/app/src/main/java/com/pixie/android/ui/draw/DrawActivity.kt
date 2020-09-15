package com.pixie.android.ui.draw
import android.os.Bundle
import androidx.appcompat.app.AppCompatActivity
import com.microsoft.appcenter.AppCenter
import com.microsoft.appcenter.analytics.Analytics
import com.microsoft.appcenter.crashes.Crashes
import com.pixie.android.R


class DrawActivity : AppCompatActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {

        AppCenter.start(
            application, "47098cee-c78c-46cf-810f-bb72f246191f",
            Analytics::class.java, Crashes::class.java
        )
        super.onCreate(savedInstanceState)

        // Instantiates CanvasFragment and DrawToolsFragment
        setContentView(R.layout.draw_activity)
    }

}
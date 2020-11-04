package com.pixie.android.ui.draw.settings

import android.content.Context
import android.content.ContextWrapper
import android.content.res.Configuration
import java.util.*


class MyContextWrapper(base: Context?) : ContextWrapper(base) {

    fun wrap(context: Context, language: String): ContextWrapper {
        var context: Context = context
        val config: Configuration = context.resources.configuration
        var sysLocale: Locale? = null
        sysLocale = getSystemLocale(config)

        if (language != "" && sysLocale.language != language) {
            val locale = Locale(language)
            Locale.setDefault(locale)
            setSystemLocale(config, locale)
        }
        context = context.createConfigurationContext(config)
        return MyContextWrapper(context)
    }


    private fun getSystemLocale(config: Configuration): Locale {
        return config.locales.get(0)
    }

    private fun setSystemLocale(config: Configuration, locale: Locale?) {
        config.setLocale(locale)
    }

}
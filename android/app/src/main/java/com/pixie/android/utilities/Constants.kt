package com.pixie.android.utilities

class Constants {
    companion object {
        const val SHARED_PREFERENCES_LOGIN = "Login"
        const val SHARED_PREFERENCES_LOGIN_STATUS = "isLoggedIn"
        const val PLACEHOLDER_AUTH_ERROR = "Invalid Username or Password"
        const val PLACEHOLDER_REGISTRATION_ERROR = "Error registering"
        const val USER_ID = "UserID"
        const val USERNAME = "Username"
        const val SHARED_PREFERENCES_SETTING = "Setting"
        const val THEME = "Theme"
        const val LANGUAGE = "Language"
        const val APPLIEDCHANGE = "Applied change"
        const val NOTIFICATION = "Notification"
        const val SHARED_PREFERENCES_GAME = "Game"
        const val GAME_DIFF = "Game difficulty"
        const val GAME_MODE = "Game mode"
        const val GAME_LANGUAGE = "Game language"
        const val GAME_CHAT_VALUE = "Game chat value"


        const val MAIN_CHANNEL_ID = 1.0
        const val LANGUAGE_ENGLISH = "English"
        const val LANGUAGE_FRENCH = "French"

        const val AVATAR_RING_COLOR_YELLOW = "#FFDF00"
        const val AVATAR_RING_COLOR_SILVER = "#C0C0C0"

    }
}


enum class PathStatus {
    BEGIN,
    ONGOING,
    END

}
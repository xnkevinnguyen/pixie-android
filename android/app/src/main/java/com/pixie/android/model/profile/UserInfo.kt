package com.pixie.android.model.profile

import com.pixie.android.model.history.ConnectionHistory
import com.pixie.android.model.history.GameHistory

data class UserInfo (val username: String, val firstName: String, val lastName: String, val avatarForeground:String?,
                     val avatarBackground: String?, val createdAt: String)
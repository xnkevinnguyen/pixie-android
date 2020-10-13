package com.pixie.android.data.profile

import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import com.pixie.android.data.chat.ChatDataSource
import com.pixie.android.model.chat.MessageData
import com.pixie.android.model.profile.UserInfo
import com.pixie.android.model.user.LoginFormState

class ProfileRepository( private val dataSource: ProfileDataSource) {
    private var userProfileInfo = MutableLiveData<UserInfo>()

    fun getUserProfile(): LiveData<UserInfo> {
        return userProfileInfo
    }
}
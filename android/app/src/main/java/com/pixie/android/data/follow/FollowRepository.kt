package com.pixie.android.data.follow

import android.util.Log
import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import com.pixie.android.data.user.UserDataSource
import com.pixie.android.data.user.UserRepository
import com.pixie.android.model.chat.ChannelParticipant

class FollowRepository() {

    private var listFollow = MutableLiveData<ArrayList<ChannelParticipant>>()
        .apply { this.postValue(arrayListOf()) }

    fun getListFollow(): LiveData<ArrayList<ChannelParticipant>> {
        return listFollow
    }

    fun isUserInFollowList(user:ChannelParticipant): Boolean{
        val followValues = listFollow.value
        if(!followValues.isNullOrEmpty()){
            return followValues.contains(user)
        }
        return false
    }

    fun addUserFollowList(user: ChannelParticipant){
        if(!listFollow.value?.contains(user)!!) {
            listFollow.value?.add(user)
        }
    }

    fun removeUserFollowList(user:ChannelParticipant){
        if(listFollow.value?.contains(user)!!) {
            listFollow.value?.remove(user)
        }
    }

    // Singleton
    companion object {
        @Volatile
        private var instance: FollowRepository? = null
        fun getInstance() = instance ?: synchronized(this) {
            instance ?: FollowRepository().also {
                instance = it
            }
        }
    }
}
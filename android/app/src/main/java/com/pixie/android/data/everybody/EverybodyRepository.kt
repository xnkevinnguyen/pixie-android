package com.pixie.android.data.everybody

import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import com.pixie.android.data.user.UserRepository
import com.pixie.android.model.chat.ChannelParticipant
import kotlinx.coroutines.runBlocking

class EverybodyRepository(private val dataSource: EverybodyDataSource,
                          private val userRepository: UserRepository) {

    private var userList =
        MutableLiveData<ArrayList<ChannelParticipant>>().apply {
            arrayListOf<ChannelParticipant>()
        }

    fun getUserList(): LiveData<ArrayList<ChannelParticipant>> {
        fetchUserList()
        return userList
    }

    private fun fetchUserList(){
        var users: ArrayList<ChannelParticipant>
        runBlocking {
            users = dataSource.getAllUsers(userRepository.getUser().userId)
        }

        var me: ArrayList<ChannelParticipant?> = arrayListOf()
        for (user in users){
            if(user.id == userRepository.getUser().userId) me.add(user)
            if(user.isVirtual != null) {
                if (user.isVirtual) me.add(user)
            }
        }

       for (user in me){
            users.remove(user)
        }
        userList.postValue(users)
    }

    // Singleton
    companion object {
        @Volatile
        private var instance: EverybodyRepository? = null
        fun getInstance() = instance ?: synchronized(this) {

            val dataSource = EverybodyDataSource()
            instance ?: EverybodyRepository(
                dataSource,
                UserRepository.getInstance()
            ).also {
                instance = it
            }
        }
    }
}
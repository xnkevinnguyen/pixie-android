package com.pixie.android.data.friend

import android.util.Log
import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import com.pixie.android.data.user.UserRepository
import com.pixie.android.model.RequestResult
import com.pixie.android.model.chat.ChannelData
import com.pixie.android.model.chat.ChannelParticipant
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import kotlinx.coroutines.runBlocking

class FriendListRepository(private val dataSource: FriendListDataSource,
                           private val userRepository: UserRepository) {

    private var friendList =
        MutableLiveData<ArrayList<ChannelParticipant>>().apply {
            arrayListOf<ChannelParticipant>()
        }

    fun getFriendList(): LiveData<ArrayList<ChannelParticipant>> {
        fetchFriendList()
        return friendList
    }

    fun addPlayer(friendId: Double, onResult: (RequestResult) -> Unit){
        CoroutineScope(Dispatchers.IO).launch {
            val result = dataSource.addFriend(
                friendId,
                userRepository.getUser().userId

            )
            CoroutineScope(Dispatchers.Main).launch {
                onResult(RequestResult(result))
            }

        }
    }

    fun removeFriend(friendId: Double, onResult: (RequestResult) -> Unit) {
        CoroutineScope(Dispatchers.IO).launch {
            val result = dataSource.removeFriend(
                friendId,
                userRepository.getUser().userId
            )
            CoroutineScope(Dispatchers.Main).launch {
                onResult(RequestResult(result))
            }
        }
    }

//    fun fetchFriendList(){
//        CoroutineScope(Dispatchers.IO).launch {
//            dataSource.getFriendList(userRepository.getUser().userId, onReceiveMessage = {
//                if (it != null) {
//                    CoroutineScope(Dispatchers.Main).launch {
//                        friendList.postValue(it)
//                    }
//                }
//                else {
//                    friendList.postValue(arrayListOf())
//                }
//            })
//        }
//    }

    fun fetchFriendList(){
        var friends: ArrayList<ChannelParticipant>
        runBlocking {
            friends = dataSource.getFriendList(userRepository.getUser().userId)
        }
        friendList.postValue(friends)
    }

    // Singleton
    companion object {
        @Volatile
        private var instance: FriendListRepository? = null
        fun getInstance() = instance ?: synchronized(this) {

            val dataSource = FriendListDataSource()
            instance ?: FriendListRepository(
                dataSource,
                UserRepository.getInstance()
            ).also {
                instance = it
            }
        }
    }

    // Function to make sure observer is notified when a data structure is modified
    // https://stackoverflow.com/questions/47941537/notify-observer-when-item-is-added-to-list-of-livedata
    fun <T> MutableLiveData<T>.notifyObserver() {
        this.value = this.value
    }
}
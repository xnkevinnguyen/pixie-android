package com.pixie.android.data.chat

import android.util.Log
import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import com.pixie.android.data.user.UserRepository
import com.pixie.android.model.chat.ChannelData
import com.pixie.android.model.chat.ChannelParticipant
import com.pixie.android.model.chat.MessageData
import kotlinx.coroutines.*
import kotlinx.coroutines.Dispatchers.IO
import kotlinx.coroutines.Dispatchers.Main
import kotlin.collections.ArrayList
import kotlin.collections.HashMap

class ChatRepository(
    private val dataSource: ChatDataSource,
    private val userRepository: UserRepository
) {

    val MAIN_CHANNEL_ID = 1.0
    private var mainChannelMessageList = MutableLiveData<MutableList<MessageData>>().apply {
        this.postValue(arrayListOf())
    }

    // Map with <ChannelID,Messages>
    private var channelMessages =
        MutableLiveData<MutableMap<Double, ArrayList<MessageData>>>().apply {
            this.postValue(HashMap())
        }
    private var channelMessageSubscriptions = HashMap<Double, Job>()
    private var channelParticipantSubscriptions = HashMap<Double, Job>()

    private var mainChannelParticipantList =
        MutableLiveData<MutableList<ChannelParticipant>>().apply {
            val loadingUsername = "Loading ..."
            val loadingID = 1.0
            this.postValue(arrayListOf(ChannelParticipant(loadingID, loadingUsername, false)))
        }
    private var userChannels = MutableLiveData<ArrayList<ChannelData>>()

    private var currentChannelID = MutableLiveData<Double>().apply {
        this.postValue(MAIN_CHANNEL_ID)
    }


    private lateinit var mainChannelMessageJob: Job
    private lateinit var mainChannelParticipantJob: Job

    fun getCurrentChannelID(): LiveData<Double> {
        return currentChannelID
    }


    fun getChannelMessages(): LiveData<MutableMap<Double, ArrayList<MessageData>>> {
        return channelMessages
    }

    fun getUserChannels(): LiveData<ArrayList<ChannelData>> {
        return userChannels
    }

    fun getMainChannelMessageList(): LiveData<MutableList<MessageData>> {
        return mainChannelMessageList
    }

    fun getMainChannelParticipantList(): LiveData<MutableList<ChannelParticipant>> {
        return mainChannelParticipantList
    }

    fun getJoinableChannels(): ArrayList<ChannelData> {
        var joinableChannels: ArrayList<ChannelData>
        runBlocking {
            joinableChannels = dataSource.getJoinableChannels(userRepository.getUser().userId)
        }
        return joinableChannels
    }

    fun fetchUserChannels() {
        CoroutineScope(IO).launch {
            val channelDataList =
                dataSource.getUserChannels(userRepository.getUser().userId, onReceiveMessage = {
                    if (it != null) {
                        CoroutineScope(Main).launch {

                            userChannels.postValue(ArrayList(it))
                            userChannels.notifyObserver()
                            //suscribe to messages
                            suscribeToUserChannelsMessages(it)
                            //suscribe to participant changes
                            suscribeToUserChannelsParticipants(it)
                        }
                    }
                })

        }

    }


    fun joinChannel(channelID: Double) {
        //enter channel
        val channelData = enterChannel(channelID)
        if (channelData != null) {
            // suscribe to messages
            addUserChannelMessageSubscription(channelData)
            //suscribe to participant changes
            addUserChannelParticipantSubscription(channelData)
            userChannels.value?.add(channelData)
            userChannels.notifyObserver()
        } else {
            Log.d("ApolloException", "Error on joinChannel")
        }

    }
    fun createChannel(channelName:String){
            val channelData = createGetChannel(channelName)

            if (channelData != null) {
                // suscribe to messages
                addUserChannelMessageSubscription(channelData)
                //suscribe to participant changes
                addUserChannelParticipantSubscription(channelData)
                userChannels.value?.add(channelData)
                userChannels.notifyObserver()
            } else {
                Log.d("ApolloException", "Error on createChannel")
            }

    }


    fun suscribeToUserChannelsMessages(newUserChannels: ArrayList<ChannelData>) {
        //clear current channels subscriptions
        channelMessageSubscriptions.clear()
        //fetch subscriptions for each channels
        newUserChannels.forEach { channelData ->
            val subscriptionJob = CoroutineScope(IO).launch {
                dataSource.suscribeToChannelMessages(
                    userRepository.getUser().userId,
                    channelData.channelID,
                    onReceiveMessage = {
                        // Main thread only used to modify values
                        CoroutineScope(Main).launch {
                            it.belongsToCurrentUser =
                                it.userName == userRepository.getUser().username
                            if (channelMessages.value?.get(channelData.channelID) == null) {
                                channelMessages.value?.put(channelData.channelID, arrayListOf(it))
                            } else {
                                channelMessages.value?.get(channelData.channelID)?.add(it)
                            }
                            //Only update UI if the change is on selected channel
                            if (currentChannelID.value == channelData.channelID) {
                                channelMessages.notifyObserver()
                            }
                        }


                    })
            }
            channelMessageSubscriptions.put(channelData.channelID, subscriptionJob)
        }

    }

    // suscribe to all joined channels participants
    fun suscribeToUserChannelsParticipants(newUserChannels: ArrayList<ChannelData>) {
        //clear current channels subscriptions
        channelParticipantSubscriptions.clear()
        //fetch subscriptions for each channels
        newUserChannels.forEach { channelData ->
            val subscriptionJob = CoroutineScope(IO).launch {
                dataSource.suscribeToChannelChange(
                    userRepository.getUser().userId,
                    channelData.channelID,
                    onChannelChange = { channelData ->
                        // Main thread only used to modify values

                        //TODO replace userChannels with a hashmap
                        CoroutineScope(Main).launch {
                            val channelToUpdate = userChannels.value?.filter {
                                it.channelID == channelData.channelID
                            }
                            if (channelToUpdate.isNullOrEmpty()) {
                                Log.d(
                                    "suscribeToUserChannelParticipants",
                                    "No channel matching subscriptions"
                                )
                            } else {
                                channelToUpdate.last().participantList = channelData.participantList
                                userChannels.notifyObserver()
                            }

                        }
                    })
            }
            channelParticipantSubscriptions.put(channelData.channelID, subscriptionJob)

        }

    }

    fun addUserChannelParticipantSubscription(channelData: ChannelData) {
        val subscriptionJob = CoroutineScope(IO).launch {
            dataSource.suscribeToChannelChange(
                userRepository.getUser().userId,
                channelData.channelID,
                onChannelChange = { channelData ->
                    // Main thread only used to modify values
                    //TODO replace userChannels with a hashmap
                    CoroutineScope(Main).launch {
                        val channelToUpdate = userChannels.value?.filter {
                            it.channelID == channelData.channelID
                        }
                        if (channelToUpdate.isNullOrEmpty()) {
                            Log.d("addUserChannelParticipantSubscription", "Channel is inexistent")
                        } else {
                            channelToUpdate.last().participantList = channelData.participantList
                            userChannels.notifyObserver()
                        }

                    }
                })
        }
        channelParticipantSubscriptions.put(channelData.channelID, subscriptionJob)
    }

    fun addUserChannelMessageSubscription(channelData: ChannelData) {
        val subscriptionJob = CoroutineScope(IO).launch {
            dataSource.suscribeToChannelMessages(
                userRepository.getUser().userId,
                channelData.channelID,
                onReceiveMessage = {
                    // Main thread only used to modify values
                    CoroutineScope(Main).launch {
                        it.belongsToCurrentUser =
                            it.userName == userRepository.getUser().username
                        if (channelMessages.value?.get(channelData.channelID) == null) {
                            channelMessages.value?.put(channelData.channelID, arrayListOf(it))
                        } else {
                            channelMessages.value?.get(channelData.channelID)?.add(it)
                        }
                        //Only update UI if the change is on selected channel
                        if (currentChannelID.value == channelData.channelID) {
                            channelMessages.notifyObserver()
                        }
                    }

                })
        }
        channelMessageSubscriptions.put(channelData.channelID, subscriptionJob)
    }

    fun suscribeToUserChannelListChanges() {
        CoroutineScope(IO).launch {
            dataSource.suscribeToChannelListChange(
                userRepository.getUser().userId,
                onChannelAdded = {

                    if (it.participantList?.contains(userRepository.getUserAsChannelParticipant()) == true) {
                        //handle case where user is in the channel
                        CoroutineScope(Main).launch {
                            userChannels.value?.add(it)
                            addUserChannelMessageSubscription(channelData = it)
                            userChannels.notifyObserver()
                        }
                    }

                },

                onChannelRemoved = { channelRemovedData ->

                    CoroutineScope(Main).launch {
                        channelMessages.value?.remove(channelRemovedData.channelID)
                        channelMessageSubscriptions.remove(channelRemovedData.channelID)
                        removeChannelFromUserList(channelRemovedData.channelID)
                    }


                })
        }
    }

    fun enterChannel(channelID: Double): ChannelData? {
        var channelData: ChannelData?
        runBlocking {
            channelData =
                dataSource.enterChannel(channelID, userRepository.getUser().userId)
        }
        return channelData
    }
    fun createGetChannel(channelName: String):ChannelData?{
        var channelData: ChannelData?
        runBlocking {
            channelData =
                dataSource.createChannel(channelName, userRepository.getUser().userId)
        }
        return channelData
    }

    fun setCurrentChannelID(id: Double) {
        currentChannelID.postValue(id)
    }

    fun clearChannels() {
        mainChannelMessageList.postValue(arrayListOf())
        mainChannelParticipantList.postValue(arrayListOf())
    }

    fun exitChannel(channelID: Double) {
        if(currentChannelID.value == channelID){
        currentChannelID.postValue(MAIN_CHANNEL_ID)
        }
        userChannels.value?.removeIf {
            it.channelID == channelID
        }
        userChannels.notifyObserver()

        channelMessages.value?.remove(channelID)

        //unsuscription
        channelParticipantSubscriptions.remove(channelID)
        channelMessageSubscriptions.remove(channelID)
        CoroutineScope(IO).launch {
            val userID = userRepository.getUser().userId
            dataSource.exitChannel(channelID, userID)
        }
    }

    fun cancelMainChannelSubscriptions() {
        mainChannelMessageJob.cancel()
        mainChannelParticipantJob.cancel()
    }







    fun sendMessage(channelID: Double, message: String) {
        CoroutineScope(IO).launch {
            val data = dataSource.sendMessageToChannel(
                message,
                channelID,
                userRepository.getUser().userId
            )
            if (data?.addMessage == null) {
                // handle possible error
            }
        }
    }


    fun removeChannelFromUserList(channelID: Double) {
        userChannels.value?.removeIf { userChannelData ->
            userChannelData.channelID == channelID
        }
        userChannels.notifyObserver()
    }

    // Singleton
    companion object {
        @Volatile
        private var instance: ChatRepository? = null
        fun getInstance() = instance ?: synchronized(this) {

            val chatDataSource = ChatDataSource()
            instance ?: ChatRepository(chatDataSource, UserRepository.getInstance()).also {
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
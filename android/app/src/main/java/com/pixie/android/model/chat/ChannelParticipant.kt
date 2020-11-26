package com.pixie.android.model.chat

data class ChannelParticipant (val id:Double,val username:String,val isOnline:Boolean?,val isVirtual:Boolean? =null,
                               val avatarForeground: String? = null,val avatarBackground:String?=null)


package com.pixie.android.model.draw

import com.pixie.android.type.CommandStatus

data class ServerDrawHistoryCommand (private val commandPathID: Double, private val commandType:CommandType)
package com.pixie.android.model.draw

import com.pixie.android.type.CommandStatus

data class ServerDrawHistoryCommand (
     val commandPathID: Double,
     val commandType:CommandType)
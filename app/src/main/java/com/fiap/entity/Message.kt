package com.fiap.entity

data class Message(
   var text: String = "",
   var timestamp: Long = 0,
   var fromId: String = "",
   var toId: String = ""
) {}
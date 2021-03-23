package com.fiap.entity

data class Contact(
        val uuid: String = "",
        val email: String = "",
        val lastMessage: String = "",
        val timestamp: Long = 0,
        val photoUrl: String = ""
) {
}
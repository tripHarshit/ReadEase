package com.example.readease.model

data class MUser(
    val id: String?,
    val userId: String,
    val displayName: String,
    val avatarUrl: String){

    fun toMap():MutableMap<String,Any> {
        return mutableMapOf(
            "user_id" to this.userId,
            "displayName" to this.displayName,
            "avatarUrl" to this.avatarUrl
        )
    }
}


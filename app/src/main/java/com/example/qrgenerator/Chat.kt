package com.example.qrgenerator

data class Chat(
    val id: String,
    val name: String,
    val message: String
) {
    constructor(data: Map<*, *>) : this(
        data["id"] as String,
        data["name"] as String,
        data["message"] as String
    )
}
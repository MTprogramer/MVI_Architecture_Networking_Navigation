package com.example.mviarchitecturektorhttpimp.model

import kotlinx.serialization.Serializable


// Data class representing the Post model from the API
@Serializable
data class Post(
    val userId: Int,
    val id: Int,
    val title: String,
    val body: String
)
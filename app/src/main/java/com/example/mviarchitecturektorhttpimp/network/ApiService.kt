package com.example.mviarchitecturektorhttpimp.network

import com.example.mviarchitecturektorhttpimp.model.Post


// Interface defining the contract for API operations


interface ApiService {
    suspend fun getPosts(): List<Post>
    fun close()
}
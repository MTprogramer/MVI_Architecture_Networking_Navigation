package com.example.mviarchitecturektorhttpimp.MVI_States_Models

import com.example.mviarchitecturektorhttpimp.model.Post

sealed class PostState {
    data object Loading : PostState()
    data class Posts(val posts: List<Post>) : PostState()
    data class Error(val error: String) : PostState()
}

data class PostStateMethod2(
    val isLoading: Boolean = false,         // General loading indicator
    val posts: List<Post> = emptyList(),    // List of posts
    val error: String? = null,              // General error message
    val isDeleting: Boolean = false,        // Specific loading state for deletion
    val deleteError: String? = null         // Specific error for deletion
)
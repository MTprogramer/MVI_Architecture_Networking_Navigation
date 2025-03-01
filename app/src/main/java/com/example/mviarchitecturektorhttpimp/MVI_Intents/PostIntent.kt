package com.example.mviarchitecturektorhttpimp.MVI_Intents


// Intents define user actions in MVI
// The "Intent" represents user actions or events that trigger changes to the state. In MVI, intents are the only way to initiate state changes.

sealed class PostIntent {
    data object LoadPosts : PostIntent()
    data object LoadPostsMethod2 : PostIntent()
    data object NavigateToNextScreen : PostIntent() //
}
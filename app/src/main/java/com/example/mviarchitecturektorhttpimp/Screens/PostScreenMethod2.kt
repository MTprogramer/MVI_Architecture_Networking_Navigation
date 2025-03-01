package com.example.mviarchitecturektorhttpimp.Screens

import androidx.compose.foundation.layout.*
import androidx.compose.material3.*
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.collectAsState
import androidx.compose.ui.Modifier
import androidx.lifecycle.viewmodel.compose.viewModel
import com.example.mviarchitecturektorhttpimp.MVI_Intents.PostIntent
import com.example.mviarchitecturektorhttpimp.ViewModels.PostViewModel
import com.example.mviarchitecturektorhttpimp.network.KtorApiService

@Composable
fun PostScreenMethod2(
    viewModel: PostViewModel = viewModel { PostViewModel(KtorApiService()) }  // Manually provide KtorApiService
) {
    val state2 = viewModel.state2.collectAsState()

    // Trigger initial data load when the screen is first composed
    LaunchedEffect(Unit) {
        viewModel.handleIntent(PostIntent.LoadPostsMethod2)
    }

    Surface(
        modifier = Modifier.fillMaxSize(),
        color = MaterialTheme.colorScheme.background
    ) {

        when{  // loading post method 2
            state2.value.isLoading -> LoadingView()
            state2.value.error != null -> ErrorView(state2.value.error!!)
            state2.value.posts.isNotEmpty() -> PostListView(state2.value.posts)
        }
    }
}
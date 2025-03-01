package com.example.mviarchitecturektorhttpimp.Screens

import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.ArrowForward
import androidx.compose.material3.*
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.collectAsState
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import androidx.lifecycle.viewmodel.compose.viewModel
import androidx.navigation.NavHostController
import com.example.mviarchitecturektorhttpimp.MVI_Intents.PostIntent
import com.example.mviarchitecturektorhttpimp.MVI_States_Models.PostState
import com.example.mviarchitecturektorhttpimp.MVI_States_Models.events.NavigationEvent
import com.example.mviarchitecturektorhttpimp.ViewModels.PostViewModel
import com.example.mviarchitecturektorhttpimp.model.Post
import com.example.mviarchitecturektorhttpimp.network.KtorApiService

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun PostScreen(
    viewModel: PostViewModel = viewModel { PostViewModel(KtorApiService()) },  // Manually provide KtorApiService
    navController: NavHostController
) {
    val state = viewModel.state.collectAsState()

    // Trigger initial data load when the screen is first composed
    LaunchedEffect(Unit) {
        viewModel.handleIntent(PostIntent.LoadPosts)
    }

    // Handle navigation events separately
    LaunchedEffect(Unit) {    // it means the coroutine is launched once, and that coroutine can keep running and processing events as long as the composable remains in the composition.
        viewModel.navigationEvents.collect { event ->
            when (event) {
                is NavigationEvent.NavigateToNextScreen -> navController.navigate("nextScreen")
            }
        }
    }

   Scaffold(
       topBar = {
           TopAppBar(
               title = {
                   Text("Post List")
               },
               actions = {
                   IconButton(onClick = { viewModel.handleIntent(PostIntent.NavigateToNextScreen) }) {
                       Icon(imageVector = Icons.Default.ArrowForward, contentDescription = "next")
                   }
               }
           )
       }
   ) { padding ->
       Surface(
           modifier = Modifier
               .fillMaxSize()
               .padding(padding),
           color = MaterialTheme.colorScheme.background
       ) {
           when (val currentState = state.value) {  // loading post method 1
               is PostState.Loading -> LoadingView()
               is PostState.Posts -> PostListView(currentState.posts)
               is PostState.Error -> ErrorView(currentState.error)
           }
       }
   }
}

@Composable
fun LoadingView() {
    Box(modifier = Modifier.fillMaxSize()) {
        CircularProgressIndicator(modifier = Modifier.align(androidx.compose.ui.Alignment.Center))
    }
}

@Composable
fun ErrorView(message: String) {
    Box(modifier = Modifier.fillMaxSize()) {
        Text(
            text = "Error: $message",
            modifier = Modifier.align(androidx.compose.ui.Alignment.Center),
            color = MaterialTheme.colorScheme.error
        )
    }
}

@Composable
fun PostListView(posts: List<Post>) {
    LazyColumn(
        modifier = Modifier.fillMaxSize(),
        contentPadding = PaddingValues(16.dp)
    ) {
        items(posts) { post ->
            PostItem(post)
            Spacer(modifier = Modifier.height(8.dp))
        }
    }
}

@Composable
fun PostItem(post: Post) {
    Card(
        modifier = Modifier.fillMaxWidth(),
        elevation = CardDefaults.cardElevation(defaultElevation = 2.dp)
    ) {
        Column(
            modifier = Modifier
                .padding(16.dp)
                .fillMaxWidth()
        ) {
            Text(
                text = post.title,
                style = MaterialTheme.typography.titleMedium,
                color = MaterialTheme.colorScheme.primary
            )
            Spacer(modifier = Modifier.height(8.dp))
            Text(
                text = post.body,
                style = MaterialTheme.typography.bodyMedium
            )
        }
    }
}
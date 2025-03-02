package com.example.mviarchitecturektorhttpimp.ViewModels

import android.util.Log
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.mviarchitecturektorhttpimp.MVI_Intents.PostIntent
import com.example.mviarchitecturektorhttpimp.MVI_States_Models.PostState
import com.example.mviarchitecturektorhttpimp.MVI_States_Models.PostStateMethod2
import com.example.mviarchitecturektorhttpimp.MVI_States_Models.events.NavigationEvent
import com.example.mviarchitecturektorhttpimp.network.ApiService
import kotlinx.coroutines.channels.Channel
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.receiveAsFlow
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.launch

/*  Why Use Channel Instead of Flow for Navigation Events?
    What’s the Difference?
   Flow:
       A stream of values emitted over time, typically used for continuous state updates (e.g., UI state like postState).
       Observers (e.g., collect) receive all values emitted while active, and it’s stateful by nature (e.g., StateFlow holds the latest value and when UI recompose for any reason it again hit navigation).
       Best for persistent data that the UI reflects continuously.
   Channel:
        A mechanism for sending one-shot, discrete events (e.g., "navigate now").
        A Channel is a queue of events, not a state holder. When you send an event It’s queued and consumed once by the collector (e.g., collect { ... }), then gone.
        Avoids recomposition issues because it doesn’t hold a current value—events are transient.
        Acts like a queue: events are sent and consumed once, not replayed unless explicitly configured means if event consumed once never effect anywhere until new event is not fired  (e.g., with Channel.RENDEZVOUS or CONFLATED).
        Converted to a Flow with receiveAsFlow() for collection, but retains its event-like nature. */

/*   Channel.RENDEZVOUS:
              Capacity 0, suspends sender until receiver is ready—not ideal for navigation due to delays when UI isn’t collecting.
     Channel.UNLIMITED:
              Unlimited capacity, queues all events—poor for navigation as it stacks screens with every click.
     Channel.BUFFERED:
              Default 64 capacity, suspends when full—unsuitable for navigation due to queuing and potential suspension.
     Custom (e.g., Channel(1)):
             User-defined capacity, suspends when full—inefficient for navigation as it queues events and delays sender.
     Channel.CONFLATED:
              Capacity 1 with conflation, keeps only the latest event and never suspends—perfect for navigation as it ensures the most recent intent is processed once without stacking or delays. */

/* 🔹 Does ViewModel Fit MVI Rules?
            ✅ Yes, if used correctly
            ❌ No, if misused (e.g., mutable state inside ViewModel)

  How to use ViewModel in MVI correctly?
            ViewModel as a Bridge (Not a State Holder)

            It acts as an Intent processor that collects UI events (Intent), processes them, and exposes State to the UI.
            It should not hold any mutable state inside it.
            State Management in ViewModel

           Use StateFlow (or LiveData) to expose a single source of truth to the UI.
           Keep state immutable.
           Business Logic Should Stay in Use Cases/Repository

          ViewModel should only process intents and delegate business logic to Use Cases or Repository.
          Use ViewModel for Surviving Config Changes

          Since ViewModel survives configuration changes, it can help maintain the current state without needing to restore it from scratch. */

class PostViewModel(
    private val apiService: ApiService // ✅ Dependency Injection for easy testing
) : ViewModel() {

    // 🔹 State Management (Using Immutable StateFlow)
    // _state is private (internal updates), state is exposed (read-only)
    private val _state = MutableStateFlow<PostState>(PostState.Loading)
    val state = _state // ✅ Exposing immutable StateFlow

    // 🔹 Alternative state representation using PostStateMethod2
    private val _state2 = MutableStateFlow(PostStateMethod2())
    val state2 = _state2 // ✅ Another immutable StateFlow for a different state model

    // 🔹 Navigation Events (Using Channel to handle one-time events)
    private val _navigationEvents = Channel<NavigationEvent>(Channel.CONFLATED)
    val navigationEvents = _navigationEvents.receiveAsFlow() // ✅ Exposing as Flow to prevent direct modification

    /**
     * Processes UI intents and delegates actions accordingly.
     * This keeps UI logic out of the ViewModel, ensuring **unidirectional data flow**.
     */
    fun handleIntent(intent: PostIntent) {
        when (intent) {
            is PostIntent.LoadPosts -> loadPosts()
            is PostIntent.LoadPostsMethod2 -> loadPostsMethod2()
            is PostIntent.NavigateToNextScreen -> navigateToNextScreen()
        }
    }

    /**
     * Fetches posts using an alternative state representation (`PostStateMethod2`).
     * Uses `update {}` to ensure immutability and safe state modification.
     */
    private fun loadPostsMethod2() {
        viewModelScope.launch {
            _state2.update { it.copy(isLoading = true, error = null) } // ✅ Updating immutable state correctly
            try {
                val posts = apiService.getPosts()
                _state2.update { it.copy(isLoading = false, posts = posts) } // ✅ State updated with posts
            } catch (e: Exception) {
                Log.e("PostViewModel", "Failed to load posts", e)
                _state2.update { it.copy(isLoading = false, error = e.message ?: "Unknown error") }
            }
        }
    }

    /**
     * Fetches posts and updates `PostState`.
     * Uses `try-catch` for error handling and follows **unidirectional data flow**.
     */
    private fun loadPosts() {
        viewModelScope.launch {
            _state.value = PostState.Loading // ✅ Set initial loading state
            _state.value = try {
                PostState.Posts(apiService.getPosts()) // ✅ New state with posts (immutability preserved)
            } catch (e: Exception) {
                PostState.Error(e.localizedMessage) // ✅ Error state (immutable)
            }
        }
    }

    /**
     * Sends a **one-time** navigation event.
     * Uses `Channel.CONFLATED` to ensure only the latest event is processed (no event stacking).
     */
    private fun navigateToNextScreen() {
        viewModelScope.launch {
            _navigationEvents.send(NavigationEvent.NavigateToNextScreen) // ✅ Ensuring navigation event is handled once
        }
    }

    /**
     * Cleans up resources when the ViewModel is destroyed.
     * Ensures `apiService.close()` is called to properly release resources.
     */
    override fun onCleared() {
        apiService.close() // ✅ Proper cleanup (e.g., closing HttpClient)
        super.onCleared()
    }
}

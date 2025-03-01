package com.example.mviarchitecturektorhttpimp.MVI_States_Models.events

sealed class NavigationEvent {
    data object NavigateToNextScreen : NavigationEvent()
}
package com.example.mviarchitecturektorhttpimp

import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.activity.enableEdgeToEdge
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.padding
import androidx.compose.material3.Scaffold
import androidx.compose.ui.Modifier
import com.example.mviarchitecturektorhttpimp.Screens.PostScreen
import com.example.mviarchitecturektorhttpimp.navigations.NavigationSetup
import com.example.mviarchitecturektorhttpimp.ui.theme.MVIArchitectureKtorHttpImpTheme

class MainActivity : ComponentActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()
        setContent {
            MVIArchitectureKtorHttpImpTheme {
                Scaffold(modifier = Modifier.fillMaxSize()) { innerPadding ->
                   Box(Modifier.padding(paddingValues = innerPadding)) {
                       NavigationSetup()
                   }
                }
            }
        }
    }
}

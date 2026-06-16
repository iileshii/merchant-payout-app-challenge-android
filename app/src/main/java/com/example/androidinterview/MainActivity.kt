package com.example.androidinterview

import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.activity.enableEdgeToEdge
import com.example.androidinterview.feature.home.ui.HomeScreen
import com.example.androidinterview.ui.theme.AndroidInterviewTheme

class MainActivity : ComponentActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()
        setContent {
            AndroidInterviewTheme {
                HomeScreen()
            }
        }
    }
}

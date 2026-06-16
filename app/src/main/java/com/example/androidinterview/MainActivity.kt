package com.example.androidinterview

import android.os.Bundle
import androidx.activity.compose.setContent
import androidx.activity.enableEdgeToEdge
import androidx.fragment.app.FragmentActivity
import com.example.androidinterview.ui.MainScreen
import com.example.androidinterview.ui.theme.AndroidInterviewTheme

class MainActivity : FragmentActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()
        setContent {
            AndroidInterviewTheme {
                MainScreen()
            }
        }
    }
}

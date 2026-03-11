package com.example.immofollow

import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import com.example.immofollow.ui.ImmoApp
import com.example.immofollow.ui.theme.ImmoFollowTheme

class MainActivity : ComponentActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContent {
            ImmoFollowTheme {
                ImmoApp()
            }
        }
    }
}
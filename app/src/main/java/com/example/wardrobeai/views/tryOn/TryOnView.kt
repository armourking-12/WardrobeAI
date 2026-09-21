package com.example.wardrobeai.views.tryOn

import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.activity.viewModels
import com.example.wardrobeai.ui.theme.ClosetOrganiserTheme
import dagger.hilt.android.AndroidEntryPoint

@AndroidEntryPoint
class TryOnView : ComponentActivity() {

    private val viewModel: TryOnViewModel by viewModels()

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContent {
            ClosetOrganiserTheme {
                TryOnScreen(viewModel = viewModel)
            }
        }
    }
}
package com.example.habitflow

import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.activity.enableEdgeToEdge
import androidx.compose.material3.Surface
import androidx.compose.runtime.getValue
import androidx.hilt.navigation.compose.hiltViewModel
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import androidx.navigation.compose.rememberNavController
import com.example.habitflow.presentation.main.MainViewModel
import com.example.habitflow.presentation.navigation.HostNavGraph
import com.example.habitflow.presentation.theme.HabitFlowTheme
import dagger.hilt.android.AndroidEntryPoint

@AndroidEntryPoint
class MainActivity : ComponentActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()
        setContent {
            val viewModel: MainViewModel = hiltViewModel()
            val isDark by viewModel.isDarkTheme.collectAsStateWithLifecycle()
            HabitFlowTheme(darkTheme = isDark) {
                Surface {
                    val navController = rememberNavController()
                    HostNavGraph(navController)
                }
            }
        }
    }
}
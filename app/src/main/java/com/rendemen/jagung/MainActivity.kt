package com.rendemen.jagung

import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.SnackbarHostState
import androidx.compose.material3.Surface
import androidx.compose.runtime.remember
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
import androidx.navigation.compose.rememberNavController
import com.rendemen.jagung.data.AppDatabase
import com.rendemen.jagung.data.RendemenRepository
import com.rendemen.jagung.ui.screens.CalculatorScreen
import com.rendemen.jagung.ui.screens.HistoryScreen
import com.rendemen.jagung.viewmodel.RendemenViewModel

class MainActivity : ComponentActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        val database = AppDatabase.getDatabase(this)
        val repository = RendemenRepository(database.rendemenDao())
        val viewModel = RendemenViewModel(repository)

        setContent {
            MaterialTheme {
                Surface(color = MaterialTheme.colorScheme.background) {
                    val navController = rememberNavController()
                    val snackbarHostState = remember { SnackbarHostState() }

                    NavHost(navController = navController, startDestination = "calculator") {
                        composable("calculator") {
                            CalculatorScreen(
                                viewModel = viewModel,
                                onNavigateHistory = { navController.navigate("history") },
                                snackbarHostState = snackbarHostState
                            )
                        }
                        composable("history") {
                            HistoryScreen(
                                viewModel = viewModel,
                                onNavigateBack = { navController.popBackStack() }
                            )
                        }
                    }
                }
            }
        }
    }
}

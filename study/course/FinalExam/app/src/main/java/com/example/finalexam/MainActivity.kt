package com.example.finalexam

import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.activity.enableEdgeToEdge
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.material3.Surface
import androidx.compose.runtime.*
import androidx.compose.ui.Modifier
import androidx.lifecycle.viewmodel.compose.viewModel
import androidx.navigation.NavType
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
import androidx.navigation.compose.rememberNavController
import androidx.navigation.navArgument
import androidx.work.*
import com.example.finalexam.data.AppDatabase
import com.example.finalexam.data.ItemRepository
import com.example.finalexam.data.UserPreferencesRepository
import com.example.finalexam.ui.navigation.Screen
import com.example.finalexam.ui.screens.*
import com.example.finalexam.ui.theme.FinalExamTheme
import com.example.finalexam.viewmodel.InventoryViewModel
import com.example.finalexam.viewmodel.InventoryViewModelFactory
import com.example.finalexam.workers.JokeFetchWorker
import java.util.concurrent.TimeUnit

class MainActivity : ComponentActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        
        // Schedule periodic joke fetching
        scheduleJokeFetching()
        
        enableEdgeToEdge()
        setContent {
            FinalExamTheme {
                Surface(modifier = Modifier.fillMaxSize()) {
                    InventoryApp()
                }
            }
        }
    }
    
    private fun scheduleJokeFetching() {
        val constraints = Constraints.Builder()
            .setRequiredNetworkType(NetworkType.CONNECTED)
            .build()
        
        val workRequest = PeriodicWorkRequestBuilder<JokeFetchWorker>(
            30, TimeUnit.MINUTES
        )
            .setConstraints(constraints)
            .build()
        
        WorkManager.getInstance(applicationContext).enqueueUniquePeriodicWork(
            "JokeFetchWork",
            ExistingPeriodicWorkPolicy.KEEP,
            workRequest
        )
    }
}

@Composable
fun InventoryApp() {
    val navController = rememberNavController()
    val context = androidx.compose.ui.platform.LocalContext.current
    
    val database = remember { AppDatabase.getDatabase(context) }
    val itemRepository = remember { ItemRepository(database.itemDao()) }
    val userPrefsRepository = remember { UserPreferencesRepository(context) }
    
    val viewModel: InventoryViewModel = viewModel(
        factory = InventoryViewModelFactory(itemRepository, userPrefsRepository)
    )
    
    val isLoggedIn by viewModel.isLoggedIn.collectAsState(initial = false)
    val username by viewModel.username.collectAsState(initial = "")
    val lastJoke by viewModel.lastJoke.collectAsState(initial = "")
    
    val startDestination = if (isLoggedIn) Screen.Home.route else Screen.Login.route
    
    NavHost(
        navController = navController,
        startDestination = startDestination
    ) {
        composable(Screen.Login.route) {
            LoginScreen(
                onLoginSuccess = {
                    navController.navigate(Screen.Home.route) {
                        popUpTo(Screen.Login.route) { inclusive = true }
                    }
                },
                onLogin = { username ->
                    viewModel.login(username)
                }
            )
        }
        
        composable(Screen.Home.route) {
            HomeScreen(
                joke = lastJoke,
                onNavigateToCategory = { category ->
                    navController.navigate(Screen.ItemList.createRoute(category))
                },
                onNavigateToSettings = {
                    navController.navigate(Screen.Settings.route)
                }
            )
        }
        
        composable(
            route = Screen.ItemList.route,
            arguments = listOf(navArgument("category") { type = NavType.StringType })
        ) { backStackEntry ->
            val category = backStackEntry.arguments?.getString("category") ?: ""
            val items by viewModel.getItemsByCategory(category).collectAsState(initial = emptyList())
            
            ItemListScreen(
                category = category,
                items = items,
                onNavigateBack = { navController.popBackStack() },
                onNavigateToHome = {
                    navController.navigate(Screen.Home.route) {
                        popUpTo(Screen.Home.route) { inclusive = true }
                    }
                },
                onNavigateToSettings = {
                    navController.navigate(Screen.Settings.route)
                },
                onNavigateToItemDetail = { itemId ->
                    navController.navigate(Screen.ItemDetail.createRoute(itemId))
                },
                onAddItem = { name, price, quantity ->
                    viewModel.insertItem(name, category, price, quantity)
                }
            )
        }
        
        composable(
            route = Screen.ItemDetail.route,
            arguments = listOf(navArgument("itemId") { type = NavType.IntType })
        ) { backStackEntry ->
            val itemId = backStackEntry.arguments?.getInt("itemId") ?: 0
            val item by viewModel.getItem(itemId).collectAsState(initial = null)
            
            ItemDetailScreen(
                item = item,
                onNavigateBack = { navController.popBackStack() },
                onNavigateToHome = {
                    navController.navigate(Screen.Home.route) {
                        popUpTo(Screen.Home.route) { inclusive = true }
                    }
                },
                onNavigateToSettings = {
                    navController.navigate(Screen.Settings.route)
                },
                onEditItem = { name, category, price, quantity ->
                    viewModel.updateItem(itemId, name, category, price, quantity)
                },
                onDeleteItem = {
                    item?.let { viewModel.deleteItem(it) }
                    navController.popBackStack()
                }
            )
        }
        
        composable(Screen.Settings.route) {
            SettingsScreen(
                username = username,
                onLogout = {
                    viewModel.logout()
                    navController.navigate(Screen.Login.route) {
                        popUpTo(0) { inclusive = true }
                    }
                },
                onNavigateBack = { navController.popBackStack() },
                onNavigateToHome = {
                    navController.navigate(Screen.Home.route) {
                        popUpTo(Screen.Home.route) { inclusive = true }
                    }
                }
            )
        }
    }
}
package com.example.workoutanalyzer.ui

import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.padding
import androidx.compose.material3.BottomAppBar
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Scaffold
import androidx.compose.material3.SnackbarHost
import androidx.compose.material3.SnackbarHostState
import androidx.compose.material3.Text
import androidx.compose.material3.TopAppBar
import androidx.compose.material3.TopAppBarDefaults
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.MutableState
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.rememberCoroutineScope
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import com.example.workoutanalyzer.ui.model.AppData
import com.example.workoutanalyzer.ui.model.Screen
import com.example.workoutanalyzer.ui.model.UserPreferencesRepository
import com.example.workoutanalyzer.ui.screens.DashboardScreen
import com.example.workoutanalyzer.ui.screens.DurationScreen
import com.example.workoutanalyzer.ui.screens.HomeScreen
import com.example.workoutanalyzer.ui.screens.ProfileScreen
import kotlinx.coroutines.launch

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun App() {
    val snackbarHostState = remember { SnackbarHostState() }
    val scope = rememberCoroutineScope()
    val context = LocalContext.current
    val userPrefs = remember { UserPreferencesRepository(context) }

    val appDataState by userPrefs.appData.collectAsState(initial = AppData())

    val currentScreen: MutableState<Screen> = remember { mutableStateOf(Screen.Home) }

    LaunchedEffect(appDataState.totalCalories, appDataState.dailyGoalCalories) {
        if (!appDataState.goalAcknowledged && appDataState.totalCalories >= appDataState.dailyGoalCalories) {
            val newAppData = appDataState.copy(goalAcknowledged = true)
            scope.launch { userPrefs.saveAppData(newAppData) }
            scope.launch {
                snackbarHostState.showSnackbar("Daily goal achieved! Great job.")
            }
        }
    }

    Scaffold(
        topBar = {
            TopAppBar(
                colors = TopAppBarDefaults.topAppBarColors(
                    containerColor = MaterialTheme.colorScheme.primaryContainer,
                    titleContentColor = MaterialTheme.colorScheme.primary,
                ),
                title = {
                Text(
                    when (val s = currentScreen.value) {
                        Screen.Home -> "Workouts"
                        is Screen.Duration -> "Enter Duration"
                        Screen.Dashboard -> "Dashboard"
                        Screen.Profile -> "Profile"
                    }
                )
            })
        },
        bottomBar = {
            BottomNav(current = currentScreen.value, onSelect = { currentScreen.value = it })
        },
        snackbarHost = { SnackbarHost(snackbarHostState) }
    ) { padding ->
        Row(modifier = Modifier
            .fillMaxSize()
            .padding(padding)) {
            when (val s = currentScreen.value) {
                Screen.Home -> HomeScreen(
                    appData = appDataState,
                    onWorkoutClick = { type -> currentScreen.value = Screen.Duration(type) },
                    onResetGoal = {
                        val newAppData = appDataState.resetProgress()
                        scope.launch { userPrefs.saveAppData(newAppData) }
                    },
                    onEditGoal = { newGoal ->
                        val newAppData = appDataState.copy(dailyGoalCalories = newGoal, goalAcknowledged = false)
                        scope.launch { userPrefs.saveAppData(newAppData) }
                    }
                )
                is Screen.Duration -> DurationScreen(
                    type = s.type,
                    user = appDataState.user,
                    onBack = { currentScreen.value = Screen.Home },
                    onSubmit = { minutes, result ->
                        val newAppData = appDataState.recordWorkout(s.type, minutes, result.calories, result.distanceKm, result.steps)
                        scope.launch { userPrefs.saveAppData(newAppData) }
                        currentScreen.value = Screen.Home
                    }
                )
                Screen.Dashboard -> DashboardScreen(appData = appDataState)
                Screen.Profile -> ProfileScreen(
                    user = appDataState.user,
                    onUpdateUser = { upd ->
                        val newAppData = appDataState.copy(user = upd)
                        scope.launch { userPrefs.saveAppData(newAppData) }
                    }
                )
            }
        }
    }
}

@Composable
private fun BottomNav(current: Screen, onSelect: (Screen) -> Unit) {
    BottomAppBar(
        containerColor = MaterialTheme.colorScheme.primaryContainer,
        contentColor = MaterialTheme.colorScheme.primary,
    ) {
        Row(
            Modifier
                .fillMaxSize()
                .padding(horizontal = 8.dp),
            horizontalArrangement = Arrangement.SpaceEvenly,
            verticalAlignment = Alignment.CenterVertically
        ) {
            NavButton(label = "Home", selected = current is Screen.Home) { onSelect(Screen.Home) }
            NavButton(label = "Dashboard", selected = current is Screen.Dashboard) { onSelect(Screen.Dashboard) }
            NavButton(label = "Profile", selected = current is Screen.Profile) { onSelect(Screen.Profile) }
        }
    }
}

@Composable
private fun NavButton(label: String, selected: Boolean, onClick: () -> Unit) {
    Text(
        modifier = Modifier.clickable { onClick() },
        text = label,
        color = if (selected) MaterialTheme.colorScheme.primary else MaterialTheme.colorScheme.onSurface.copy(alpha = 0.6f),
        fontWeight = if (selected) FontWeight.Bold else FontWeight.Normal,
        style = MaterialTheme.typography.labelLarge
    )
}

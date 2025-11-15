package com.example.workoutanalyzer.ui.screens

import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.material3.Button
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.OutlinedButton
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.Text
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import com.example.workoutanalyzer.ui.components.ProgressRing
import com.example.workoutanalyzer.ui.components.WorkoutIcon
import com.example.workoutanalyzer.ui.model.AppData
import com.example.workoutanalyzer.ui.model.WorkoutType

@Composable
fun HomeScreen(
    appData: AppData,
    onWorkoutClick: (WorkoutType) -> Unit,
    onResetGoal: () -> Unit,
    onEditGoal: (Int) -> Unit
) {
    var editingGoal by remember { mutableStateOf(false) }
    var goalInput by remember { mutableStateOf(appData.dailyGoalCalories.toString()) }

    Column(
        modifier = Modifier
            .fillMaxSize()
            .padding(16.dp)
    ) {
        Text("Daily Goal", style = MaterialTheme.typography.titleLarge)
        Spacer(Modifier.height(16.dp))
        Row(
            modifier = Modifier
                .fillMaxWidth()
                .padding(vertical = 8.dp),
            horizontalArrangement = Arrangement.SpaceAround,
            verticalAlignment = Alignment.CenterVertically
        ) {
            val progress = (appData.totalCalories.toFloat() / appData.dailyGoalCalories.coerceAtLeast(1)).coerceIn(0f, 1f)
            ProgressRing(progress = progress, ringSize = 120.dp, label = "${appData.totalCalories}/${appData.dailyGoalCalories} kcal")
            Column(horizontalAlignment = Alignment.CenterHorizontally) {
                Button(onClick = { onResetGoal() }) { Text("Reset Progress") }
                Spacer(Modifier.height(8.dp))
                OutlinedButton(onClick = { editingGoal = true }) { Text("Edit Goal") }
            }
        }

        if (editingGoal) {
            EditGoalDialog(
                value = goalInput,
                onValueChange = { goalInput = it.filter { ch -> ch.isDigit() }.take(5) },
                onDismiss = { editingGoal = false },
                onSave = {
                    val v = goalInput.toIntOrNull()?.coerceAtLeast(50) ?: appData.dailyGoalCalories
                    onEditGoal(v)
                    editingGoal = false
                }
            )
        }

        Spacer(Modifier.height(16.dp))
        Text("Workouts", style = MaterialTheme.typography.titleLarge)
        Spacer(Modifier.height(8.dp))

        val items = WorkoutType.values().toList()
        LazyColumn(modifier = Modifier.fillMaxSize()) {
            items(items) { type ->
                val p = appData.perActivity[type]
                Card(
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(vertical = 8.dp)
                        .clickable { onWorkoutClick(type) },
                    elevation = CardDefaults.cardElevation(defaultElevation = 4.dp)
                ) {
                    Row(
                        Modifier
                            .fillMaxWidth()
                            .padding(16.dp),
                        horizontalArrangement = Arrangement.SpaceBetween,
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        Row(verticalAlignment = Alignment.CenterVertically) {
                            WorkoutIcon(type, size = 48.dp)
                            Spacer(Modifier.width(16.dp))
                            Column {
                                Text(type.name, style = MaterialTheme.typography.titleMedium)
                                Text(
                                    "${p?.minutes ?: 0} min • ${(p?.distanceKm ?: 0.0).let { String.format("%.1f km", it) }}",
                                    style = MaterialTheme.typography.bodyMedium,
                                    color = MaterialTheme.colorScheme.onSurfaceVariant
                                )
                            }
                        }
                        val goal = appData.dailyGoalCalories.coerceAtLeast(1)
                        val prog = ((p?.calories ?: 0).toFloat() / goal).coerceIn(0f, 1f)
                        ProgressRing(progress = prog, ringSize = 64.dp, label = "${p?.calories ?: 0}kcal")
                    }
                }
            }
        }
    }
}

@Composable
private fun EditGoalDialog(
    value: String,
    onValueChange: (String) -> Unit,
    onDismiss: () -> Unit,
    onSave: () -> Unit
) {
    Card(
        modifier = Modifier.fillMaxWidth(),
        elevation = CardDefaults.cardElevation(defaultElevation = 8.dp)
    ) {
        Column(Modifier.padding(16.dp)) {
            Text("Set daily calorie goal (kcal)", style = MaterialTheme.typography.titleMedium)
            Spacer(Modifier.height(16.dp))
            OutlinedTextField(value = value, onValueChange = onValueChange, label = { Text("kcal") })
            Spacer(Modifier.height(16.dp))
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.End
            ) {
                OutlinedButton(onClick = onDismiss) { Text("Cancel") }
                Spacer(Modifier.width(8.dp))
                Button(onClick = onSave) { Text("Save") }
            }
        }
    }
}

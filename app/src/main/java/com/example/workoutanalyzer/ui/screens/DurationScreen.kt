package com.example.workoutanalyzer.ui.screens

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.width
import androidx.compose.material3.Button
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.OutlinedButton
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.text.input.KeyboardType
import androidx.compose.ui.unit.dp
import com.example.workoutanalyzer.ui.components.WorkoutIcon
import com.example.workoutanalyzer.ui.model.Estimator
import com.example.workoutanalyzer.ui.model.User
import com.example.workoutanalyzer.ui.model.WorkoutType

@Composable
fun DurationScreen(
    type: WorkoutType,
    user: User,
    onBack: () -> Unit,
    onSubmit: (minutes: Int, result: Estimator.Result) -> Unit
) {
    val (minutesText, setMinutesText) = remember { mutableStateOf(30.toString()) }
    val minutes = minutesText.toIntOrNull() ?: 0
    val result = Estimator.estimate(type, minutes.coerceAtLeast(0), user)

    Column(
        modifier = Modifier
            .fillMaxSize()
            .padding(16.dp)
    ) {
        Row(
            modifier = Modifier.fillMaxWidth(),
            horizontalArrangement = Arrangement.spacedBy(12.dp),
            verticalAlignment = Alignment.CenterVertically
        ) {
            WorkoutIcon(type, size = 64.dp)
            Text(type.name, style = MaterialTheme.typography.headlineMedium)
        }
        Spacer(Modifier.height(24.dp))
        OutlinedTextField(
            value = minutesText,
            onValueChange = { setMinutesText(it.filter { ch -> ch.isDigit() }.take(4)) },
            label = { Text("Duration (minutes)") },
            modifier = Modifier.fillMaxWidth()
        )

        Spacer(Modifier.height(24.dp))

        Card(
            modifier = Modifier.fillMaxWidth(),
            elevation = CardDefaults.cardElevation(defaultElevation = 4.dp)
        ) {
            Column(Modifier.padding(16.dp)) {
                Text("Estimated Results", style = MaterialTheme.typography.titleLarge)
                Spacer(Modifier.height(16.dp))
                Text("Calories: ${result.calories} kcal", style = MaterialTheme.typography.bodyLarge)
                val distanceStr = String.format("%.2f km", result.distanceKm)
                Text("Distance: $distanceStr", style = MaterialTheme.typography.bodyLarge)
                Text("Steps: ${result.steps}", style = MaterialTheme. typography.bodyLarge)
            }
        }

        Spacer(Modifier.height(24.dp))

        Row(
            modifier = Modifier.fillMaxWidth(),
            horizontalArrangement = Arrangement.End
        ) {
            OutlinedButton(onClick = onBack) { Text("Cancel") }
            Spacer(Modifier.width(8.dp))
            Button(onClick = { if (minutes > 0) onSubmit(minutes, result) }) { Text("Add") }
        }
    }
}

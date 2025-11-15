package com.example.workoutanalyzer.ui.screens

import androidx.compose.foundation.Canvas
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.geometry.Offset
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.drawscope.Stroke
import androidx.compose.ui.unit.dp
import com.example.workoutanalyzer.ui.model.AppData
import com.example.workoutanalyzer.ui.model.WorkoutType

@Composable
fun DashboardScreen(appData: AppData) {
    Column(
        modifier = Modifier
            .fillMaxSize()
            .padding(16.dp)
    ) {
        Text("Today's Summary", style = MaterialTheme.typography.headlineMedium)
        Spacer(Modifier.height(16.dp))
        SummaryCard(appData)
        Spacer(Modifier.height(24.dp))
        Card(
            modifier = Modifier.fillMaxWidth(),
            elevation = CardDefaults.cardElevation(defaultElevation = 4.dp)
        ) {
            Column(Modifier.padding(16.dp)) {
                Text("Calories by Activity", style = MaterialTheme.typography.titleLarge)
                Spacer(Modifier.height(16.dp))
                CaloriesBarChart(appData)
            }
        }
    }
}

@Composable
private fun SummaryCard(appData: AppData) {
    Card(
        modifier = Modifier.fillMaxWidth(),
        elevation = CardDefaults.cardElevation(defaultElevation = 4.dp)
    ) {
        Column(modifier = Modifier.padding(16.dp)) {
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceAround,
                verticalAlignment = Alignment.CenterVertically
            ) {
                SummaryItem("Calories", "${appData.totalCalories} kcal")
                SummaryItem("Time", "${appData.totalMinutes} min")
            }
            Spacer(Modifier.height(16.dp))
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceAround,
                verticalAlignment = Alignment.CenterVertically
            ) {
                val dist = String.format("%.2f km", appData.totalDistanceKm)
                SummaryItem("Distance", dist)
                SummaryItem("Steps", "${appData.totalSteps}")
            }
        }
    }
}

@Composable
private fun SummaryItem(name: String, value: String) {
    Column(horizontalAlignment = Alignment.CenterHorizontally) {
        Text(name, style = MaterialTheme.typography.titleMedium)
        Text(value, style = MaterialTheme.typography.bodyLarge)
    }
}


@Composable
private fun CaloriesBarChart(appData: AppData) {
    val data = WorkoutType.values().map { it to (appData.perActivity[it]?.calories ?: 0) }
    val max = (data.maxOfOrNull { it.second } ?: 1).coerceAtLeast(1)
    val colors = listOf(
        MaterialTheme.colorScheme.primary,
        MaterialTheme.colorScheme.secondary,
        MaterialTheme.colorScheme.tertiary,
        MaterialTheme.colorScheme.primary.copy(alpha = 0.7f),
        MaterialTheme.colorScheme.secondary.copy(alpha = 0.7f)
    )
    Column(modifier = Modifier.fillMaxWidth()) {
        data.forEachIndexed { idx, (type, value) ->
            Row(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(vertical = 8.dp),
                verticalAlignment = Alignment.CenterVertically
            ) {
                Text(
                    modifier = Modifier.weight(0.3f),
                    text = type.name,
                    style = MaterialTheme.typography.bodyMedium
                )
                Canvas(modifier = Modifier
                    .weight(0.7f)
                    .height(24.dp)) {
                    val w = size.width * (value.toFloat() / max)
                    drawRect(
                        color = colors[idx % colors.size],
                        size = androidx.compose.ui.geometry.Size(w, size.height)
                    )
                    drawRect(color = Color.Black.copy(alpha = 0.1f), style = Stroke(width = 2f))
                }
            }
        }
    }
}

package com.example.workoutanalyzer.ui.model

import androidx.compose.runtime.Immutable
import kotlinx.serialization.Serializable
import kotlin.math.roundToInt

enum class WorkoutType { Running, Walking, Swimming, Cycling, Skating }

sealed interface Screen {
    data object Home : Screen
    data class Duration(val type: WorkoutType) : Screen
    data object Dashboard : Screen
    data object Profile : Screen
}

@Serializable
@Immutable
data class User(
    val name: String = "You",
    val age: Int = 30,
    val gender: String = "Unspecified",
    val heightCm: Int = 170,
    val weightKg: Int = 70,
    val strideLengthM: Double = 0.75
)

@Serializable
@Immutable
data class ActivityProgress(
    val minutes: Int = 0,
    val calories: Int = 0,
    val distanceKm: Double = 0.0,
    val steps: Int = 0
)

@Serializable
@Immutable
data class AppData(
    val user: User = User(),
    val dailyGoalCalories: Int = 500,
    val goalAcknowledged: Boolean = false,
    val perActivity: Map<WorkoutType, ActivityProgress> = WorkoutType.values().associateWith { ActivityProgress() },
    val history: List<HistoryEntry> = emptyList(),
    val caloriesBurnedSinceLastWeightUpdate: Int = 0
) {
    val totalCalories: Int get() = perActivity.values.sumOf { it.calories }
    val totalMinutes: Int get() = perActivity.values.sumOf { it.minutes }
    val totalDistanceKm: Double get() = perActivity.values.sumOf { it.distanceKm }
    val totalSteps: Int get() = perActivity.values.sumOf { it.steps }

    fun resetProgress(): AppData = copy(
        goalAcknowledged = false,
        perActivity = WorkoutType.values().associateWith { ActivityProgress() }
    )

    fun recordWorkout(type: WorkoutType, minutes: Int, calories: Int, distanceKm: Double, steps: Int): AppData {
        val old = perActivity[type] ?: ActivityProgress()
        val updated = old.copy(
            minutes = old.minutes + minutes,
            calories = old.calories + calories,
            distanceKm = old.distanceKm + distanceKm,
            steps = old.steps + steps
        )
        val newMap = perActivity.toMutableMap().apply { put(type, updated) }.toMap()
        val entry = HistoryEntry(type, minutes, calories, distanceKm, steps)
        return copy(
            perActivity = newMap,
            history = history + entry,
            caloriesBurnedSinceLastWeightUpdate = caloriesBurnedSinceLastWeightUpdate + calories
        ).withWeightUpdate()
    }

    private fun withWeightUpdate(): AppData {
        val caloriesPerKg = 7700 // roughly 3500 calories per pound
        if (caloriesBurnedSinceLastWeightUpdate < caloriesPerKg) {
            return this
        }

        val kgToLose = caloriesBurnedSinceLastWeightUpdate / caloriesPerKg
        val remainingCalories = caloriesBurnedSinceLastWeightUpdate % caloriesPerKg

        return copy(
            user = user.copy(weightKg = user.weightKg - kgToLose),
            caloriesBurnedSinceLastWeightUpdate = remainingCalories
        )
    }
}

@Serializable
@Immutable
data class HistoryEntry(
    val type: WorkoutType,
    val minutes: Int,
    val calories: Int,
    val distanceKm: Double,
    val steps: Int
)

object Estimator {
    // Approximate MET values
    private val mets = mapOf(
        WorkoutType.Running to 11.0,   // ~7 mph
        WorkoutType.Walking to 4.5,   // brisk
        WorkoutType.Swimming to 8.0,  // vigorous
        WorkoutType.Cycling to 10.0,  // ~12-14 mph
        WorkoutType.Skating to 12.0    // strenuous
    )

    private val speedsKmh = mapOf(
        WorkoutType.Running to 11.0,
        WorkoutType.Walking to 6.0,
        WorkoutType.Swimming to 2.5,
        WorkoutType.Cycling to 20.0,
        WorkoutType.Skating to 24.0
    )

    data class Result(val calories: Int, val distanceKm: Double, val steps: Int)

    fun estimate(type: WorkoutType, minutes: Int, user: User): Result {
        val hours = minutes / 60.0
        val met = mets[type] ?: 5.0
        val calories = (met * user.weightKg * hours).roundToInt()
        val distanceKm = (speedsKmh[type] ?: 5.0) * hours
        val steps = when (type) {
            WorkoutType.Walking, WorkoutType.Running -> (distanceKm * 1000 / user.strideLengthM).roundToInt()
            else -> 0
        }
        return Result(calories, distanceKm, steps)
    }
}

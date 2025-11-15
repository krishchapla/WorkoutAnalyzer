package com.example.workoutanalyzer.ui.components

import androidx.compose.foundation.Canvas
import androidx.compose.foundation.layout.size
import androidx.compose.material3.MaterialTheme
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.geometry.Offset
import androidx.compose.ui.graphics.Path
import androidx.compose.ui.graphics.drawscope.Fill
import androidx.compose.ui.graphics.drawscope.Stroke
import androidx.compose.ui.unit.Dp
import androidx.compose.ui.unit.dp
import com.example.workoutanalyzer.ui.model.WorkoutType

@Composable
fun WorkoutIcon(type: WorkoutType, size: Dp = 36.dp) {
    val color = MaterialTheme.colorScheme.primary
    Canvas(modifier = Modifier.size(size)) {
        val w = this.size.width
        val h = this.size.height
        val minDim = this.size.minDimension
        when (type) {
            WorkoutType.Running -> {
                // Right arrow
                val path = Path().apply {
                    moveTo(w * 0.2f, h * 0.5f)
                    lineTo(w * 0.7f, h * 0.5f)
                }
                drawPath(path = path, color = color, style = Stroke(width = minDim * 0.1f))
                val head = Path().apply {
                    moveTo(w * 0.6f, h * 0.3f)
                    lineTo(w * 0.85f, h * 0.5f)
                    lineTo(w * 0.6f, h * 0.7f)
                    close()
                }
                drawPath(head, color = color, style = Fill)
            }
            WorkoutType.Walking -> {
                // Simple shoe shape
                val shoe = Path().apply {
                    moveTo(w * 0.2f, h * 0.65f)
                    lineTo(w * 0.55f, h * 0.65f)
                    lineTo(w * 0.7f, h * 0.55f)
                    lineTo(w * 0.8f, h * 0.55f)
                    lineTo(w * 0.8f, h * 0.75f)
                    lineTo(w * 0.2f, h * 0.75f)
                    close()
                }
                drawPath(shoe, color = color)
            }
            WorkoutType.Swimming -> {
                // Waves
                val y1 = h * 0.45f
                val y2 = h * 0.6f
                for (y in listOf(y1, y2)) {
                    val wave = Path().apply {
                        moveTo(w * 0.1f, y)
                        quadraticBezierTo(w * 0.25f, y - h * 0.08f, w * 0.4f, y)
                        quadraticBezierTo(w * 0.55f, y + h * 0.08f, w * 0.7f, y)
                        quadraticBezierTo(w * 0.85f, y - h * 0.08f, w * 0.9f, y)
                    }
                    drawPath(wave, color = color, style = Stroke(width = minDim * 0.08f))
                }
            }
            WorkoutType.Cycling -> {
                // Wheels
                drawCircle(color = color, radius = minDim * 0.18f, center = Offset(w * 0.3f, h * 0.7f), style = Stroke(width = minDim * 0.06f))
                drawCircle(color = color, radius = minDim * 0.18f, center = Offset(w * 0.7f, h * 0.7f), style = Stroke(width = minDim * 0.06f))
                // Frame line
                drawLine(color = color, start = Offset(w * 0.3f, h * 0.7f), end = Offset(w * 0.55f, h * 0.5f), strokeWidth = minDim * 0.06f)
                drawLine(color = color, start = Offset(w * 0.55f, h * 0.5f), end = Offset(w * 0.7f, h * 0.7f), strokeWidth = minDim * 0.06f)
                drawLine(color = color, start = Offset(w * 0.55f, h * 0.5f), end = Offset(w * 0.65f, h * 0.35f), strokeWidth = minDim * 0.06f)
            }
            WorkoutType.Skating -> {
                // Boot
                val boot = Path().apply {
                    moveTo(w * 0.25f, h * 0.45f)
                    lineTo(w * 0.55f, h * 0.45f)
                    lineTo(w * 0.6f, h * 0.6f)
                    lineTo(w * 0.25f, h * 0.6f)
                    close()
                }
                drawPath(boot, color = color)
                // Blade
                drawLine(color = color, start = Offset(w * 0.2f, h * 0.7f), end = Offset(w * 0.7f, h * 0.7f), strokeWidth = minDim * 0.08f)
            }
        }
    }
}

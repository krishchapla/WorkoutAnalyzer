package com.example.workoutanalyzer.ui.screens

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.material3.Button
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.text.input.KeyboardType
import androidx.compose.ui.unit.dp
import com.example.workoutanalyzer.ui.model.User

@Composable
fun ProfileScreen(user: User, onUpdateUser: (User) -> Unit) {
    val (name, setName) = remember(user) { mutableStateOf(user.name) }
    val (age, setAge) = remember(user) { mutableStateOf(user.age.toString()) }
    val (gender, setGender) = remember(user) { mutableStateOf(user.gender) }
    val (height, setHeight) = remember(user) { mutableStateOf(user.heightCm.toString()) }
    val (weight, setWeight) = remember(user) { mutableStateOf(user.weightKg.toString()) }
    val (stride, setStride) = remember(user) { mutableStateOf(user.strideLengthM.toString()) }

    Column(
        modifier = Modifier
            .fillMaxSize()
            .padding(16.dp)
    ) {
        Text("User Profile", style = MaterialTheme.typography.headlineMedium)
        Spacer(Modifier.height(16.dp))

        Card(
            modifier = Modifier.fillMaxWidth(),
            elevation = CardDefaults.cardElevation(defaultElevation = 4.dp)
        ) {
            Column(Modifier.padding(16.dp), verticalArrangement = Arrangement.spacedBy(12.dp)) {
                OutlinedTextField(
                    value = name,
                    onValueChange = setName,
                    label = { Text("Name") },
                    modifier = Modifier.fillMaxWidth()
                )
                OutlinedTextField(
                    value = age,
                    onValueChange = { setAge(it.filter { c -> c.isDigit() }.take(3)) },
                    label = { Text("Age") },
                    modifier = Modifier.fillMaxWidth()
                )
                OutlinedTextField(
                    value = gender,
                    onValueChange = setGender,
                    label = { Text("Gender") },
                    modifier = Modifier.fillMaxWidth()
                )
                Row(horizontalArrangement = Arrangement.spacedBy(8.dp)) {
                    OutlinedTextField(
                        value = height,
                        onValueChange = { setHeight(it.filter { c -> c.isDigit() }.take(3)) },
                        label = { Text("Height (cm)") },
                        modifier = Modifier.weight(1f)
                    )
                    OutlinedTextField(
                        value = weight,
                        onValueChange = { setWeight(it.filter { c -> c.isDigit() }.take(3)) },
                        label = { Text("Weight (kg)") },
                        modifier = Modifier.weight(1f)
                    )
                }
                OutlinedTextField(
                    value = stride,
                    onValueChange = { setStride(it.filter { c -> c.isDigit() || c == '.' }.take(5)) },
                    label = { Text("Stride (m)") },
                    modifier = Modifier.fillMaxWidth()
                )
                Row(Modifier.fillMaxWidth(), horizontalArrangement = Arrangement.End) {
                    Button(onClick = {
                        val upd = User(
                            name = name.ifBlank { "You" },
                            age = age.toIntOrNull() ?: user.age,
                            gender = gender.ifBlank { user.gender },
                            heightCm = height.toIntOrNull() ?: user.heightCm,
                            weightKg = weight.toIntOrNull() ?: user.weightKg,
                            strideLengthM = stride.toDoubleOrNull() ?: user.strideLengthM
                        )
                        onUpdateUser(upd)
                    }) { Text("Save") }
                }
            }
        }

        Spacer(Modifier.height(24.dp))

        Card(
            modifier = Modifier.fillMaxWidth(),
            elevation = CardDefaults.cardElevation(defaultElevation = 4.dp)
        ) {
            Column(Modifier.padding(16.dp)) {
                Text("Current Details", style = MaterialTheme.typography.titleLarge)
                Spacer(Modifier.height(16.dp))
                Text("Name: ${user.name}", style = MaterialTheme.typography.bodyLarge)
                Text("Age: ${user.age}", style = MaterialTheme.typography.bodyLarge)
                Text("Gender: ${user.gender}", style = MaterialTheme.typography.bodyLarge)
                Text("Height: ${user.heightCm} cm", style = MaterialTheme.typography.bodyLarge)
                Text("Weight: ${user.weightKg} kg", style = MaterialTheme.typography.bodyLarge)
                Text("Stride: ${String.format("%.2f", user.strideLengthM)} m", style = MaterialTheme.typography.bodyLarge)
            }
        }
    }
}

package com.rendemen.jagung.ui.screens

import androidx.compose.animation.animateColorAsState
import androidx.compose.animation.core.tween
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.History
import androidx.compose.material.icons.filled.Refresh
import androidx.compose.material.icons.filled.Save
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.input.KeyboardType
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import kotlinx.coroutines.launch
import com.rendemen.jagung.model.CalculationMode
import com.rendemen.jagung.viewmodel.RendemenViewModel

val SeedGreen = Color(0xFF2E7D32)
val TargetBgGreen = Color(0xFFE8F5E9)

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun CalculatorScreen(
    viewModel: RendemenViewModel,
    onNavigateHistory: () -> Unit,
    snackbarHostState: SnackbarHostState
) {
    val coroutineScope = rememberCoroutineScope()

    Scaffold(
        topBar = {
            TopAppBar(
                title = {
                    Text(
                        text = "🌽 Rendemen Jagung",
                        fontWeight = FontWeight.Bold,
                        color = Color.White
                    )
                },
                actions = {
                    IconButton(onClick = onNavigateHistory) {
                        Icon(
                            imageVector = Icons.Default.History,
                            contentDescription = "Riwayat",
                            tint = Color.White
                        )
                    }
                },
                colors = TopAppBarDefaults.topAppBarColors(
                    containerColor = SeedGreen
                )
            )
        },
        snackbarHost = { SnackbarHost(snackbarHostState) }
    ) { innerPadding ->
        Column(
            modifier = Modifier
                .fillMaxSize()
                .padding(innerPadding)
                .verticalScroll(rememberScrollState())
                .padding(16.dp),
            verticalArrangement = Arrangement.spacedBy(16.dp)
        ) {
            // Segmented Button
            SegmentedModeSelector(
                selectedMode = viewModel.mode,
                onModeSelected = { viewModel.onModeSelected(it) }
            )

            // Input 1: Berat Biji Pipil
            InputCard(
                label = "Berat Biji Pipil",
                unit = "gram",
                value = viewModel.pipilInput,
                onValueChange = { viewModel.onPipilChanged(it) },
                isTarget = viewModel.mode == CalculationMode.PIPIL,
                warning = viewModel.pipilWarning
            )

            // Input 2: Berat Total Tongkol
            InputCard(
                label = "Berat Total Tongkol",
                unit = "gram",
                value = viewModel.tongkolInput,
                onValueChange = { viewModel.onTongkolChanged(it) },
                isTarget = viewModel.mode == CalculationMode.TONGKOL,
                warning = viewModel.tongkolError
            )

            // Input 3: Rendemen
            InputCard(
                label = "Rendemen",
                unit = "%",
                value = viewModel.rendemenInput,
                onValueChange = { viewModel.onRendemenChanged(it) },
                isTarget = viewModel.mode == CalculationMode.RENDEMEN,
                warning = viewModel.rendemenError ?: viewModel.rendemenWarning
            )

            // Catatan Opsional
            OutlinedTextField(
                value = viewModel.noteInput,
                onValueChange = { viewModel.onNoteChanged(it) },
                label = { Text("Catatan (opsional)") },
                placeholder = { Text("mis. Batch 1, lahan A") },
                modifier = Modifier.fillMaxWidth(),
                shape = RoundedCornerShape(12.dp)
            )

            // Action Buttons
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.spacedBy(12.dp)
            ) {
                OutlinedButton(
                    onClick = { viewModel.reset() },
                    modifier = Modifier.weight(1f),
                    shape = RoundedCornerShape(12.dp)
                ) {
                    Icon(Icons.Default.Refresh, contentDescription = null)
                    Spacer(Modifier.width(6.dp))
                    Text("Reset")
                }

                Button(
                    onClick = {
                        viewModel.saveRecord(
                        onSuccess = {
                            coroutineScope.launch {
                                snackbarHostState.showSnackbar("Riwayat tersimpan ✓")
                            }
                        },
                        onError = {
                            coroutineScope.launch {
                                snackbarHostState.showSnackbar("Lengkapi dulu nilai yang diperlukan.")
                            }
                        }
                    )
                    },
                    modifier = Modifier.weight(1.5f),
                    shape = RoundedCornerShape(12.dp),
                    colors = ButtonDefaults.buttonColors(containerColor = SeedGreen)
                ) {
                    Icon(Icons.Default.Save, contentDescription = null)
                    Spacer(Modifier.width(6.dp))
                    Text("Simpan ke Riwayat")
                }
            }

            // Bottom Formula Info
            Column(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(top = 12.dp),
                horizontalAlignment = Alignment.CenterHorizontally
            ) {
                Text(
                    text = "Rumus: Rendemen = (Biji Pipil ÷ Berat Tongkol) × 100",
                    fontSize = 12.sp,
                    color = Color.Gray,
                    textAlign = TextAlign.Center
                )
                Text(
                    text = "Pilih yang dihitung, isi 2 nilai lainnya.",
                    fontSize = 12.sp,
                    color = Color.Gray,
                    textAlign = TextAlign.Center
                )
            }
        }
    }
}

@Composable
fun InputCard(
    label: String,
    unit: String,
    value: String,
    onValueChange: (String) -> Unit,
    isTarget: Boolean,
    warning: String?
) {
    val borderColor by animateColorAsState(
        targetValue = if (isTarget) SeedGreen else Color.LightGray,
        animationSpec = tween(150),
        label = "border"
    )
    val bgColor by animateColorAsState(
        targetValue = if (isTarget) TargetBgGreen else Color.Transparent,
        animationSpec = tween(150),
        label = "bg"
    )

    Column(modifier = Modifier.fillMaxWidth()) {
        Card(
            modifier = Modifier
                .fillMaxWidth()
                .border(2.dp, borderColor, RoundedCornerShape(12.dp)),
            shape = RoundedCornerShape(12.dp),
            colors = CardDefaults.cardColors(containerColor = bgColor)
        ) {
            Column(modifier = Modifier.padding(12.dp)) {
                Text(
                    text = label,
                    fontSize = 13.sp,
                    fontWeight = FontWeight.Bold,
                    color = if (isTarget) SeedGreen else Color.Black
                )
                Spacer(Modifier.height(4.dp))
                Row(
                    modifier = Modifier.fillMaxWidth(),
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    TextField(
                        value = value,
                        onValueChange = onValueChange,
                        readOnly = isTarget,
                        placeholder = {
                            Text(if (isTarget) "hasil otomatis" else "0")
                        },
                        keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Decimal),
                        modifier = Modifier.weight(1f),
                        colors = TextFieldDefaults.colors(
                            focusedContainerColor = Color.Transparent,
                            unfocusedContainerColor = Color.Transparent,
                            disabledContainerColor = Color.Transparent
                        ),
                        singleLine = true
                    )
                    Text(
                        text = unit,
                        fontWeight = FontWeight.Medium,
                        color = Color.Gray,
                        modifier = Modifier.padding(end = 8.dp)
                    )
                }
            }
        }
        if (!warning.isNullOrBlank()) {
            Text(
                text = warning,
                color = Color.Red,
                fontSize = 13.sp,
                modifier = Modifier.padding(start = 4.dp, top = 4.dp)
            )
        }
    }
}

@Composable
fun SegmentedModeSelector(
    selectedMode: CalculationMode,
    onModeSelected: (CalculationMode) -> Unit
) {
    Row(
        modifier = Modifier
            .fillMaxWidth()
            .background(Color(0xFFF1F8E9), RoundedCornerShape(24.dp))
            .padding(4.dp)
    ) {
        val modes = listOf(
            CalculationMode.RENDEMEN to "Rendemen",
            CalculationMode.PIPIL to "Pipil",
            CalculationMode.TONGKOL to "Tongkol"
        )
        modes.forEach { (mode, label) ->
            val isSelected = mode == selectedMode
            Button(
                onClick = { onModeSelected(mode) },
                modifier = Modifier.weight(1f),
                shape = RoundedCornerShape(20.dp),
                colors = ButtonDefaults.buttonColors(
                    containerColor = if (isSelected) SeedGreen else Color.Transparent,
                    contentColor = if (isSelected) Color.White else SeedGreen
                ),
                elevation = if (isSelected) ButtonDefaults.buttonElevation(defaultElevation = 2.dp) else null
            ) {
                Text(label, fontWeight = if (isSelected) FontWeight.Bold else FontWeight.Normal)
            }
        }
    }
}

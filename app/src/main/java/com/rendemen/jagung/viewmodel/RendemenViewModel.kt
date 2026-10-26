package com.rendemen.jagung.viewmodel

import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.setValue
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.rendemen.jagung.data.RendemenRepository
import com.rendemen.jagung.model.CalculationMode
import com.rendemen.jagung.model.CalculationRecord
import kotlinx.coroutines.flow.SharingStarted
import kotlinx.coroutines.flow.stateIn
import kotlinx.coroutines.launch
import java.util.Locale

class RendemenViewModel(private val repository: RendemenRepository) : ViewModel() {

    var mode by mutableStateOf(CalculationMode.RENDEMEN)
        private set

    var pipilInput by mutableStateOf("")
        private set

    var tongkolInput by mutableStateOf("")
        private set

    var rendemenInput by mutableStateOf("")
        private set

    var noteInput by mutableStateOf("")
        private set

    // Warnings and Errors
    var tongkolError by mutableStateOf<String?>(null)
        private set
    var rendemenError by mutableStateOf<String?>(null)
        private set
    var pipilWarning by mutableStateOf<String?>(null)
        private set
    var rendemenWarning by mutableStateOf<String?>(null)
        private set

    val history = repository.allHistory.stateIn(
        scope = viewModelScope,
        started = SharingStarted.WhileSubscribed(5000),
        initialValue = emptyList()
    )

    fun onModeSelected(newMode: CalculationMode) {
        mode = newMode
        when (newMode) {
            CalculationMode.RENDEMEN -> rendemenInput = ""
            CalculationMode.PIPIL -> pipilInput = ""
            CalculationMode.TONGKOL -> tongkolInput = ""
        }
        recalculate()
    }

    fun onPipilChanged(value: String) {
        pipilInput = value
        recalculate()
    }

    fun onTongkolChanged(value: String) {
        tongkolInput = value
        recalculate()
    }

    fun onRendemenChanged(value: String) {
        rendemenInput = value
        recalculate()
    }

    fun onNoteChanged(value: String) {
        noteInput = value
    }

    private fun parseDecimal(text: String): Double? {
        if (text.isBlank()) return null
        return text.trim().replace(',', '.').toDoubleOrNull()
    }

    private fun recalculate() {
        val p = parseDecimal(pipilInput)
        val t = parseDecimal(tongkolInput)
        val r = parseDecimal(rendemenInput)

        tongkolError = null
        rendemenError = null
        pipilWarning = null
        rendemenWarning = null

        when (mode) {
            CalculationMode.RENDEMEN -> {
                if (p == null || t == null) {
                    rendemenInput = ""
                    return
                }
                if (t == 0.0) {
                    tongkolError = "Berat tongkol tidak boleh 0."
                    rendemenInput = ""
                    return
                }
                val calcR = (p / t) * 100.0
                rendemenInput = String.format(Locale.US, "%.2f", calcR)
                if (p > t) pipilWarning = "⚠️ Biji pipil lebih berat dari tongkol."
                if (calcR > 100.0) rendemenWarning = "⚠️ Rendemen > 100%"
            }
            CalculationMode.PIPIL -> {
                if (r == null || t == null) {
                    pipilInput = ""
                    return
                }
                if (t == 0.0) {
                    tongkolError = "Berat tongkol tidak boleh 0."
                    pipilInput = ""
                    return
                }
                if (r == 0.0) {
                    rendemenError = "Rendemen tidak boleh 0."
                    pipilInput = ""
                    return
                }
                val calcP = (r / 100.0) * t
                pipilInput = String.format(Locale.US, "%.2f", calcP)
                if (r > 100.0) rendemenWarning = "⚠️ Rendemen > 100%"
                if (calcP > t) pipilWarning = "⚠️ Biji pipil lebih berat dari tongkol."
            }
            CalculationMode.TONGKOL -> {
                if (p == null || r == null) {
                    tongkolInput = ""
                    return
                }
                if (r == 0.0) {
                    rendemenError = "Rendemen tidak boleh 0."
                    tongkolInput = ""
                    return
                }
                val calcT = (p * 100.0) / r
                tongkolInput = String.format(Locale.US, "%.2f", calcT)
                if (r > 100.0) rendemenWarning = "⚠️ Rendemen > 100%"
                if (p > calcT) pipilWarning = "⚠️ Biji pipil lebih berat dari tongkol."
            }
        }
    }

    fun canSave(): Boolean {
        val p = parseDecimal(pipilInput)
        val t = parseDecimal(tongkolInput)
        val r = parseDecimal(rendemenInput)
        return p != null && t != null && r != null && p >= 0.0 && t > 0.0 && r > 0.0
    }

    fun saveRecord(onSuccess: () -> Unit, onError: () -> Unit) {
        val p = parseDecimal(pipilInput)
        val t = parseDecimal(tongkolInput)
        val r = parseDecimal(rendemenInput)
        if (p == null || t == null || r == null || t == 0.0 || r == 0.0) {
            onError()
            return
        }

        viewModelScope.launch {
            val record = CalculationRecord(
                mode = mode,
                pipil = p,
                tongkol = t,
                rendemen = r,
                note = noteInput.ifBlank { null }
            )
            repository.insert(record)
            noteInput = ""
            onSuccess()
        }
    }

    fun reset() {
        pipilInput = ""
        tongkolInput = ""
        rendemenInput = ""
        noteInput = ""
        tongkolError = null
        rendemenError = null
        pipilWarning = null
        rendemenWarning = null
    }

    fun deleteItem(id: String) {
        viewModelScope.launch { repository.delete(id) }
    }

    fun clearAll() {
        viewModelScope.launch { repository.clearAll() }
    }
}

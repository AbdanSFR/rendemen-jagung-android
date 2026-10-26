package com.rendemen.jagung.model

import androidx.room.Entity
import androidx.room.PrimaryKey

enum class CalculationMode {
    RENDEMEN,
    PIPIL,
    TONGKOL
}

@Entity(tableName = "calculation_history")
data class CalculationRecord(
    @PrimaryKey
    val id: String = System.currentTimeMillis().toString(),
    val timestamp: Long = System.currentTimeMillis(),
    val mode: CalculationMode,
    val pipil: Double,
    val tongkol: Double,
    val rendemen: Double,
    val note: String? = null
)

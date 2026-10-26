package com.rendemen.jagung.data

import androidx.room.Dao
import androidx.room.Delete
import androidx.room.Insert
import androidx.room.OnConflictStrategy
import androidx.room.Query
import com.rendemen.jagung.model.CalculationRecord
import kotlinx.coroutines.flow.Flow

@Dao
interface RendemenDao {
    @Query("SELECT * FROM calculation_history ORDER BY timestamp DESC")
    fun getAllHistory(): Flow<List<CalculationRecord>>

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertRecord(record: CalculationRecord)

    @Delete
    suspend fun deleteRecord(record: CalculationRecord)

    @Query("DELETE FROM calculation_history WHERE id = :id")
    suspend fun deleteById(id: String)

    @Query("DELETE FROM calculation_history")
    suspend fun clearAll()
}

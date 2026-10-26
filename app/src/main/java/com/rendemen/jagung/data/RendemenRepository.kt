package com.rendemen.jagung.data

import com.rendemen.jagung.model.CalculationRecord
import kotlinx.coroutines.flow.Flow

class RendemenRepository(private val dao: RendemenDao) {
    val allHistory: Flow<List<CalculationRecord>> = dao.getAllHistory()

    suspend fun insert(record: CalculationRecord) {
        dao.insertRecord(record)
    }

    suspend fun delete(id: String) {
        dao.deleteById(id)
    }

    suspend fun clearAll() {
        dao.clearAll()
    }
}

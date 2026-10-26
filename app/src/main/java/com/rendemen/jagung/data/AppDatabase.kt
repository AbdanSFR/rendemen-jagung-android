package com.rendemen.jagung.data

import android.content.Context
import androidx.room.Database
import androidx.room.Room
import androidx.room.RoomDatabase
import androidx.room.TypeConverter
import androidx.room.TypeConverters
import com.rendemen.jagung.model.CalculationMode
import com.rendemen.jagung.model.CalculationRecord

class Converters {
    @TypeConverter
    fun toMode(value: String): CalculationMode = enumValueOf<CalculationMode>(value)

    @TypeConverter
    fun fromMode(mode: CalculationMode): String = mode.name
}

@Database(entities = [CalculationRecord::class], version = 1, exportSchema = false)
@TypeConverters(Converters::class)
abstract class AppDatabase : RoomDatabase() {
    abstract fun rendemenDao(): RendemenDao

    companion object {
        @Volatile
        private var INSTANCE: AppDatabase? = null

        fun getDatabase(context: Context): AppDatabase {
            return INSTANCE ?: synchronized(this) {
                val instance = Room.databaseBuilder(
                    context.applicationContext,
                    AppDatabase::class.java,
                    "rendemen_jagung.db"
                ).build()
                INSTANCE = instance
                instance
            }
        }
    }
}

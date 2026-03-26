package com.example.habitflow.data.local.database

import android.content.Context
import androidx.room.Database
import androidx.room.Room
import androidx.room.RoomDatabase
import androidx.room.migration.Migration
import androidx.sqlite.db.SupportSQLiteDatabase
import com.example.habitflow.data.local.dao.HabitDao
import com.example.habitflow.data.local.dao.HabitEntryDao
import com.example.habitflow.data.local.entity.HabitEntity
import com.example.habitflow.data.local.entity.HabitEntryEntity

@Database(
    entities = [HabitEntity::class, HabitEntryEntity::class],
    version = 3,
    exportSchema = true
)
abstract class HabitDatabase : RoomDatabase() {
    abstract fun habitDao(): HabitDao
    abstract fun habitEntryDao(): HabitEntryDao

    companion object {
        @Volatile
        private var INSTANCE: HabitDatabase? = null

        fun getInstance(context: Context): HabitDatabase {
            return INSTANCE ?: synchronized(this) {
                INSTANCE ?: Room.databaseBuilder(
                    context = context,
                    klass = HabitDatabase::class.java,
                    name = "habit_database"
                )
                    .addMigrations(MIGRATION_2_3)
                    .build().also { INSTANCE = it }
            }
        }

        @Suppress("MagicNumber")
        val MIGRATION_2_3 = object : Migration(2, 3) {
            override fun migrate(db: SupportSQLiteDatabase) {
                db.execSQL(
                    "ALTER TABLE habit_entries ADD COLUMN updatedAt TEXT NOT NULL DEFAULT '1970-01-01T00:00:00'"
                )
            }
        }


    }
}

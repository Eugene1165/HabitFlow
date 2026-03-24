package com.example.habitflow.integrationTests.migration

import androidx.room.testing.MigrationTestHelper
import androidx.test.ext.junit.runners.AndroidJUnit4
import androidx.test.platform.app.InstrumentationRegistry
import com.example.habitflow.data.local.database.HabitDatabase
import com.example.habitflow.data.local.database.HabitDatabase.Companion.MIGRATION_2_3
import org.junit.Rule
import org.junit.Test
import org.junit.runner.RunWith

@RunWith(AndroidJUnit4::class)
class MigrationTest {

    @get:Rule
    val helper = MigrationTestHelper(
        InstrumentationRegistry.getInstrumentation(),
        HabitDatabase::class.java
    )

    @Test
    fun migrate_2_to_3() {
        helper.createDatabase("test_db", 2)
        helper.runMigrationsAndValidate(
            name = "test_db",
            version = 3,
            validateDroppedTables = true,
            MIGRATION_2_3)
    }
}

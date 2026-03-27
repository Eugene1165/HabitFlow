package com.example.habitflow.tests

import com.example.habitflow.integrationTests.dao.HabitDaoTest
import com.example.habitflow.integrationTests.dao.HabitEntryDaoTest
import com.example.habitflow.integrationTests.migration.MigrationTest
import org.junit.runner.RunWith
import org.junit.runners.Suite

@RunWith(Suite::class)
@Suite.SuiteClasses(
    ArchivedScreenTest::class,
    CalendarScreenTest::class,
    HabitFormTest::class,
    HabitInfoTest::class,
    HabitsListTest::class,
    NavigationTest::class,
    SettingsScreenTest::class,
    StatisticsScreenTest::class,
    HabitDaoTest::class,
    HabitEntryDaoTest::class,
    MigrationTest::class
)
class AllTestsSuite

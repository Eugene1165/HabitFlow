package com.example.habitflow.tests

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
    StatisticsScreenTest::class
)
class AllTestsSuite

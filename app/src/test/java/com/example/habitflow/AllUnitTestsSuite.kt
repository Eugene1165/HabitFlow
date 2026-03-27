package com.example.habitflow

import com.example.habitflow.tests.GetAllHabitsStatisticsUseCaseTest
import com.example.habitflow.tests.GetHabitStatisticsUseCaseTest
import com.example.habitflow.tests.HabitFormViewModelTest
import com.example.habitflow.tests.StatisticsViewModelTest
import com.example.habitflow.tests.ToggleHabitEntryUseCaseTest
import org.junit.runner.RunWith
import org.junit.runners.Suite

@RunWith(Suite::class)
@Suite.SuiteClasses(
    GetAllHabitsStatisticsUseCaseTest::class,
    GetHabitStatisticsUseCaseTest::class,
    HabitFormViewModelTest::class,
    StatisticsViewModelTest::class,
    ToggleHabitEntryUseCaseTest::class,
)
class AllUnitTestsSuite

package com.example.habitflow.tests

import com.example.habitflow.data.mapper.HabitMapper
import com.example.habitflow.domain.model.RepeatType
import com.example.habitflow.fixtures.HabitEntityFactory
import com.example.habitflow.fixtures.HabitFactory
import org.junit.Assert.assertEquals
import org.junit.Test
import java.time.DayOfWeek

class HabitMapperTest {
    private val mapper = HabitMapper()

    @Test
    fun `mapHabitEntityToHabit with repeattype daily`() {
        val habit = HabitEntityFactory.createHabitEntity()

        val result = mapper.mapHabitEntityToHabit(habit)
        val expected = HabitFactory.createHabitDomain()

        assertEquals(expected, result)
    }

    @Test
    fun `mapHabitEntityToHabit with WeeklyDays`() {
        val habitEntity = HabitEntityFactory.createHabitEntity(
            repeatType = "WEEKLY_DAYS",
            repeatDays = "MONDAY,TUESDAY"
        )

        val result = mapper.mapHabitEntityToHabit(habitEntity)
        val expected = HabitFactory.createHabitDomain(
            repeatType = RepeatType.WeeklyDays(listOf(DayOfWeek.MONDAY, DayOfWeek.TUESDAY))
        )

        assertEquals(expected, result)
    }

    @Test
    fun `mapHabitEntityToHabit with WeeklyCount`(){
        val habitEntity = HabitEntityFactory.createHabitEntity(
            repeatCount = 3,
            repeatType = "WEEKLY_COUNT"
        )

        val result = mapper.mapHabitEntityToHabit(habitEntity)
        val expected = HabitFactory.createHabitDomain(
            repeatType = RepeatType.WeeklyCount(3)
        )

        assertEquals(expected,result)
    }

    @Test
    fun `mapHabitToHabitEntity with daily`(){
        val habit = HabitFactory.createHabitDomain()

        val result = mapper.mapHabitToHabitEntity(habit)
        val expected = HabitEntityFactory.createHabitEntity(
            repeatCount = null,
            repeatDays = null
        )

        assertEquals(expected, result)
    }

    @Test
    fun `mapHabitToHabitEntity with WeeklyDays`(){
        val habit = HabitFactory.createHabitDomain(
            repeatType = RepeatType.WeeklyDays(listOf(DayOfWeek.MONDAY, DayOfWeek.TUESDAY)),
        )

        val result = mapper.mapHabitToHabitEntity(habit)
        val expected = HabitEntityFactory.createHabitEntity(
            repeatType = "WEEKLY_DAYS",
            repeatDays = "MONDAY,TUESDAY",
            repeatCount = null
        )

        assertEquals(expected,result)
    }

    @Test
    fun `mapHabitToHabitEntity with WeeklyCount`(){
        val habit = HabitFactory.createHabitDomain(
            repeatType = RepeatType.WeeklyCount(3),
        )

        val result = mapper.mapHabitToHabitEntity(habit)
        val expected = HabitEntityFactory.createHabitEntity(
            repeatType = "WEEKLY_COUNT",
            repeatDays = null,
            repeatCount = 3
        )

        assertEquals(expected,result)

    }


}

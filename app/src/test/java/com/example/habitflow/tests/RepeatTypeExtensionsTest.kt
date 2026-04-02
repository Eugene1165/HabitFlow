package com.example.habitflow.tests

import com.example.habitflow.domain.extensions.toRepeatType
import com.example.habitflow.domain.model.RepeatType
import org.junit.Assert.assertEquals
import org.junit.Assert.assertThrows
import org.junit.Test
import java.time.DayOfWeek

class RepeatTypeExtensionsTest {

    @Test
    fun `Daily with null for repeatDays and repeatcount`() {
        val repeatDays = null
        val repeatCount = null

        val result = "DAILY".toRepeatType(repeatDays, repeatCount)

        assertEquals(RepeatType.Daily, result)
    }

    @Test
    fun `Weekly_days with repeatDays equals days`() {
        val repeatDays = listOf(DayOfWeek.MONDAY, DayOfWeek.TUESDAY).joinToString(",")

        val result = "WEEKLY_DAYS".toRepeatType(repeatDays, null)

        assertEquals(RepeatType.WeeklyDays(listOf(DayOfWeek.MONDAY, DayOfWeek.TUESDAY)), result)
    }

    @Test
    fun `Weekly_days with repeatDays equal null and throw exception`() {
        assertThrows(IllegalArgumentException::class.java) {
            "WEEKLY_DAYS".toRepeatType(
                null,
                2
            )
        }
    }

    @Test
    fun `Weekly_count with repeatCount equal 3`() {
        val result = "WEEKLY_COUNT".toRepeatType(null, 3)
        assertEquals(RepeatType.WeeklyCount(3), result)
    }

    @Test
    fun `Weekly_count with repeatCount equal null and throw exception`() {
        assertThrows(IllegalArgumentException::class.java) {
            "WEEKLY_COUNT".toRepeatType(
                null,
                null
            )
        }
    }
    @Test
    fun `Unknown type`(){
        assertThrows(IllegalArgumentException::class.java){
            "UNKNOWN".toRepeatType(null,null)
        }
    }

}

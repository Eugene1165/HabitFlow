package com.example.habitflow.domain.usecase

import com.example.habitflow.domain.model.HabitEntry
import com.example.habitflow.domain.repository.HabitEntryRepository
import kotlinx.coroutines.flow.Flow
import java.time.LocalDate
import javax.inject.Inject

class GetHabitEntriesForPeriodUseCase @Inject constructor(private val repository: HabitEntryRepository) {
    operator fun invoke(
        habitId: Int,
        startDate: LocalDate,
        endDate: LocalDate
    ): Flow<List<HabitEntry>> {
        return repository.getEntriesForPeriod(habitId, startDate, endDate)
    }
}
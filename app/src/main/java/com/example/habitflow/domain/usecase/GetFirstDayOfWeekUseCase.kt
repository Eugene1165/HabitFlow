package com.example.habitflow.domain.usecase

import com.example.habitflow.domain.repository.UserPreferencesRepository
import kotlinx.coroutines.flow.Flow
import java.time.DayOfWeek
import javax.inject.Inject

class GetFirstDayOfWeekUseCase @Inject constructor(
    private val repository: UserPreferencesRepository
) {
    operator fun invoke(): Flow<DayOfWeek> {
        return repository.getFirstDayOfWeek()
    }
}
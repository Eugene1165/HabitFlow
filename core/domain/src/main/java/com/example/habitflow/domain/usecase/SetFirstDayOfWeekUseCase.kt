package com.example.habitflow.domain.usecase


import com.example.habitflow.domain.repository.UserPreferencesRepository
import java.time.DayOfWeek
import javax.inject.Inject

class SetFirstDayOfWeekUseCase @Inject constructor(
    private val repository: UserPreferencesRepository
) {
    suspend operator fun invoke(day: DayOfWeek){
        return repository.setFirstDayOfWeek(day)
    }
}

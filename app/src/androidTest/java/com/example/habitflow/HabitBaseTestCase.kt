package com.example.habitflow

import android.Manifest
import android.os.Build
import androidx.test.rule.GrantPermissionRule
import com.kaspersky.kaspresso.interceptors.watcher.testcase.StepWatcherInterceptor
import com.kaspersky.kaspresso.kaspresso.Kaspresso
import com.kaspersky.kaspresso.testcases.api.testcase.TestCase
import com.kaspersky.kaspresso.testcases.models.info.StepInfo
import org.junit.Rule

open class HabitBaseTestCase : TestCase(
    kaspressoBuilder = Kaspresso.Builder.simple {
        stepWatcherInterceptors = mutableListOf()
        stepWatcherInterceptors.add(object : StepWatcherInterceptor {
            override fun interceptAfterWithError(
                stepInfo: StepInfo,
                error: Throwable
            ) {
                screenshots.take("step_failed")
            }
        })
    }
) {
    @get:Rule
    val grantPermissionRule: GrantPermissionRule =
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.TIRAMISU) {
            GrantPermissionRule.grant(Manifest.permission.POST_NOTIFICATIONS)
        } else {
            GrantPermissionRule.grant()
        }
}
